package expo.modules.buscadornotifications

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

/**
 * User-controlled local backup of notification previews, not access to chats.
 * Only supported source packages are considered; no network or telemetry.
 */
class BuscadorNotificationListener : NotificationListenerService() {
  override fun onNotificationPosted(sbn: StatusBarNotification?) {
    if (sbn == null || !NotificationArchiveStore.isEnabled(this)) return
    if (!NotificationArchiveStore.selectedPackages(this).contains(sbn.packageName)) return

    val notification = sbn.notification
    if ((notification.flags and Notification.FLAG_GROUP_SUMMARY) != 0) return
    val extras = notification.extras ?: return
    val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
    val body = (
      extras.getCharSequence(Notification.EXTRA_BIG_TEXT)
        ?: extras.getCharSequence(Notification.EXTRA_TEXT)
      )?.toString() ?: ""

    if (body.isBlank()) return
    try {
      NotificationArchiveStore.append(this, sbn.packageName, title, body)
    } catch (_: Exception) {
      // Fail closed: never publish private content to logs if storage fails.
    }
  }
}
