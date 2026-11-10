# Instalación real de Buscador (Android)

## Por qué la compilación anterior no bastaba

- El perfil `development` instala un *cliente de desarrollo*. Requiere Metro.
- El perfil `preview` crea una **APK autónoma**, con JS incluido, que abre
  Buscador sin Metro ni Termux.
- El permiso `NotificationListenerService` no es un diálogo ordinario:
  el usuario debe tocar `Abrir ajustes de permiso` y autorizar Buscador
  manualmente en los Ajustes especiales de Android.

## Nueva versión

Versión visible: 1.1.0, icono y splash rojo/negro incorporados.
La pantalla principal muestra `Protege las notificaciones que recibes`.
Esta tarjeta abre un centro de permisos y controles.

## Pasos desde Termux

```bash
cd ~/BUSCADOR/frontend
git pull --ff-only
npm install --no-audit --no-fund
npm run typecheck
EAS_SKIP_AUTO_FINGERPRINT=1 npx eas-cli@latest build -p android --profile preview
```

En la pregunta sobre instalar en emulador responde **no**; EAS
proporcionará un enlace para instalar manualmente el APK en tu teléfono.

Si Android reconoce la misma firma de Expo, instalará la nueva versión
sobre la anterior. Si muestra un error, **no desinstales todavía**:
el historial local podría perderse al desinstalar (no existe sincronización).
La APK tiene desactivadas las copias de seguridad automáticas para
proteger mensajes privados.

## Activación voluntaria

1. Abre Buscador → `Hoy` → `Protege las notificaciones que recibes`.
2. Comprueba `Versión nativa 1.1.0-archive` y `Servicio en el sistema: REGISTRADO`.
3. Pulsa `Abrir ajustes de permiso` y concede acceso en Android.
4. Vuelve a Buscador, selecciona WhatsApp, Facebook, Messenger o TikTok.
5. Activa `Guardar notificaciones`.
6. Recibe **una notificación nueva visible** en tu propio Android.
7. Regresa al centro y pulsa `Actualizar`; el texto debe aparecer en el historial.
8. Verifica `Detener` y `Borrar todo el historial local`.

No hay permiso automático ni recuperación de conversaciones completas.
El módulo no envía el historial a internet y almacena solo eventos nuevos
que Android permita leer, con fecha y origen.

## Si hay problemas

- `Se necesita una nueva APK` significa que el módulo Kotlin no existe en la APK.
- `Servicio no registrado` significa que el manifiesto nativo no declaró el listener.
- `Permiso pendiente` significa que Android no concedió acceso.
- `Permiso concedido`, pero `Listener conectado: NO`: revisar estado del sistema.
- `Registro activado`, pero `0 elementos`: confirmar que la aplicación de
  origen tiene las notificaciones habilitadas y que la vista previa muestra texto.

El splash nativo en development builds puede diferir del de la APK autónoma;
Expo recomienda evaluar la pantalla de arranque en una compilación de release.
