# BUSCADOR-FRONT

Buscador es un explorador editorial de ideas y contenido de muestra, realizado
con React Native, Expo Router y TypeScript para Android.

## Pantallas
- **Hoy**, **Radar**, **Explorar**, **Guardados**: contenido editorial y búsqueda local.
- **Ajustes**: privacidad y acceso al Centro de notificaciones.
- **Centro de notificaciones**: acceso especial de Android, selección de fuentes,
  activación/desactivación, historial local y borrado total.
- **Detalle** y **Briefing**: lectura editorial de ejemplo.

El catálogo de noticias es ficticio: no muestra actualidad real.

## Historial autorizado de notificaciones

El nuevo módulo Android (Kotlin) puede almacenar **solo nuevas notificaciones**
que el sistema muestre procedentes de WhatsApp, Facebook y TikTok. No accede
directamente a chats, no restaura mensajes en esas apps y no recupera contenido
anterior a su activación. Se habilita únicamente tras seleccionar las fuentes,
dar permiso de Android y activar el guardado en la app. El historial permanece
cifrado localmente (AES-GCM / Android Keystore), máximo 500 registros/30 días.
No se envía a MongoDB ni a otros servicios.

Documentación técnica: [docs/notification-center.md](docs/notification-center.md)

## Instalar actualización **nativa** (nueva APK)

La APK anterior solo ejecuta la interfaz JavaScript. Tras incorporar el módulo
Kotlin, debes generar e instalar otra APK una vez:

```bash
cd ~/BUSCADOR/frontend
git pull --ff-only
npm install --no-audit --no-fund
npm run typecheck
EAS_SKIP_AUTO_FINGERPRINT=1 npx eas-cli@latest build -p android --profile development
```

**El typecheck de GitHub revisa TypeScript, no Kotlin.** El módulo nativo
todavía requiere comprobar que el nuevo EAS Build compile y funcione en
un Android real.

Después de instalar la APK nueva, la edición habitual de TS/JS sigue usando:

```bash
npx expo start --dev-client --localhost -c
```

## Privacidad
El permiso de acceso a notificaciones es amplio a nivel de Android, por eso
se filtra y conserva únicamente el texto de notificaciones de fuentes elegidas.
Al detener el registro, Android puede conservar el permiso concedido:
el usuario también puede revocarlo en los Ajustes del dispositivo.
No se registra lo escrito en otras aplicaciones.
