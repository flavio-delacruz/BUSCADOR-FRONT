# Centro de notificaciones — alcance y privacidad

## Estado

Interfaz y especificación preparadas. **No se ha implementado captura nativa, acceso a notificaciones ni transmisión a un servidor.**

## Objetivo autorizado

Historial personal y voluntario en Android con fuentes seleccionadas por el dueño del dispositivo:

- WhatsApp: com.whatsapp (también podría existir la variante Business)
- Facebook: com.facebook.katana
- TikTok: com.zhiliaoapp.musically

Los identificadores pueden variar entre versiones o distribuciones. La lista por sí sola no concede permisos.

## Modelo propuesto

1. Pantalla visible que detalle qué permiso se pide y para qué.
2. Usuario abre el ajuste del sistema y concede explícitamente acceso a notificaciones a Buscador.
3. Control independiente dentro de Buscador para activar/desactivar el historial y elegir fuentes.
4. Solo eventos nuevos y visibles para Android, sin recuperar mensajes pasados.
5. Registro local cifrado, sin conexión automática con MongoDB para contenido privado.
6. Historial visible al usuario, caducidad configurable y borrado total accesible.
7. Comprobar que el permiso esté concedido antes de mostrar estado activo.
8. Pruebas en dispositivo propio y documentación de límites del sistema.

## Tecnología

Android ofrece `NotificationListenerService` para notificaciones cuando el usuario concede acceso. Requiere un componente nativo y una nueva compilación APK con EAS. No es una función disponible por añadir únicamente JavaScript a Metro.

## Restricciones

No usar captura silenciosa, credenciales de otras personas, ocultamiento de finalidad, recolección de teclas ni envío de mensajes privados sin una explicación clara y consentimiento específico.

Consultar la documentación oficial de Android:

https://developer.android.com/reference/android/service/notification/NotificationListenerService
