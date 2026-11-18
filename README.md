# Buscador — Android nativo Kotlin

Migración del proyecto Expo/React Native a Android 100 % nativo,
con **Kotlin, Jetpack Compose y NotificationListenerService**.
No requiere Expo, Metro, React Native, Node.js ni EAS para compilar.

## Seis pantallas originales NYVARA y login de demostración

Este APK **Buscador** reproduce en **Kotlin + Jetpack Compose** las seis pantallas
originales del onboarding de NYVARA MARKET. Los textos y recursos visuales
se obtuvieron del repositorio NYVARA, **sin modificar un solo archivo allí**:

1. **Un mundo de opciones, un solo lugar** — logo original NYVARA animado.
2. **Lo que necesitas, a tu alcance** — moda / hogar, ilustración NYVARA.
3. **Compra de forma más inteligente** — detalles / precios, ilustración NYVARA.
4. **Encuentra ese detalle especial** — regalos / novedades.
5. **Tus actividades pueden sumar XP** — presentación visual del Club NYVARA XP.
6. **Usa tus XP para ahorrar** — presentación visual de beneficios XP.

El onboarding utiliza la paleta vino/rosa/blanco, tarjetas flotantes, títulos
serif, seis puntos de progreso, botón Atrás/Continuar y revelación del logo de
izquierda a derecha. La imagen de la marca **NYVARA** y el icono Android
provienen directamente de los archivos oficiales de su repositorio.

Tras la sexta pantalla aparece un **login de muestra de NYVARA**.
Pulsar **Continuar con Google** / **Registrarme con Google** solo muestra
un diálogo: **"Falló. Contacte al desarrollador."** La interfaz no hace
peticiones de autenticación, no crea sesión y **no navega** al pulsar ese botón.

Para conservar la utilidad de captura de Buscador sin iniciar sesión,
existe el acceso separado "Explorar sin iniciar sesión" al modo local.
Ese acceso NO concede acceso a notificaciones ni activa sincronización:
el usuario debe autorizarlos en su propia pantalla.

La app instalada sigue llamándose **Buscador**, pero usa logo e icono
oficiales de **NYVARA**. El backend de Buscador no se modifica.
Las funciones existentes de la app permanecen en sus módulos separados.

## Notificaciones con permiso explícito
En el primer inicio, la app explica su alcance y solicita al usuario
abrir **Acceso a notificaciones** de Android. El servicio de Android
empieza a registrar solo notificaciones nuevas de WhatsApp, Facebook y
TikTok después de que el usuario acepte. No solicita accesibilidad,
no registra teclado, no obtiene conversaciones antiguas y no contiene SDK de analítica.
El envío remoto está desactivado hasta que el usuario lo autorice por separado.

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
**Buscador** → descarga el ZIP, extrae **Buscador.apk**.

## Importante sobre la instalación
La compilación **debug** utiliza el paquete
`com.flaviodelacruz.buscador.nyvara` y aparece como **Buscador**:
puede instalarse junto a la app anterior de Expo y la APK Kotlin de prueba sin desinstalarlas. La nueva instalación comienza con un archivo local vacío y debes autorizar su propio acceso a notificaciones.
La compilación de **producción** conserva el applicationId
`com.flaviodelacruz.buscador`, pero la APK
de GitHub Actions se firma con una clave de depuración distinta de la
firma de Expo. Android no permite actualizar la app instalada con otra
firma. No la desinstales si necesitas conservar el archivo local, ya
que no existe exportación de mensajes cifrados.

La versión React Native anterior está preservada en la rama
`backup/expo-sdk58-2026-11-17`.

## Sincronización automática con autorización única

El backend propio `BUSCADOR-BACK` expone `POST /api/notifications/sync`
y requiere MongoDB, token privado y clave AES-256-GCM.

En el Centro de notificaciones, se configura UNA vez la URL base HTTPS, el
token de acceso y una casilla que explica que nuevas notificaciones,
incluido remitente y texto, se enviarán a un servidor remoto.
El interruptor está **apagado por defecto**; el permiso de Android para
leer notificaciones **no** implica autorización para subirlas a Internet.

Después de activar el interruptor, cada nueva notificación local se agrega
a una cola de WorkManager que intenta enviarla por HTTPS con conectividad de
red. El servidor deduplica por ID. Si no hay conexión, WorkManager reintenta
cuando Android lo permite (sin garantía de entrega inmediata o absoluta).
Solo se exportan registros creados después del consentimiento de subida;
los anteriores NO se envían automáticamente.

El token se guarda cifrado mediante Android Keystore para permitir el trabajo
en segundo plano y se borra al apagar el interruptor. Se puede detener la
sincronización remota en cualquier momento, sin borrar ni detener el historial
local. La eliminación de copias ya enviadas al servidor requiere usar el
endpoint privado DELETE del backend.

No incrustes un token global en una APK pública. Esta autenticación es para
un único propietario y debe reemplazarse por cuentas y credenciales revocables
en una aplicación distribuida. No subas notificaciones ajenas sin autorización.

**Firma de prueba:** las compilaciones de GitHub Actions pueden usar certificados
`debug` diferentes. No desinstales tus APK anteriores si quieres conservar sus
notificaciones. Este sufijo de prueba evita sobreescribirlas; para actualizaciones
estables necesitamos una clave de firma persistente y protegida.

## Compatibilidad y diseño de la nueva prueba

El APK de prueba `com.flaviodelacruz.buscador.nyvara` se instala junto a
versiones anteriores con sus propias notificaciones y permisos; no sobrescribe
el archivo cifrado de otras instalaciones. La versión de producción mantiene
`com.flaviodelacruz.buscador` pero requiere firma estable para actualizarse.
El motor `BuscadorNotificationListener`, el almacenamiento AES-GCM,
WorkManager y las rutas de MongoDB permanecen intactos. El login de la
interfaz nueva es intencionalmente de demostración y no tiene autenticación.
