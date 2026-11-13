package expo.modules.buscadornotifications

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

/**
 * User-controlled local backup of notification previews, not access to chats.
 * Only supported source packages are considered; no network or telemetry.
 */
class BuscadorNotificationListener : NotificationListenerService() {
  companion object {
    @Volatile var connected = false
      private set
  }

  override fun onListenerConnected() {
    super.onListenerConnected()
    connected = true
    // Only activate the first-run archive if the user already pressed
    // "Aceptar y configurar acceso", chose sources, and Android actually
    // bound this service after the special access was granted.
    try {
      if (NotificationArchiveStore.setupPending(this) &&
          !NotificationArchiveStore.setupFinished(this) &&
          NotificationArchiveStore.selectedPackages(this).isNotEmpty()) {
        NotificationArchiveStore.setEnabled(this, true)
        NotificationArchiveStore.setSetupFinished(this, true)
      }
    } catch (_: Exception) {
      // Do not handle or log private notification content here.
    }
  }

  override fun onListenerDisconnected() {
    connected = false
    super.onListenerDisconnected()
  }

  override fun onDestroy() {
    connected = false
    super.onDestroy()
  }

  override fun onNotificationPosted(sbn: StatusBarNotification?) {
    if (sbn == null || !NotificationArchiveStore.isEnabled(this)) return
    if (!NotificationArchiveStore.selectedPackages(this).contains(sbn.packageName)) return

    val notification = sbn.notification
    if ((notification.flags and Notification.FLAG_GROUP_SUMMARY) != 0) return
    val extras = notification.extras ?: return
    val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
    // MessagingStyle is commonly used by chat apps. Prefer the newest message
    // preview over a generic "multiple new messages" summary.
    val messagingPreview = if (android.os.Build.VERSION.SDK_INT >= 30) {
      try {
        Notification.MessagingStyle.Message.getMessagesFromBundleArray(
          extras.getParcelableArray(Notification.EXTRA_MESSAGES)
        ).lastOrNull()?.text?.toString()
      } catch (_: Exception) {
        null
      }
    } else {
      null
    }
    val body = sequenceOf(
      messagingPreview,
      extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString(),
      extras.getCharSequence(Notification.EXTRA_TEXT)?.toString(),
      extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)?.lastOrNull()?.toString()
    ).firstOrNull { !it.isNullOrBlank() } ?: ""

    if (body.isBlank()) return
    try {
      NotificationArchiveStore.append(this, sbn.packageName, title, body)
    } catch (_: Exception) {
      // Fail closed: never publish private content to logs if storage fails.
    }
  }
}
