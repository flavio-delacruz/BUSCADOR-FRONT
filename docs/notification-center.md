# Historial voluntario de notificaciones: Android

## Qué se implementó
La app tiene un módulo Android local basado en `NotificationListenerService`.
El usuario debe: (1) abrir el Centro de notificaciones; (2) habilitar el acceso
especial en Ajustes de Android; (3) elegir las redes sociales; (4) activar
**Guardar notificaciones**. Ninguno de estos pasos se realiza automáticamente.

- WhatsApp normal / Business: `com.whatsapp`, `com.whatsapp.w4b`
- Facebook: `com.facebook.katana`
- TikTok: `com.zhiliaoapp.musically`

Solo se guarda el título y el texto de una **notificación nueva** proporcionada
por Android. La disponibilidad depende de la app de origen y de su configuración:
puede no existir vista previa, llegar como resumen o no publicarse una notificación.
No se extraen chats, no se recuperan mensajes anteriores y no se obtiene información
de aplicaciones que no estén en la lista permitida.

## Controles y protección
- Estado inicial desactivado y sin fuentes seleccionadas.
- La app no contiene un servidor que reciba estos datos.
- Archivo local cifrado con AES-GCM y clave no exportable de Android Keystore.
- Máximo 500 notificaciones y hasta 30 días de conservación.
- Detección de publicaciones duplicadas dentro de 8 segundos.
- Opción de detener el guardado y borrar todo el historial local.
- El permiso especial de Android **sigue concedido** cuando se detiene el guardado:
  el usuario puede revocarlo desde Ajustes.
- Sin registro de contenido privado en consola ni analíticas.
- La app no lee lo escrito en otras apps y no modifica los mensajes de origen.

## Prueba real — nueva APK obligatoria
El commit integra código Kotlin por primera vez. No se carga por Metro en la APK
anterior. Para probar en un Android propio, instala una **nueva** development build
de EAS y usa Metro normalmente después.

```bash
cd ~/BUSCADOR/frontend
git pull --ff-only
npm install --no-audit --no-fund
npm run typecheck
npx eas-cli@latest build -p android --profile development
```

Si se instala sobre la app anterior, el paquete Android se mantiene igual.
Si Android muestra un permiso restringido por origen de instalación, revisar
los ajustes de la aplicación y seguir únicamente las indicaciones del sistema.

## Puntos que aún requieren validación de dispositivo
El compilador CI verifica TypeScript, pero no compila Kotlin/Gradle.
Es indispensable validar EAS Build, el registro del servicio en el manifest,
el enlace nativo y el comportamiento de notificaciones reales en Android.
Los fabricantes pueden aplicar políticas de ahorro de batería y permisos especiales.

En todo caso, respetar permiso informado y visible, no ocultar finalidades,
y nunca subir el contenido privado a MongoDB sin una decisión adicional y explícita.
