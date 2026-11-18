package com.flaviodelacruz.buscador

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import org.json.JSONArray
import org.json.JSONObject
import java.net.URI
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.util.Base64

/**
 * Private, device-local configuration for *explicitly authorized* automatic uploads.
 * No remote synchronization is enabled by granting notification listener access alone.
 */
internal data class AutoSyncConfig(
    val endpoint: String,
    val token: String,
    val startedAt: Long
)

internal object AutoSyncSettings {
    private const val PREFS = "buscador_remote_sync_v1"
    private const val ALIAS = "buscador_auto_sync_key_v1"
    private const val MAX_IDS = 500
    private val lock = Any()

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun validateBaseUrl(value: String): String {
        val uri = URI(value.trim().trimEnd('/'))
        require(uri.scheme.equals("https", ignoreCase = true) &&
            !uri.host.isNullOrBlank() &&
            uri.rawUserInfo == null && uri.rawQuery == null &&
            uri.rawFragment == null && (uri.path.isNullOrEmpty() || uri.path == "/") &&
            (uri.port == -1 || uri.port == 443)) {
            "El servidor debe usar HTTPS con una URL base válida."
        }
        return "https://" + uri.host.lowercase() + "/api/notifications/sync"
    }

    private fun key(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore")
        store.load(null)
        val existing = store.getKey(ALIAS, null)
        if (existing is SecretKey) return existing
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(KeyGenParameterSpec.Builder(
            ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build())
        return generator.generateKey()
    }

    private fun protect(text: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key())
        val bytes = byteArrayOf(1) + cipher.iv + cipher.doFinal(text.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun unprotect(encoded: String): String {
        val bytes = Base64.decode(encoded, Base64.NO_WRAP)
        require(bytes.size > 29 && bytes[0].toInt() == 1)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, bytes.copyOfRange(1, 13)))
        return String(cipher.doFinal(bytes.copyOfRange(13, bytes.size)), Charsets.UTF_8)
    }

    // Consent is a separate local flag, unrelated to Android's notification permission.
    fun isEnabled(context: Context): Boolean =
        prefs(context).getBoolean("enabled", false)

    fun config(context: Context): AutoSyncConfig? = synchronized(lock) {
        if (!isEnabled(context)) return@synchronized null
        val encrypted = prefs(context).getString("encrypted_config", null) ?: return@synchronized null
        try {
            val json = JSONObject(unprotect(encrypted))
            AutoSyncConfig(
                json.getString("endpoint"),
                json.getString("token"),
                json.getLong("startedAt")
            )
        } catch (_: Exception) {
            null // Fail closed if the Keystore or encrypted configuration is unavailable.
        }
    }

    fun enable(context: Context, baseUrl: String, bearerToken: String) {
        val endpoint = validateBaseUrl(baseUrl)
        val token = bearerToken.trim()
        require(token.length in 32..256 && token.matches(Regex("[A-Za-z0-9._~+/=-]+"))) {
            "Token inválido."
        }
        synchronized(lock) {
            // Only future notifications are eligible: never backfill the old archive.
            val json = JSONObject()
                .put("endpoint", endpoint)
                .put("token", token)
                .put("startedAt", System.currentTimeMillis())
            val stored = protect(json.toString())
            check(prefs(context).edit()
                .putString("encrypted_config", stored)
                .putBoolean("enabled", true)
                .remove("synced_ids")
                .remove("last_status")
                .commit()) { "No se pudo guardar la autorización." }
        }
    }

    fun disable(context: Context) {
        synchronized(lock) {
            check(prefs(context).edit()
                .putBoolean("enabled", false)
                .remove("encrypted_config")
                .remove("synced_ids")
                .remove("last_status")
                .commit()) { "No se pudo desactivar la sincronización." }
        }
    }

    fun syncedIds(context: Context): Set<String> = synchronized(lock) {
        prefs(context).getStringSet("synced_ids", emptySet())?.toSet() ?: emptySet()
    }

    fun acknowledge(context: Context, configuration: AutoSyncConfig, ids: List<String>) {
        synchronized(lock) {
            // Never mark a record as sent if remote-sync consent has been revoked/replaced.
            val current = config(context) ?: return
            if (current.startedAt != configuration.startedAt ||
                current.endpoint != configuration.endpoint) return
            val known = prefs(context).getStringSet("synced_ids", emptySet())?.toMutableSet()
                ?: mutableSetOf()
            known.addAll(ids)
            // The local archive holds at most 500 records.
            if (known.size > MAX_IDS) {
                val keep = known.takeLast(MAX_IDS).toSet()
                known.clear()
                known.addAll(keep)
            }
            prefs(context).edit().putStringSet("synced_ids", known).commit()
        }
    }

    fun status(context: Context): String = prefs(context).getString("last_status", "") ?: ""

    fun setStatus(context: Context, value: String) {
        prefs(context).edit().putString("last_status", value.take(160)).apply()
    }
}
