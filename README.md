# BUSCADOR-FRONT

**Buscador · Editorial Preview** — explorador visual de ideas y noticias de muestra para Android.

Construido con React Native, Expo Router y TypeScript. La interfaz actual no solicita permisos especiales de Android ni accede a notificaciones o texto escrito en otras aplicaciones.

## Pantallas

- **Hoy:** portada editorial, historia destacada, temas y briefing.
- **Radar:** exploración de intereses y puntuaciones **simuladas** con representación visual ilustrativa.
- **Explorar:** búsqueda local por palabras y categorías.
- **Guardados:** colección de lecturas de la sesión.
- **Ajustes:** identidad, enlaces de exploración y transparencia.
- **Detalle:** lectura de los artículos de muestra.
- **Briefing:** selección de tres historias.

Todas las historias son **contenido editorial ilustrativo, no noticias actuales**. No hay scraping ni API de noticias implementada. El siguiente paso podría ser una fuente pública de noticias con atribución y fechas.

## Probar con la APK de desarrollo ya instalada

Desde Termux:

```bash
cd ~/BUSCADOR/frontend
git pull --ff-only
npm run typecheck
npx expo start --dev-client --localhost -c
```

En la aplicación instalada, conecta a `http://127.0.0.1:8081`. Los cambios de JavaScript/TypeScript no requieren reconstruir la APK.

## Desarrollo nuevo

```bash
npm install --no-audit --no-fund
npm run typecheck
```

Dependencias nativas existentes: no se incorporaron nuevas librerías nativas en el rediseño editorial.

## Privacidad

No se capturan mensajes ni pulsaciones de teclado. Si en una fase futura se construyen funciones que requieren acceso a información privada, deberán explicarse claramente, solicitar permiso explícito y permitir al usuario desactivarlas y gestionar sus datos.
