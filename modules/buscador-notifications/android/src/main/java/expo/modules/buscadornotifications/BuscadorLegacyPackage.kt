package expo.modules.buscadornotifications

import android.content.ComponentName
import android.content.Intent
import android.provider.Settings
import android.service.notification.NotificationListenerService
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.uimanager.ViewManager

/**
 * An explicitly registered React Native bridge, independent of Expo's
 * dynamic module provider. All notification content stays in Android storage.
 */
class BuscadorLegacyModule(
  private val reactCtx: ReactApplicationContext
) : ReactContextBaseJavaModule(reactCtx) {
  override fun getName() = "BuscadorNotificationsLegacy"

  private fun accessGranted(): Boolean {
    val target = ComponentName(reactCtx, BuscadorNotificationListener::class.java)
    val enabled = Settings.Secure.getString(reactCtx.contentResolver, "enabled_notification_listeners") ?: ""
    return enabled.split(":").any { ComponentName.unflattenFromString(it) == target }
  }

  private fun listenerRegistered(): Boolean {
    return try {
      reactCtx.packageManager.getServiceInfo(
        ComponentName(reactCtx, BuscadorNotificationListener::class.java), 0
      )
      true
    } catch (_: Exception) { false }
  }

  @ReactMethod
  fun getStatus(promise: Promise) {
    try {
      val result = Arguments.createMap()
      result.putBoolean("accessGranted", accessGranted())
      result.putBoolean("serviceRegistered", listenerRegistered())
      result.putBoolean("serviceConnected", BuscadorNotificationListener.connected)
      result.putBoolean("recordingEnabled", NotificationArchiveStore.isEnabled(reactCtx))
      result.putBoolean("setupFinished", NotificationArchiveStore.setupFinished(reactCtx))
      result.putBoolean("setupPending", NotificationArchiveStore.setupPending(reactCtx))
      result.putString("nativeBuild", "1.1.1-legacy-bridge")
      val sources = Arguments.createArray()
      NotificationArchiveStore.selectedPackages(reactCtx).sorted().forEach { sources.pushString(it) }
      result.putArray("sources", sources)
      result.putInt("recordsCount", NotificationArchiveStore.getRecords(reactCtx).size)
      promise.resolve(result)
    } catch (e: Exception) {
      promise.reject("STATUS_FAILED", "No se pudo leer el estado nativo", e)
    }
  }

  @ReactMethod
  fun setSetupPending(pending: Boolean, promise: Promise) {
    try {
      NotificationArchiveStore.setSetupPending(reactCtx, pending)
      promise.resolve(true)
    } catch (e: Exception) {
      promise.reject("SETUP_PENDING_FAILED", "No se pudo guardar la solicitud de permiso", e)
    }
  }

  @ReactMethod
  fun setSetupFinished(finished: Boolean, promise: Promise) {
    try {
      NotificationArchiveStore.setSetupFinished(reactCtx, finished)
      promise.resolve(true)
    } catch (e: Exception) {
      promise.reject("SETUP_FINISH_FAILED", "No se pudo terminar la configuración", e)
    }
  }

  @ReactMethod
  fun openAccessSettings(promise: Promise) {
    try {
      val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      reactCtx.startActivity(intent)
      promise.resolve(true)
    } catch (e: Exception) {
      promise.reject("SETTINGS_FAILED", "No fue posible abrir el permiso especial", e)
    }
  }

  @ReactMethod
  fun setRecordingEnabled(enabled: Boolean, promise: Promise) {
    try {
      if (enabled && !accessGranted()) {
        promise.reject("ACCESS_REQUIRED", "Debes autorizar el acceso a notificaciones en Android")
        return
      }
      if (enabled && NotificationArchiveStore.selectedPackages(reactCtx).isEmpty()) {
        promise.reject("SOURCES_REQUIRED", "Selecciona al menos una aplicación")
        return
      }
      NotificationArchiveStore.setEnabled(reactCtx, enabled)
      if (enabled && !BuscadorNotificationListener.connected) {
        NotificationListenerService.requestRebind(
          ComponentName(reactCtx, BuscadorNotificationListener::class.java)
        )
      }
      promise.resolve(true)
    } catch (e: Exception) {
      promise.reject("RECORDING_FAILED", "No fue posible cambiar el estado del historial", e)
    }
  }

  @ReactMethod
  fun setSources(selected: ReadableArray, promise: Promise) {
    try {
      val packages = buildList {
        for (i in 0 until selected.size()) selected.getString(i)?.let { add(it) }
      }
      NotificationArchiveStore.setSelectedPackages(reactCtx, packages)
      if (NotificationArchiveStore.selectedPackages(reactCtx).isEmpty()) {
        NotificationArchiveStore.setEnabled(reactCtx, false)
      }
      promise.resolve(true)
    } catch (e: Exception) {
      promise.reject("SOURCES_FAILED", "No fue posible configurar las aplicaciones", e)
    }
  }

  @ReactMethod
  fun getRecords(promise: Promise) {
    try {
      val records = Arguments.createArray()
      NotificationArchiveStore.getRecords(reactCtx).forEach { item ->
        val record = Arguments.createMap()
        record.putString("id", item["id"] as String)
        record.putString("source", item["source"] as String)
        record.putString("title", item["title"] as String)
        record.putString("message", item["message"] as String)
        record.putDouble("time", (item["time"] as Number).toDouble())
        records.pushMap(record)
      }
      promise.resolve(records)
    } catch (e: Exception) {
      promise.reject("RECORDS_FAILED", "No se pudo leer el archivo cifrado", e)
    }
  }

  @ReactMethod
  fun clearRecords(promise: Promise) {
    try {
      NotificationArchiveStore.clear(reactCtx)
      promise.resolve(true)
    } catch (e: Exception) {
      promise.reject("CLEAR_FAILED", "No fue posible borrar el historial", e)
    }
  }
}

class BuscadorLegacyPackage : ReactPackage {
  override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> =
    listOf(BuscadorLegacyModule(reactContext))

  override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> =
    emptyList()
}
