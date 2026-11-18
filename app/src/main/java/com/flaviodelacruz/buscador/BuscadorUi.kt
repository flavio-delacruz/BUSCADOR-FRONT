package com.flaviodelacruz.buscador

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Ink = Color(0xFF090B11)
val Panel = Color(0xFF151923)
val PanelAlt = Color(0xFF1C2230)
val Outline = Color(0xFF2A3040)
val Red = Color(0xFFFF3859)
val White = Color(0xFFF8F8FA)
val Muted = Color(0xFFADB4C2)
val Soft = Color(0xFF7C8598)
val Cyan = Color(0xFF71D8EC)
val Lilac = Color(0xFFC8A5FA)

@Composable
fun BuscadorApp(
    page: String,
    session: BuscadorSession?,
    status: ArchiveSnapshot,
    records: List<Map<String, Any>>,
    error: String?,
    favorites: Set<String>,
    article: Article,
    onPage: (String) -> Unit,
    onFinishIntro: () -> Unit,
    onSignedIn: (BuscadorSession) -> Unit,
    onSignOut: () -> Unit,
    onArticle: (String) -> Unit,
    onFavorite: (String) -> Unit,
    onConsent: () -> Unit,
    onSkip: () -> Unit,
    onOpenAccess: () -> Unit,
    onStopCapture: () -> Unit,
    onSourceChange: (List<String>, Boolean) -> Unit,
    onClear: () -> Unit,
    onRefresh: () -> Unit
) {
    MaterialTheme(
        colorScheme = darkColorScheme(primary = Red, background = Ink, surface = Panel, onSurface = White)
    ) {
        Column(Modifier.fillMaxSize().background(Ink).safeDrawingPadding()) {
            Box(Modifier.weight(1f)) {
                when (page) {
                    "intro" -> IntroExperience(onFinishIntro, onSignedIn)
                    "account" -> AccountEntry(onFinishIntro, onSignedIn, showGuest = true)
                    "welcome" -> WelcomeScreen(status, error, onConsent, onSkip)
                    "home" -> TodayScreen(status, onPage, onArticle)
                    "radar" -> RadarScreen(onArticle)
                    "search" -> ExploreScreen(onArticle)
                    "saved" -> SavedScreen(favorites, onArticle, onPage, onFavorite)
                    "settings" -> SettingsScreen(status, session, onPage, onSignOut)
                    "notifications" -> NotificationCenterScreen(
                        status, records, error, { onPage("home") }, onConsent,
                        onOpenAccess, onStopCapture, onSourceChange, onClear, onRefresh
                    )
                    "briefing" -> BriefingScreen(onArticle, { onPage("home") })
                    "detail" -> DetailScreen(article, article.id in favorites, onFavorite, { onPage("home") })
                }
            }
            if (page in listOf("home", "radar", "search", "saved", "settings")) {
                BottomTabs(page, onPage)
            }
        }
    }
}

@Composable
fun Label(value: String, color: Color = Red) {
    Text(value, color = color, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.4.sp)
}

@Composable
fun BodyText(value: String, color: Color = Muted) {
    Text(value, color = color, fontSize = 13.sp, lineHeight = 21.sp)
}

@Composable
fun Headline(value: String) {
    Text(value, color = White, fontWeight = FontWeight.Black, fontSize = 31.sp, lineHeight = 38.sp)
}

@Composable
fun SurfacePanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Panel, RoundedCornerShape(21.dp))
            .border(1.dp, Outline, RoundedCornerShape(21.dp))
            .padding(17.dp),
        content = content
    )
}

@Composable
fun RedAction(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(51.dp),
        shape = RoundedCornerShape(13.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Red, contentColor = White)
    ) {
        Text(label, fontWeight = FontWeight.Black, fontSize = 12.sp, letterSpacing = 0.4.sp)
    }
}

