package com.flaviodelacruz.buscador

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val Wine = Color(0xFF6F273E)
private val Rose = Color(0xFFFF8AA2)

/**
 * New brand: crimson B with a white magnifying ring integrated in its lower
 * counter. A native vector/Compose logo, not a bitmap preview.
 */
@Composable
fun BuscadorMark(modifier: Modifier = Modifier, large: Boolean = false) {
    val side = if (large) 152.dp else 64.dp
    Box(modifier.size(side), contentAlignment = Alignment.Center) {
        Box(
            Modifier.fillMaxSize()
                .background(
                    Brush.linearGradient(listOf(Color(0xFF2B1726), Color(0xFF0F1520))),
                    RoundedCornerShape(if (large) 37.dp else 19.dp)
                )
                .border(1.dp, Wine.copy(alpha = 0.85f), RoundedCornerShape(if (large) 37.dp else 19.dp))
        )
        Text(
            "B",
            fontSize = if (large) 101.sp else 44.sp,
            fontWeight = FontWeight.Black,
            color = Red,
            modifier = Modifier.offset(x = if (large) (-6).dp else (-3).dp, y = if (large) (-4).dp else (-2).dp)
        )
        Canvas(
            modifier = Modifier
                .size(if (large) 54.dp else 25.dp)
                .offset(x = if (large) 34.dp else 15.dp, y = if (large) 28.dp else 14.dp)
        ) {
            val r = size.minDimension * 0.29f
            val cx = size.width * 0.41f
            val cy = size.height * 0.40f
            drawCircle(
                White, r, center = androidx.compose.ui.geometry.Offset(cx, cy),
                style = Stroke(width = size.minDimension * 0.095f)
            )
            drawLine(
                White,
                start = androidx.compose.ui.geometry.Offset(cx + r * 0.70f, cy + r * 0.70f),
                end = androidx.compose.ui.geometry.Offset(size.width * 0.86f, size.height * 0.88f),
                strokeWidth = size.minDimension * 0.10f,
                cap = StrokeCap.Round
            )
        }
    }
}

private data class OnboardingSlide(
    val eyebrow: String,
    val title: String,
    val emphasis: String,
    val description: String,
    val glyph: String,
    val accent: Color
)

private val pages = listOf(
    OnboardingSlide(
        "01 · UNA NUEVA PERSPECTIVA",
        "Descubre lo que", "importa.",
        "Un espacio moderno para explorar temas, conectar ideas y encontrar historias.",
        "✦", Red
    ),
    OnboardingSlide(
        "02 · UN MUNDO DE IDEAS",
        "Todo comienza", "buscando.",
        "Explora tecnología, ciencia, cultura y nuevas perspectivas desde un solo lugar.",
        "⌕", Cyan
    ),
    OnboardingSlide(
        "03 · TU PROPIO RADAR",
        "Conecta señales.", "Descubre más.",
        "Un radar visual y lecturas seleccionadas para profundizar en aquello que te interesa.",
        "◉", Lilac
    ),
    OnboardingSlide(
        "04 · PRIVACIDAD BAJO CONTROL",
        "Tus datos.", "Tus decisiones.",
        "El historial de notificaciones requiere permiso de Android; el envío al servidor se autoriza por separado y puede desactivarse.",
        "♢", Rose
    )
)

@Composable
fun IntroExperience(
    onGuest: () -> Unit,
    onSignedIn: (BuscadorSession) -> Unit
) {
    var step by rememberSaveable { mutableStateOf(0) }
    // 1. Splash, 2-4. Feature slides, 5. Sign in, 6. Main screen.
    LaunchedEffect(step) {
        if (step == 0) {
            delay(1400)
            step = 1
        }
    }
    when {
        step == 0 -> SplashScreen()
        step in 1..3 -> IntroSlide(slide = pages[step - 1], number = step, onContinue = { step++ }, onSkip = { step = 4 })
        step == 4 -> IntroSlide(slide = pages[3], number = 4, onContinue = { step = 5 }, onSkip = { step = 5 })
        else -> AccountEntry(onGuest, onSignedIn, showGuest = true)
    }
}

@Composable
private fun SplashScreen() {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.76f,
        animationSpec = tween(durationMillis = 750),
        label = "brand-reveal"
    )
    Box(
        Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Ink, Color(0xFF251220), Ink))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            BuscadorMark(Modifier.scale(scale), large = true)
            Spacer(Modifier.height(19.dp))
            Text("BUSCADOR", color = White, fontSize = 27.sp, fontWeight = FontWeight.Black, letterSpacing = 6.sp)
            Spacer(Modifier.height(9.dp))
            Text("EXPLORA · CONECTA · DESCUBRE", color = Soft, fontSize = 10.sp, letterSpacing = 2.sp)
        }
    }
}

