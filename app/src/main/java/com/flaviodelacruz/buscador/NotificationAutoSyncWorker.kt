package com.flaviodelacruz.buscador

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection

/**
 * Persistent, idempotent queue: new local notifications schedule a network-constrained
 * job, then wait for Android's scheduler. This never blocks the notification callback.
 * Only records created after the explicit remote-sync opt-in can be uploaded.
 */
class NotificationAutoSyncWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    companion object {
        private const val UNIQUE_NAME = "buscador-consented-notification-sync"

        fun enqueue(context: Context) {
            if (AutoSyncSettings.config(context) == null) return
            val work = OneTimeWorkRequestBuilder<NotificationAutoSyncWorker>()
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(context.applicationContext)
                .enqueueUniqueWork(UNIQUE_NAME, ExistingWorkPolicy.APPEND_OR_REPLACE, work)
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context.applicationContext).cancelUniqueWork(UNIQUE_NAME)
        }
    }

    private fun post(config: AutoSyncConfig, items: List<Map<String, Any>>): Int {
        val target = URL(config.endpoint)
        require(target.protocol == "https") // No HTTP or cross-protocol redirects.
        val connection = target.openConnection() as HttpsURLConnection
        try {
            connection.instanceFollowRedirects = false // Never leak Bearer tokens to redirects.
            connection.requestMethod = "POST"
            connection.connectTimeout = 12_000
            connection.readTimeout = 12_000
            connection.doOutput = true
            connection.setRequestProperty("Authorization", "Bearer " + config.token)
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            connection.setRequestProperty("Accept", "application/json")
            val payload = JSONObject()
            val array = JSONArray()
            items.forEach { item ->
                array.put(JSONObject()
                    .put("id", item["id"] as String)
                    .put("source", item["source"] as String)
                    .put("title", item["title"] as String)
                    .put("message", item["message"] as String)
                    .put("time", (item["time"] as Number).toLong())
                )
            }
            payload.put("items", array)
            connection.outputStream.use {
                it.write(payload.toString().toByteArray(Charsets.UTF_8))
            }
            return connection.responseCode
        } finally {
            connection.disconnect()
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val context = applicationContext
        val config = AutoSyncSettings.config(context) ?: return@withContext Result.success()
        try {
            val acknowledged = AutoSyncSettings.syncedIds(context)
            val pending = NotificationArchiveStore.getRecords(context)
                .filter {
                    (it["time"] as? Number)?.toLong()?.let { time ->
                        time >= config.startedAt && time <= System.currentTimeMillis()
                    } ?: false
                }
                .filter { (it["id"] as? String) !in acknowledged }
                .sortedBy { (it["time"] as Number).toLong() }
            for (batch in pending.chunked(40)) {
                if (isStopped) return@withContext Result.success()
                val current = AutoSyncSettings.config(context) ?: return@withContext Result.success()
                if (current.startedAt != config.startedAt || current.endpoint != config.endpoint)
                    return@withContext Result.success()
                val code = post(config, batch)
                when {
                    code in 200..299 -> {
                        AutoSyncSettings.acknowledge(context, config, batch.map { it["id"] as String })
                        AutoSyncSettings.setStatus(context, "Último lote sincronizado correctamente.")
                    }
                    code == 401 || code == 403 -> {
                        AutoSyncSettings.setStatus(context, "Token rechazado por el servidor. Reconfigura el acceso.")
                        return@withContext Result.failure()
                    }
                    code == 400 || code == 404 -> {
                        AutoSyncSettings.setStatus(context, "La API rechazó los datos. Revisa la configuración.")
                        return@withContext Result.failure()
                    }
                    else -> {
                        AutoSyncSettings.setStatus(context, "Conexión temporalmente no disponible: reintentaremos.")
                        return@withContext Result.retry()
                    }
                }
            }
            Result.success()
        } catch (_: Exception) {
            // Deliberately avoid logging message contents, tokens or full URLs.
            AutoSyncSettings.setStatus(context, "Sin conexión con el servidor. Android reintentará el envío.")
            Result.retry()
        }
    }
}
