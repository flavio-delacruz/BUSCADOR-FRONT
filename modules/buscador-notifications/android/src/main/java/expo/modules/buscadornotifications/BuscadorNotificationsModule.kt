package expo.modules.buscadornotifications

import android.content.ComponentName
import android.content.Intent
import android.provider.Settings
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class BuscadorNotificationsModule : Module() {
  private fun context() =
    appContext.reactContext ?: throw IllegalStateException("Contexto Android no disponible")

  private fun accessGranted(): Boolean {
    val context = context()
    val expected = ComponentName(context, BuscadorNotificationListener::class.java)
    val enabled = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners") ?: ""
    return enabled.split(":").any { ComponentName.unflattenFromString(it) == expected }
  }

  override fun definition() = ModuleDefinition {
    Name("BuscadorNotifications")

    AsyncFunction("getStatus") {
      val context = context()
      mapOf(
        "accessGranted" to accessGranted(),
        "recordingEnabled" to NotificationArchiveStore.isEnabled(context),
        "sources" to NotificationArchiveStore.selectedPackages(context).sorted(),
        "recordsCount" to NotificationArchiveStore.getRecords(context).size
      )
    }

    AsyncFunction("openAccessSettings") {
      val context = context()
      val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(intent)
      true
    }

    AsyncFunction("setRecordingEnabled") { enabled: Boolean ->
      val context = context()
      if (enabled && !accessGranted()) {
        throw IllegalStateException("Primero activa el permiso desde Ajustes de Android")
      }
      if (enabled && NotificationArchiveStore.selectedPackages(context).isEmpty()) {
        throw IllegalStateException("Selecciona al menos una red social")
      }
      NotificationArchiveStore.setEnabled(context, enabled)
      true
    }

    AsyncFunction("setSources") { selected: List<String> ->
      val context = context()
      NotificationArchiveStore.setSelectedPackages(context, selected)
      if (NotificationArchiveStore.selectedPackages(context).isEmpty()) {
        NotificationArchiveStore.setEnabled(context, false)
      }
      true
    }

    AsyncFunction("getRecords") {
      NotificationArchiveStore.getRecords(context())
    }

    AsyncFunction("clearRecords") {
      NotificationArchiveStore.clear(context())
      true
    }
  }
}
