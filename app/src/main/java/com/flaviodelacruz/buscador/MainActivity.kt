package com.flaviodelacruz.buscador

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.service.notification.NotificationListenerService
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext

data class ArchiveSnapshot(
    val accessGranted: Boolean = false,
    val serviceConnected: Boolean = false,
    val recordingEnabled: Boolean = false,
    val setupPending: Boolean = false,
    val setupFinished: Boolean = false,
    val sources: Set<String> = emptySet(),
    val count: Int = 0
)

val defaultPackages = setOf(
    "com.whatsapp", "com.whatsapp.w4b",
    "com.facebook.katana", "com.facebook.lite",
    "com.zhiliaoapp.musically"
)

class MainActivity : ComponentActivity() {
    private var snapshot by mutableStateOf(ArchiveSnapshot())
    private var records by mutableStateOf<List<Map<String, Any>>>(emptyList())
    private var archiveError by mutableStateOf<String?>(null)

    private fun accessGranted(): Boolean {
        val target = ComponentName(this, BuscadorNotificationListener::class.java)
        val listeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners") ?: ""
        return listeners.split(":").any { ComponentName.unflattenFromString(it) == target }
    }

    private fun completePendingConsent() {
        if (accessGranted() &&
            NotificationArchiveStore.setupPending(this) &&
            NotificationArchiveStore.selectedPackages(this).isNotEmpty()) {
            NotificationArchiveStore.setEnabled(this, true)
            NotificationArchiveStore.setSetupFinished(this, true)
        }
    }

    private fun reload() {
        try {
            records = NotificationArchiveStore.getRecords(this)
            snapshot = ArchiveSnapshot(
                accessGranted = accessGranted(),
                serviceConnected = BuscadorNotificationListener.connected,
                recordingEnabled = NotificationArchiveStore.isEnabled(this),
                setupPending = NotificationArchiveStore.setupPending(this),
                setupFinished = NotificationArchiveStore.setupFinished(this),
                sources = NotificationArchiveStore.selectedPackages(this),
                count = records.size
            )
            archiveError = null
        } catch (e: Exception) {
            archiveError = "No se pudo abrir el archivo cifrado. No se borraron los datos."
            // Never automatically overwrite a file that failed decryption.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reload()
        val initial = if (NotificationArchiveStore.setupFinished(this)) "home" else "welcome"
        setContent {
            var page by rememberSaveable { mutableStateOf(initial) }
            var detailId by rememberSaveable { mutableStateOf("1") }
            var previousPage by rememberSaveable { mutableStateOf("home") }
            var favorites by remember { mutableStateOf(setOf<String>()) }

            LaunchedEffect(snapshot.setupFinished) {
                if (snapshot.setupFinished && page == "welcome") page = "notifications"
            }
            BackHandler(page !in listOf("home","radar","search","saved","settings")) {
                page = if (page == "detail") previousPage else "home"
            }

            BuscadorApp(
                page = page,
                status = snapshot,
                records = records,
                error = archiveError,
                favorites = favorites,
                article = articles.firstOrNull { it.id == detailId } ?: articles.first(),
                onPage = { page = it },
                onArticle = { id ->
                    previousPage = if (page == "detail") "home" else page
                    detailId = id
                    page = "detail"
                },
                onFavorite = { id ->
                    favorites = if (id in favorites) favorites - id else favorites + id
                },
                onConsent = { requestCapture() },
                onSkip = {
                    NotificationArchiveStore.setSetupPending(this, false)
                    NotificationArchiveStore.setSetupFinished(this, true)
                    reload()
                    page = "home"
                },
                onOpenAccess = { openAccessSettings() },
                onStopCapture = {
                    NotificationArchiveStore.setEnabled(this, false)
                    reload()
                },
                onSourceChange = { packages, selected ->
                    val updated = snapshot.sources.toMutableSet()
                    if (selected) updated.addAll(packages) else updated.removeAll(packages)
                    NotificationArchiveStore.setSelectedPackages(this, updated.toList())
                    if (updated.isEmpty()) NotificationArchiveStore.setEnabled(this, false)
                    reload()
                },
                onClear = {
                    NotificationArchiveStore.clear(this)
                    reload()
                },
                onRefresh = { reload() }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        try { completePendingConsent() }
        catch (_: Exception) { /* Consent is still visible and retryable. */ }
        reload()
        // Recover pending, explicitly consented remote-sync jobs after returning online.
        NotificationAutoSyncWorker.enqueue(this)
    }

    private fun openAccessSettings() {
        try {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        } catch (_: Exception) {
            archiveError = "Abre Ajustes > Aplicaciones > Acceso especial > Acceso a notificaciones."
        }
    }

    private fun requestCapture() {
        try {
            if (NotificationArchiveStore.selectedPackages(this).isEmpty()) {
                NotificationArchiveStore.setSelectedPackages(this, defaultPackages.toList())
            }
            if (accessGranted()) {
                NotificationArchiveStore.setEnabled(this, true)
                NotificationArchiveStore.setSetupFinished(this, true)
                if (!BuscadorNotificationListener.connected) {
                    NotificationListenerService.requestRebind(
                        ComponentName(this, BuscadorNotificationListener::class.java)
                    )
                }
                reload()
            } else {
                // Only set pending after an explicit user tap.
                NotificationArchiveStore.setSetupFinished(this, false)
                NotificationArchiveStore.setSetupPending(this, true)
                reload()
                openAccessSettings()
            }
        } catch (_: Exception) {
            archiveError = "No se pudo iniciar la captura. Comprueba el permiso de Android."
        }
    }
}
