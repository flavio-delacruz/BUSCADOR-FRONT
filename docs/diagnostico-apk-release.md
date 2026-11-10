# Diagnóstico de módulo de notificaciones en APK preview

## Incidente: JS muestra "Se necesita una nueva APK" aun con el commit correcto

La compilación EAS preview 7210d870-458f-4608-8ab7-d1b0f6f2f69e
utilizó gitCommitHash 744d463, pero `requireOptionalNativeModule("BuscadorNotifications")`
devolvió null. Esto significa que el puente JS no ve el módulo, no necesariamente
que se haya instalado una versión anterior.

En SDK 58, Android activa R8 por defecto en Release. Puede eliminar clases
que el cargador nativo espera encontrar mediante reflexión. Las validaciones
previas comprobaban Kotlin y AndroidManifest **solo en Debug**, así que no
descartaban este problema.

## Corrección de prueba

- Se agrega `expo-build-properties` para desactivar R8/minify y
  resource shrink en la compilación Release mientras se verifica el puente.
- GitHub Actions compila una APK **Release** real y verifica que el APK
  incluya `BuscadorNotificationsModule` en DEX y
  `BuscadorNotificationListener` en AndroidManifest.
- El historial sigue siendo exclusivamente local, por selección voluntaria,
  y conserva sus límites de capacidad y días. El cambio no agrega networking.
- Después de probar la integración en el dispositivo, se podrá reactivar
  R8 con reglas keep explícitas si se requiere optimizar tamaño.

## Validar localmente

```bash
cd ~/BUSCADOR/frontend
git status --short
git pull --ff-only
npm install --no-audit --no-fund
npm run typecheck
EAS_SKIP_AUTO_FINGERPRINT=1 npx eas-cli@latest build -p android --profile preview
```

No reinstales APK mientras no haya comprobación de Release exitosa.
No desinstales la APK antigua ni hagas stash pop sin examinar primero
los cambios locales. El `app.json` local modificado puede sobreescribir
la configuración del plugin si se genera otra EAS Build antes de
resolver la discrepancia.

El permiso especial de Android requiere activación manual y no se otorga
por la instalación. Una vez activo, la app solo puede conservar el
contenido de nuevas notificaciones que Android entregue.