@Composable
fun ScrollPage(
    title: String? = null,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        if (title != null) {
            Label(subtitle ?: "BUSCADOR / ANDROID NATIVO")
            Spacer(Modifier.height(11.dp))
            Headline(title)
            Spacer(Modifier.height(22.dp))
        }
        content()
        Spacer(Modifier.height(30.dp))
    }
}

@Composable
fun BottomTabs(active: String, onPage: (String) -> Unit) {
    val tabs = listOf(
        Triple("home", "⌂", "Hoy"),
        Triple("radar", "◉", "Radar"),
        Triple("search", "⌕", "Explorar"),
        Triple("saved", "♡", "Guardados"),
        Triple("settings", "☷", "Ajustes")
    )
    Row(
        modifier = Modifier.fillMaxWidth().background(Color(0xFF11151F))
            .border(width = 1.dp, color = Outline)
            .padding(top = 9.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEach { (id, symbol, label) ->
            Column(
                modifier = Modifier.weight(1f).clickable { onPage(id) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(symbol, fontSize = 23.sp, color = if (active == id) Red else Soft)
                Text(
                    label, color = if (active == id) White else Muted,
                    fontWeight = FontWeight.Bold, fontSize = 10.sp, maxLines = 1
                )
                Spacer(Modifier.height(4.dp))
                Box(Modifier.size(4.dp).background(if (active == id) Red else Color.Transparent, RoundedCornerShape(100)))
            }
        }
    }
}

@Composable
fun ArticleCard(article: Article, saved: Boolean = false, onOpen: () -> Unit, onSave: (() -> Unit)? = null) {
    SurfacePanel(Modifier.padding(bottom = 12.dp).clickable(onClick = onOpen)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Label(article.category.uppercase())
            Spacer(Modifier.weight(1f))
            Label("${article.minutes} MIN", Soft)
        }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(54.dp).background(PanelAlt, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(article.glyph, color = Red, fontWeight = FontWeight.Black, fontSize = 31.sp)
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text(article.title, fontWeight = FontWeight.Black, color = White, fontSize = 16.sp, lineHeight = 21.sp)
                Spacer(Modifier.height(5.dp))
                Text(article.description, color = Muted, fontSize = 11.sp, lineHeight = 16.sp, maxLines = 2)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Label("LEER HISTORIA  ↗")
            Spacer(Modifier.weight(1f))
            if (onSave != null) {
                Text(
                    if (saved) "♥ Guardado" else "♡ Guardar",
                    color = if (saved) Red else Muted,
                    modifier = Modifier.clickable { onSave() }.padding(7.dp),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun TodayScreen(status: ArchiveSnapshot, onPage: (String) -> Unit, onArticle: (String) -> Unit) {
    ScrollPage {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BuscadorMark()
            Spacer(Modifier.width(13.dp))
            Column {
                Label("BUSCADOR / TU EDICIÓN PERSONAL")
                Text("DESCUBRE MÁS CADA DÍA", color = Soft, fontSize = 10.sp, letterSpacing = 1.sp)
            }
        }
        Spacer(Modifier.height(18.dp))
        Headline("Mira más allá\ndel titular.")
        Spacer(Modifier.height(21.dp))
        SurfacePanel(Modifier.clickable { onPage("notifications") }) {
            Label(if (status.recordingEnabled && status.accessGranted) "● HISTORIAL ENCENDIDO" else "● PROTEGE TUS NOTIFICACIONES")
            Spacer(Modifier.height(8.dp))
            Text("WhatsApp · Facebook · TikTok", color = White, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Spacer(Modifier.height(7.dp))
            BodyText(if (status.recordingEnabled) "Tienes ${status.count} notificaciones guardadas en este teléfono."
                else "Activa la captura con tu autorización. Tu historial queda cifrado y bajo tu control.")
            Spacer(Modifier.height(11.dp))
            Label("ABRIR HISTORIAL Y PERMISOS  ↗")
        }
        Spacer(Modifier.height(24.dp))
        Label("EN PORTADA · 01 / 08")
        Spacer(Modifier.height(13.dp))
        SurfacePanel(Modifier.clickable { onArticle("1") }) {
            Box(
                Modifier.fillMaxWidth().height(170.dp)
                    .background(Color(0xFF2C1826), RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("B.  /  001", color = Red, fontWeight = FontWeight.Black, fontSize = 53.sp)
            }
            Spacer(Modifier.height(16.dp))
            Label("TECNOLOGÍA / FUTURO CERCANO")
            Spacer(Modifier.height(8.dp))
            Text(articles[0].title, color = White, fontWeight = FontWeight.Black, fontSize = 24.sp, lineHeight = 29.sp)
            Spacer(Modifier.height(7.dp))
            BodyText(articles[0].description)
            Spacer(Modifier.height(12.dp))
            Label("EXPLORAR HISTORIA  ↗")
        }
        Spacer(Modifier.height(19.dp))
        SurfacePanel(Modifier.clickable { onPage("briefing") }) {
            Label("✳  TU DOSIS DE CONTEXTO", Lilac)
            Spacer(Modifier.height(8.dp))
            Text("El briefing de hoy  ↗", color = White, fontWeight = FontWeight.Black, fontSize = 18.sp)
            BodyText("Tres historias de ejemplo para descubrir")
        }
        Spacer(Modifier.height(25.dp))
        Text("Más para descubrir", color = White, fontWeight = FontWeight.Black, fontSize = 21.sp)
        Spacer(Modifier.height(15.dp))
        articles.drop(1).take(4).forEach { ArticleCard(it, onOpen = { onArticle(it.id) }) }
        BodyText("Catálogo demostrativo. No son noticias en tiempo real.", Soft)
    }
}

@Composable
fun RadarScreen(onArticle: (String) -> Unit) {
    ScrollPage("Detecta temas.\nConecta ideas.", "RADAR / MAPA DE IDEAS") {
        BodyText("Una perspectiva diferente para descubrir conversaciones y temas que vale la pena investigar.")
        Spacer(Modifier.height(18.dp))
        SurfacePanel {
            Label("EL MAPA RADAR", Cyan)
            Spacer(Modifier.height(8.dp))
            Text("Panorama de intereses", color = White, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Spacer(Modifier.height(14.dp))
            Box(Modifier.fillMaxWidth().height(225.dp), contentAlignment = Alignment.Center) {
                val transition = rememberInfiniteTransition(label = "radar")
                val pulse by transition.animateFloat(
                    initialValue = 0.4f, targetValue = 1f,
                    animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
                    label = "pulse"
                )
                Canvas(Modifier.size(207.dp)) {
                    val center = center
                    listOf(size.minDimension * 0.47f, size.minDimension * 0.34f, size.minDimension * 0.19f).forEachIndexed { i, radius ->
                        drawCircle(if (i == 2) Red else Cyan, radius, alpha = if (i == 2) 0.65f else 0.25f + pulse * 0.2f,
                            style = Stroke(width = 2f))
                    }
                    drawCircle(Red, size.minDimension * 0.025f, center = androidx.compose.ui.geometry.Offset(center.x * 0.43f, center.y * 0.57f))
                    drawCircle(Cyan, size.minDimension * 0.025f, center = androidx.compose.ui.geometry.Offset(center.x * 1.42f, center.y * 0.75f))
                    drawCircle(Lilac, size.minDimension * 0.025f, center = androidx.compose.ui.geometry.Offset(center.x * 0.85f, center.y * 1.68f))
                }
                Text("B", color = Red, fontWeight = FontWeight.Black, fontSize = 45.sp)
            }
            BodyText("Visualización ilustrativa; no usa datos de seguimiento.", Soft)
        }
        Spacer(Modifier.height(25.dp))
        Text("Índice de interés", color = White, fontWeight = FontWeight.Black, fontSize = 21.sp)
        Spacer(Modifier.height(12.dp))
        val signals = listOf(
            Triple("Inteligencia artificial", 84, "3"),
            Triple("Vida en las ciudades", 69, "4"),
            Triple("Exploración científica", 58, "2"),
            Triple("Diseño y creatividad", 42, "5")
        )
        signals.forEachIndexed { i, (name, score, id) ->
            SurfacePanel(Modifier.padding(bottom = 10.dp).clickable { onArticle(id) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Label("0${i + 1}")
                    Spacer(Modifier.width(13.dp))
                    Text(name, Modifier.weight(1f), color = White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(score.toString(), color = White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
                Spacer(Modifier.height(10.dp))
                Box(Modifier.fillMaxWidth().height(5.dp).background(Outline, RoundedCornerShape(5.dp))) {
                    Box(Modifier.fillMaxWidth(score / 100f).height(5.dp).background(Red, RoundedCornerShape(5.dp)))
                }
                Spacer(Modifier.height(10.dp))
                Label("VER HISTORIA  ↗", Soft)
            }
        }
    }
}

@Composable
fun ExploreScreen(onArticle: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("Todo") }
    val matches = articles.filter { a ->
        (filter == "Todo" || a.category == filter) &&
        (query.isBlank() || (a.title + " " + a.description + " " + a.category + " " + a.tags.joinToString()).contains(query.trim(), ignoreCase = true))
    }
    ScrollPage("Toda gran idea\nempieza aquí.", "BUSCADOR / EXPLORAR") {
        BodyText("Encuentra nuevas perspectivas sobre los temas que te interesan.")
        Spacer(Modifier.height(15.dp))
        OutlinedTextField(
            value = query, onValueChange = { query = it },
            singleLine = true, label = { Text("Busca una idea, tema o palabra") },
            leadingIcon = { Text("⌕", color = Red, fontSize = 25.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp)
        )
        Spacer(Modifier.height(21.dp))
        Label("FILTRAR POR UNIVERSO", Soft)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 12.dp)) {
            (listOf("Todo") + topics).forEach { name ->
                Box(
                    Modifier.padding(end = 8.dp).background(if (filter == name) Red else Panel, RoundedCornerShape(12.dp))
                        .border(1.dp, if (filter == name) Red else Outline, RoundedCornerShape(12.dp))
                        .clickable { filter = name }.padding(horizontal = 13.dp, vertical = 11.dp)
                ) {
                    Text(name, color = White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Historias", color = White, fontWeight = FontWeight.Black, fontSize = 21.sp)
            Spacer(Modifier.weight(1f))
            Label("${matches.size} RESULTADOS", Soft)
        }
        Spacer(Modifier.height(13.dp))
        if (matches.isEmpty()) {
            SurfacePanel {
                Text("Sin coincidencias", color = White, fontWeight = FontWeight.Bold)
                BodyText("Prueba otra búsqueda o categoría.")
            }
        } else matches.forEach { ArticleCard(it, onOpen = { onArticle(it.id) }) }
        BodyText("Catálogo de demostración. Aún no hay noticias en vivo.", Soft)
    }
}

@Composable
fun SavedScreen(favorites: Set<String>, onArticle: (String) -> Unit, onPage: (String) -> Unit, onFavorite: (String) -> Unit) {
    ScrollPage("Mi biblioteca", "LEE MÁS TARDE / TU SELECCIÓN") {
        SurfacePanel {
            Label("TU COLECCIÓN PERSONAL")
            Text(favorites.size.toString().padStart(2, '0'), color = White, fontSize = 70.sp, fontWeight = FontWeight.Black)
            Text("Historias guardadas", color = Red, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            BodyText("Aquello que te inspira merece un segundo vistazo.")
        }
        Spacer(Modifier.height(24.dp))
        val list = articles.filter { it.id in favorites }
        if (list.isEmpty()) {
            SurfacePanel {
                Text("Aquí empiezan las buenas ideas.", color = White, fontWeight = FontWeight.Black, fontSize = 17.sp)
                Spacer(Modifier.height(8.dp))
                BodyText("Guarda historias desde su página de lectura. Encontrarás todo aquí.")
                Spacer(Modifier.height(12.dp))
                RedAction("DESCUBRIR HISTORIAS") { onPage("search") }
            }
        } else list.forEach {
            ArticleCard(it, saved = true, onOpen = { onArticle(it.id) }, onSave = { onFavorite(it.id) })
        }
        Spacer(Modifier.height(12.dp))
        BodyText("Los favoritos de esta versión se conservan durante la sesión.", Soft)
    }
}

@Composable
fun SettingsScreen(status: ArchiveSnapshot, session: BuscadorSession?, onPage: (String) -> Unit, onSignOut: () -> Unit) {
    ScrollPage("Mi espacio", "LA EXPERIENCIA BUSCADOR") {
        SurfacePanel {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BuscadorMark()
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Buscador", color = White, fontSize = 23.sp, fontWeight = FontWeight.Black)
                    BodyText("Una nueva forma de descubrir.")
                    Label("EDICIÓN 2.0 · KOTLIN NATIVO")
                }
            }
        }
        Spacer(Modifier.height(25.dp))
        Text("Mi cuenta", color = White, fontWeight = FontWeight.Black, fontSize = 21.sp)
        Spacer(Modifier.height(12.dp))
        SurfacePanel {
            Label(if (session == null) "MODO INVITADO" else "SESIÓN INICIADA", Cyan)
            Spacer(Modifier.height(8.dp))
            Text(session?.email ?: "Explora sin cuenta", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            if (session == null) {
                Text("INICIAR SESIÓN O CREAR CUENTA   ↗",
                    color = Red, fontSize = 11.sp, fontWeight = FontWeight.Black,
                    modifier = Modifier.clickable { onPage("account") }.padding(vertical = 8.dp))
            } else {
                Text("CERRAR SESIÓN",
                    color = Red, fontSize = 11.sp, fontWeight = FontWeight.Black,
                    modifier = Modifier.clickable { onSignOut() }.padding(vertical = 8.dp))
            }
            BodyText("La captura local y la sincronización consentida se gestionan por separado.", Soft)
        }
        Spacer(Modifier.height(22.dp))
        Text("Privacidad y transparencia", color = White, fontWeight = FontWeight.Black, fontSize = 21.sp)
        Spacer(Modifier.height(12.dp))
        SurfacePanel(Modifier.clickable { onPage("notifications") }) {
            Label(if (status.recordingEnabled) "● CAPTURA ACTIVA" else "● CAPTURA DESACTIVADA")
            Spacer(Modifier.height(7.dp))
            Text("Centro de notificaciones  ↗", color = White, fontWeight = FontWeight.Black, fontSize = 17.sp)
            BodyText("Consulta, pausa, filtra o borra tu historial cifrado.")
        }
        Spacer(Modifier.height(16.dp))
        SurfacePanel {
            Label("SOBRE ESTA VERSIÓN", Cyan)
            Spacer(Modifier.height(11.dp))
            BodyText("Interfaz: Kotlin + Jetpack Compose")
            Spacer(Modifier.height(9.dp))
            BodyText("Historial: archivos cifrados y Android Keystore")
            Spacer(Modifier.height(9.dp))
            BodyText("Conexión: envío remoto solo con autorización manual")
            Spacer(Modifier.height(9.dp))
            BodyText("Contenido editorial: solo demostración")
        }
        Spacer(Modifier.height(24.dp))
        Label("BUSCADOR / HECHO PARA DESCUBRIR", Soft)
    }
}

@Composable
fun DetailScreen(article: Article, saved: Boolean, onFavorite: (String) -> Unit, onBack: () -> Unit) {
    ScrollPage {
        Text("‹  REGRESAR", color = Muted, fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onBack() }.padding(vertical = 10.dp), fontSize = 12.sp)
        Spacer(Modifier.height(18.dp))
        Label(article.kicker)
        Spacer(Modifier.height(12.dp))
        Headline(article.title)
        Spacer(Modifier.height(15.dp))
        BodyText(article.description, White)
        Spacer(Modifier.height(22.dp))
        SurfacePanel {
            Label("${article.category.uppercase()} · ${article.minutes} MIN", Cyan)
            Spacer(Modifier.height(14.dp))
            Box(Modifier.fillMaxWidth().height(155.dp).background(Color(0xFF271B2B), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center) {
                Text(article.glyph, color = Red, fontSize = 76.sp, fontWeight = FontWeight.Black)
            }
        }
        Spacer(Modifier.height(18.dp))
        RedAction(if (saved) "♥ QUITAR DE GUARDADOS" else "♡ GUARDAR PARA DESPUÉS") { onFavorite(article.id) }
        Spacer(Modifier.height(24.dp))
        article.paragraphs.forEach { paragraph ->
            Text(paragraph, color = Muted, fontSize = 15.sp, lineHeight = 25.sp)
            Spacer(Modifier.height(17.dp))
        }
        Label("LECTURA DEMOSTRATIVA · NO ES NOTICIA EN VIVO", Soft)
    }
}

@Composable
fun BriefingScreen(onArticle: (String) -> Unit, onBack: () -> Unit) {
    ScrollPage("Tu briefing\nde hoy.", "TRES HISTORIAS PARA DESCUBRIR") {
        Text("‹  REGRESAR", color = Muted, modifier = Modifier.clickable { onBack() }, fontSize = 12.sp)
        Spacer(Modifier.height(14.dp))
        BodyText("Una selección editorial de ejemplo para comenzar.")
        Spacer(Modifier.height(20.dp))
        listOf("1","3","2").forEach { id ->
            articles.firstOrNull { it.id == id }?.let { ArticleCard(it, onOpen = { onArticle(id) }) }
        }
    }
}

@Composable
fun WelcomeScreen(status: ArchiveSnapshot, error: String?, onConsent: () -> Unit, onSkip: () -> Unit) {
    ScrollPage {
        Spacer(Modifier.height(20.dp))
        Label("BUSCADOR / CONFIGURACIÓN INICIAL")
        Spacer(Modifier.height(16.dp))
        Headline("Tus avisos,\nbajo tu control.")
        Spacer(Modifier.height(14.dp))
        BodyText("Buscador puede guardar las notificaciones nuevas de WhatsApp, Facebook y TikTok con tu autorización. Todo se almacena cifrado en este dispositivo.")
        Spacer(Modifier.height(24.dp))
        SurfacePanel {
            Label("PRIVACIDAD Y PERMISOS", Cyan)
            Spacer(Modifier.height(10.dp))
            BodyText("Android pedirá un acceso especial a las notificaciones. Este permiso puede dar acceso amplio, pero Buscador solo conserva las fuentes indicadas.")
            Spacer(Modifier.height(11.dp))
            BodyText("Hasta 500 registros durante 30 días. No se envían a servidores; no se restauran mensajes antiguos ni contenido oculto por Android.")
            Spacer(Modifier.height(11.dp))
            Label("FUENTES PRESELECCIONADAS", Lilac)
            Spacer(Modifier.height(9.dp))
            listOf("✓  WhatsApp", "✓  Facebook", "✓  TikTok").forEach {
                Text(it, color = White, fontSize = 15.sp, modifier = Modifier.padding(vertical = 4.dp))
            }
        }
        Spacer(Modifier.height(22.dp))
        RedAction(if (status.accessGranted) "ACTIVAR CAPTURA" else "AUTORIZAR EN ANDROID") { onConsent() }
        Spacer(Modifier.height(10.dp))
        Text("AHORA NO · CONTINUAR SIN CAPTURA", color = Muted,
            fontSize = 12.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().clickable { onSkip() }.padding(16.dp),
            textAlign = TextAlign.Center)
        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(error, color = Red, fontSize = 12.sp)
        }
        Spacer(Modifier.height(15.dp))
        BodyText("Si Android restringe este acceso para una APK instalada manualmente, revisa Ajustes > Aplicaciones > Buscador > Permitir ajustes restringidos.", Soft)
    }
}
