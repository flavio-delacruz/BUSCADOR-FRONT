# BUSCADOR-FRONT

**Buscador**, aplicación Android de demostración para explorar contenido, filtrarlo y guardar favoritos. React Native + TypeScript + Expo Router.

## Pantallas

- Inicio con identidad visual negra y roja
- Buscar con filtrado por texto y categorías
- Guardados (memoria de sesión)
- Ajustes con información transparente de privacidad
- Detalle de cada recurso

El catálogo de esta fase es ficticio, local y no contiene información privada. **No hay un lector de notificaciones ni teclado implementado**.

## Abrir en Termux

```bash
pkg install nodejs-lts git -y
git clone https://github.com/flavio-delacruz/BUSCADOR-FRONT.git
cd BUSCADOR-FRONT
npm install
npx expo install --fix
npm run typecheck
npx expo start --tunnel
```

Para crear una APK Android sin Android Studio local, inicia sesión en Expo y utiliza EAS Build:

```bash
npx expo install expo-dev-client
npx eas-cli@latest login
npx eas-cli@latest build:configure
npx eas-cli@latest build -p android --profile preview
```

La build aún no ha sido ejecutada ni verificada en un dispositivo.

## Próximas fases

- Persistencia local de favoritos
- Integración opcional con la API Buscador
- Animación de la marca y assets definitivos
- Pruebas Android reales y accesibilidad

Antes de cualquier uso de permisos especiales de Android se requerirán pantallas de consentimiento claro, control de revocación y justificación de los datos accedidos.
