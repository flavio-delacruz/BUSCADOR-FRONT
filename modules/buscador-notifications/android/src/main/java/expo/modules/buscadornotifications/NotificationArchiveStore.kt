package expo.modules.buscadornotifications

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * The archive stays on this device. The Android Keystore key is not exportable.
 * Never send notification content to a backend or analytics provider.
 */
internal object NotificationArchiveStore {
  private val lock = Any()
  private const val preferencesName = "buscador_notifications_v1"
  private const val archiveName = "notifications_v1.enc"
  private const val keyAlias = "buscador_notification_archive_aes_v1"
  private const val maxEntries = 500
  private const val lifetimeMillis = 30L * 24L * 60L * 60L * 1000L

  val packages = setOf("com.whatsapp", "com.whatsapp.w4b", "com.facebook.katana", "com.zhiliaoapp.musically")

  private fun preferences(context: Context) =
    context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)

  fun isEnabled(context: Context): Boolean =
    preferences(context).getBoolean("recording_enabled", false)

  fun setEnabled(context: Context, enabled: Boolean) {
    preferences(context).edit().putBoolean("recording_enabled", enabled).apply()
  }

  fun selectedPackages(context: Context): Set<String> {
    return preferences(context).getStringSet("selected_sources", emptySet())
      ?.filter { packages.contains(it) }?.toSet() ?: emptySet()
  }

  fun setSelectedPackages(context: Context, selected: List<String>) {
    val validated = selected.filter { packages.contains(it) }.toSet()
    // A changed selection is fully persisted before the UI can activate recording.
    preferences(context).edit().putStringSet("selected_sources", validated).commit()
  }

  private fun key(): SecretKey {
    val store = KeyStore.getInstance("AndroidKeyStore")
    store.load(null)
    val previous = store.getKey(keyAlias, null)
    if (previous is SecretKey) return previous

    val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
    generator.init(
      KeyGenParameterSpec.Builder(
        keyAlias,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
      )
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .setKeySize(256)
        .build()
    )
    return generator.generateKey()
  }

  private fun encrypt(text: String): ByteArray {
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, key())
    return byteArrayOf(1) + cipher.iv + cipher.doFinal(text.toByteArray(Charsets.UTF_8))
  }

  private fun decrypt(bytes: ByteArray): String {
    require(bytes.size > 29 && bytes[0].toInt() == 1) { "Formato de historial desconocido" }
    val iv = bytes.copyOfRange(1, 13)
    val ciphertext = bytes.copyOfRange(13, bytes.size)
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, iv))
    return String(cipher.doFinal(ciphertext), Charsets.UTF_8)
  }

  private fun readArray(context: Context): JSONArray {
    val file = File(context.filesDir, archiveName)
    if (!file.exists()) return JSONArray()
    // Fail closed if the Keystore key is unavailable or contents were corrupted.
    // Never overwrite a file that failed decryption.
    return JSONArray(decrypt(file.readBytes()))
  }

  private fun saveArray(context: Context, array: JSONArray) {
    val target = File(context.filesDir, archiveName)
    val temp = File(context.filesDir, archiveName + ".tmp")
    temp.writeBytes(encrypt(array.toString()))
    if (!temp.renameTo(target)) {
      temp.delete()
      throw IllegalStateException("No fue posible guardar el historial cifrado")
    }
  }

  fun append(context: Context, pkg: String, title: String, message: String) {
    if (!packages.contains(pkg) || !isEnabled(context) || !selectedPackages(context).contains(pkg)) return
    val safeMessage = message.trim().take(2048)
    if (safeMessage.isEmpty()) return
    val safeTitle = title.trim().take(160)
    val now = System.currentTimeMillis()

    synchronized(lock) {
      if (!isEnabled(context) || !selectedPackages(context).contains(pkg)) return
      val current = readArray(context)
      val output = JSONArray()
      val cutoff = now - lifetimeMillis
      for (i in 0 until current.length()) {
        val entry = current.optJSONObject(i) ?: continue
        if (entry.optLong("time", 0L) >= cutoff) output.put(entry)
      }
      val recent = if (output.length() > 0) output.optJSONObject(output.length() - 1) else null
      if (recent != null &&
          recent.optString("source") == pkg &&
          recent.optString("title") == safeTitle &&
          recent.optString("message") == safeMessage &&
          now - recent.optLong("time") < 8000L) {
        return
      }
      val record = JSONObject()
        .put("id", now.toString() + "_" + java.util.UUID.randomUUID().toString())
        .put("source", pkg)
        .put("title", safeTitle)
        .put("message", safeMessage)
        .put("time", now)
      output.put(record)
      val bounded = JSONArray()
      for (index in maxOf(0, output.length() - maxEntries) until output.length()) {
        bounded.put(output.getJSONObject(index))
      }
      saveArray(context, bounded)
    }
  }

  fun getRecords(context: Context): List<Map<String, Any>> {
    synchronized(lock) {
      val array = readArray(context)
      val cutoff = System.currentTimeMillis() - lifetimeMillis
      val results = mutableListOf<Map<String, Any>>()
      for (i in array.length() - 1 downTo 0) {
        val entry = array.optJSONObject(i) ?: continue
        val time = entry.optLong("time")
        if (time < cutoff) continue
        results.add(mapOf(
          "id" to entry.optString("id"),
          "source" to entry.optString("source"),
          "title" to entry.optString("title"),
          "message" to entry.optString("message"),
          "time" to time
        ))
      }
      return results
    }
  }

  fun clear(context: Context) {
    synchronized(lock) {
      val file = File(context.filesDir, archiveName)
      if (file.exists() && !file.delete()) {
        throw IllegalStateException("No se pudo borrar el historial")
      }
    }
  }
}