@Composable
private fun IntroSlide(
    slide: OnboardingSlide,
    number: Int,
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(Ink, Color(0xFF190F1C), Ink)))
            .padding(horizontal = 24.dp, vertical = 23.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BuscadorMark()
            Spacer(Modifier.width(13.dp))
            Column {
                Text("BUSCADOR", color = White, fontSize = 16.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Text("LA EXPERIENCIA COMIENZA", color = Soft, fontSize = 9.sp, letterSpacing = 1.sp)
            }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onSkip) { Text("OMITIR", color = Muted, fontSize = 11.sp) }
        }
        Spacer(Modifier.weight(0.6f))
        // Each panel gets a distinct symbol, accent and composition.
        Box(
            Modifier.fillMaxWidth().height(235.dp)
                .background(
                    Brush.linearGradient(listOf(Color(0xFF2E1C30), Color(0xFF101D2C))),
                    RoundedCornerShape(31.dp)
                )
                .border(1.dp, slide.accent.copy(alpha = 0.34f), RoundedCornerShape(31.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val midpoint = center
                for (n in 3 downTo 1) {
                    drawCircle(
                        slide.accent.copy(alpha = 0.13f + (4 - n) * 0.05f),
                        radius = size.minDimension * (0.13f + n * 0.12f),
                        style = Stroke(width = 1.6.dp.toPx()),
                        center = midpoint
                    )
                }
            }
            Text(
                slide.glyph, color = slide.accent, fontSize = 106.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                "%02d / 04".format(number),
                modifier = Modifier.align(Alignment.TopEnd).padding(18.dp),
                color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(32.dp))
        Label(slide.eyebrow, slide.accent)
        Spacer(Modifier.height(11.dp))
        Text(slide.title, color = White, fontWeight = FontWeight.Black, fontSize = 32.sp, lineHeight = 38.sp)
        Text(slide.emphasis, color = slide.accent, fontWeight = FontWeight.Black, fontSize = 32.sp, lineHeight = 38.sp)
        Spacer(Modifier.height(15.dp))
        BodyText(slide.description)
        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp), modifier = Modifier.padding(bottom = 20.dp)) {
            repeat(4) { index ->
                Box(
                    Modifier.width(if (index == number - 1) 29.dp else 7.dp)
                        .height(6.dp)
                        .background(if (index == number - 1) slide.accent else Outline, RoundedCornerShape(8.dp))
                )
            }
        }
        RedAction(if (number == 4) "IR A MI CUENTA  →" else "CONTINUAR  →", onClick = onContinue)
    }
}

@Composable
fun AccountEntry(
    onGuest: () -> Unit,
    onSignedIn: (BuscadorSession) -> Unit,
    showGuest: Boolean = true
) {
    var creating by remember { mutableStateOf(false) }
    var url by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Brush.verticalGradient(listOf(Ink, Color(0xFF1A101D), Ink)))
            .padding(horizontal = 24.dp, vertical = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        BuscadorMark(large = true)
        Spacer(Modifier.height(19.dp))
        Text("Bienvenido a Buscador", color = White, fontSize = 25.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(5.dp))
        BodyText("Una experiencia diseñada para descubrir.", Soft)
        Spacer(Modifier.height(24.dp))

        SurfacePanel {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(false to "INICIAR SESIÓN", true to "CREAR CUENTA").forEach { (mode, label) ->
                    Box(
                        modifier = Modifier.weight(1f)
                            .background(if (creating == mode) Red.copy(alpha = 0.18f) else PanelAlt, RoundedCornerShape(11.dp))
                            .border(1.dp, if (creating == mode) Red else Outline, RoundedCornerShape(11.dp))
                            .clickable { creating = mode; error = "" }
                            .padding(vertical = 13.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(label, color = if (creating == mode) White else Muted, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
            Label(if (creating) "TU NUEVO ESPACIO" else "QUÉ BUENO VERTE DE NUEVO")
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = url, onValueChange = { url = it },
                label = { Text("Dirección HTTPS del servidor") },
                placeholder = { Text("https://tu-api.ejemplo.com") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Contraseña (mínimo 10 caracteres)") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))
            RedAction(if (busy) "CONECTANDO…" else if (creating) "CREAR MI CUENTA  →" else "INGRESAR A BUSCADOR  →") {
                if (!busy) {
                    if (email.isBlank() || password.length < 10 || url.isBlank()) {
                        error = "Introduce la URL HTTPS, tu correo y una contraseña de al menos 10 caracteres."
                    } else {
                        busy = true
                        error = ""
                        scope.launch {
                            val outcome = withContext(Dispatchers.IO) {
                                runCatching { AuthClient.authenticate(url, email, password, creating) }
                            }
                            busy = false
                            outcome.onSuccess {
                                password = ""
                                onSignedIn(it)
                            }.onFailure { e ->
                                error = e.message ?: "No se pudo iniciar sesión. Comprueba la conexión."
                            }
                        }
                    }
                }
            }
            if (error.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Text(error, color = Rose, fontSize = 12.sp, lineHeight = 18.sp)
            }
            Spacer(Modifier.height(10.dp))
            BodyText("La cuenta necesita MongoDB y un backend HTTPS configurados.", Soft)
        }
        Spacer(Modifier.height(18.dp))
        if (showGuest) {
            Text(
                "CONTINUAR SIN CUENTA  ↗",
                modifier = Modifier.fillMaxWidth().clickable { onGuest() }.padding(12.dp),
                color = Cyan, fontWeight = FontWeight.Black, fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
            BodyText("Puedes explorar Buscador y configurar la captura local sin iniciar sesión.", Soft)
        }
        Spacer(Modifier.height(15.dp))
        BodyText("El inicio de sesión no autoriza acceso a tus notificaciones ni sincronización remota.", Soft)
    }
}
