# Buscador — Android nativo Kotlin

Migración del proyecto Expo/React Native a Android 100 % nativo,
con **Kotlin, Jetpack Compose y NotificationListenerService**.
No requiere Expo, Metro, React Native, Node.js ni EAS para compilar.

## Pantallas
Inicio (Hoy), Radar, Explorar, Guardados, Ajustes, lectura y
Centro de notificaciones, con identidad rojo/negro original.

## Notificaciones con permiso explícito
En el primer inicio, la app explica su alcance y solicita al usuario
abrir **Acceso a notificaciones** de Android. El servicio de Android
empieza a registrar solo notificaciones nuevas de WhatsApp, Facebook y
TikTok después de que el usuario acepte. No solicita accesibilidad,
no registra teclado, no obtiene conversaciones antiguas, no envía mensajes
a ningún servidor ni contiene SDK de analítica.

El historial utiliza AES-256-GCM con clave protegida por Android Keystore;
se conservan como máximo 500 registros durante 30 días. La app permite
detener la captura, cambiar fuentes y borrar el archivo.

## Compilación
Android Studio: abrir este directorio como proyecto Gradle e instalar el SDK 35.
Desde terminal con Gradle 8.11.1, JDK 17 y Android SDK configurados:

```sh
gradle :app:assembleDebug
```

Para obtener una APK sin Android Studio ni EAS:
GitHub Actions → **Kotlin Android APK** → Artifacts →
**Buscador-Kotlin-Debug-APK**.

## Importante sobre la instalación
Se conserva el applicationId `com.flaviodelacruz.buscador`, pero la APK
de GitHub Actions se firma con una clave de depuración distinta de la
firma de Expo. Android no permite actualizar la app instalada con otra
firma. No la desinstales si necesitas conservar el archivo local, ya
que no existe exportación de mensajes cifrados.

La versión React Native anterior está preservada en la rama
`backup/expo-sdk58-2026-11-17`.
