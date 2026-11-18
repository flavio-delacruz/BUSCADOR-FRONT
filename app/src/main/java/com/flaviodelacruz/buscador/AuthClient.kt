package com.flaviodelacruz.buscador

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import org.json.JSONObject
import java.net.URI
import java.net.URL
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import java.security.KeyStore
import javax.net.ssl.HttpsURLConnection

data class BuscadorSession(
    val email: String,
    val baseUrl: String,
    val token: String,
    val expiresAt: Long
)

internal object AuthSessionStore {
    private const val PREFS = "buscador_account_v1"
    private const val KEY_ALIAS = "buscador_account_session_aes_v1"
    private fun prefs(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun key(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val previous = store.getKey(KEY_ALIAS, null)
        if (previous is SecretKey) return previous
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(KeyGenParameterSpec.Builder(
            KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256).build())
        return generator.generateKey()
    }

    private fun encrypt(text: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key())
        return Base64.encodeToString(
            byteArrayOf(1) + cipher.iv + cipher.doFinal(text.toByteArray(Charsets.UTF_8)),
            Base64.NO_WRAP
        )
    }
    private fun decrypt(value: String): String {
        val bytes = Base64.decode(value, Base64.NO_WRAP)
        require(bytes.size > 29 && bytes[0].toInt() == 1)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, bytes.copyOfRange(1, 13)))
        return String(cipher.doFinal(bytes.copyOfRange(13, bytes.size)), Charsets.UTF_8)
    }
    fun read(ctx: Context): BuscadorSession? {
        return try {
            val raw = prefs(ctx).getString("session", null) ?: return null
            val json = JSONObject(decrypt(raw))
            val session = BuscadorSession(
                json.getString("email"), json.getString("baseUrl"),
                json.getString("token"), json.getLong("expiresAt")
            )
            if (session.expiresAt > System.currentTimeMillis()) session else null
        } catch (_: Exception) {
            null
        }
    }
    fun write(ctx: Context, session: BuscadorSession) {
        val json = JSONObject()
            .put("email", session.email).put("baseUrl", session.baseUrl)
            .put("token", session.token).put("expiresAt", session.expiresAt)
        check(prefs(ctx).edit().putString("session", encrypt(json.toString())).commit())
    }
    fun clear(ctx: Context) {
        prefs(ctx).edit().remove("session").commit()
    }
    fun onboardingDone(ctx: Context): Boolean = prefs(ctx).getBoolean("onboarding_done", false)
    fun markOnboardingDone(ctx: Context) {
        prefs(ctx).edit().putBoolean("onboarding_done", true).commit()
    }
}

/** Auth never touches or enables the private notification listener or remote sync consent. */
internal object AuthClient {
    fun baseUrl(raw: String): String {
        val parsed = URI(raw.trim().trimEnd('/'))
        require(parsed.scheme.equals("https", ignoreCase = true) &&
            !parsed.host.isNullOrBlank() &&
            parsed.rawUserInfo == null && parsed.rawQuery == null &&
            parsed.rawFragment == null && (parsed.path.isNullOrEmpty() || parsed.path == "/") &&
            (parsed.port == -1 || parsed.port == 443)) {
            "Introduce el dominio HTTPS de tu backend."
        }
        return "https://" + parsed.host.lowercase()
    }

    private fun request(base: String, route: String, body: JSONObject?, token: String? = null): JSONObject {
        val connection = URL(base + route).openConnection() as HttpsURLConnection
        try {
            connection.instanceFollowRedirects = false
            connection.connectTimeout = 12000
            connection.readTimeout = 12000
            connection.requestMethod = if (body == null) "GET" else "POST"
            connection.setRequestProperty("Accept", "application/json")
            if (token != null) connection.setRequestProperty("Authorization", "Bearer $token")
            if (body != null) {
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                connection.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
            }
            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val response = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: "{}"
            if (code !in 200..299) {
                val message = try { JSONObject(response).optString("error") } catch (_: Exception) { "" }
                throw IllegalStateException(
                    if (code == 503) "Servidor sin MongoDB. Configura el backend."
                    else if (code == 429) "Demasiados intentos. Prueba más tarde."
                    else if (message.isNotBlank()) message
                    else "No se pudo acceder al servidor (HTTP $code)."
                )
            }
            return JSONObject(response)
        } finally {
            connection.disconnect()
        }
    }

    fun authenticate(url: String, email: String, password: String, registering: Boolean): BuscadorSession {
        val base = baseUrl(url)
        val payload = JSONObject().put("email", email.trim().lowercase()).put("password", password)
        val result = request(base, if (registering) "/api/auth/register" else "/api/auth/login", payload)
        val user = result.getJSONObject("user")
        val expires = java.time.Instant.parse(result.getString("expiresAt")).toEpochMilli()
        return BuscadorSession(
            user.getString("email"), base, result.getString("token"), expires
        )
    }

    fun logout(session: BuscadorSession) {
        request(session.baseUrl, "/api/auth/logout", JSONObject(), session.token)
    }
}
