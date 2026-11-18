package com.flaviodelacruz.buscador

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Faithful Kotlin port of the SIX official NYVARA MARKET slides.
// NYVARA's own React Native application is neither modified nor linked.
private val NCanvas = Color(0xFFFFFBFD)
private val NWine = Color(0xFFA6174B)
private val NInk = Color(0xFF590212)
private val NRose = Color(0xFFD93672)
private val NText = Color(0xFF735364)
private val NBorder = Color(0xFFF0D5E1)
private val NGold = Color(0xFFD3A075)

private data class NyvaraPage(
    val id: String,
    val eyebrow: String,
    val title: String,
    val accent: String,
    val description: String,
    val image: Int?,
    val left: Pair<String,String>?,
    val right: Pair<String,String>?,
    val color: Color
)

private val nyvaraPages = listOf(
    NyvaraPage(
        "inicio", "BIENVENIDO A NYVARA MARKET",
        "Un mundo de opciones,", "un solo lugar.",
        "Moda, tecnología, hogar, accesorios y mucho más. Descubre un marketplace para explorar diferentes categorías.",
        R.drawable.nyvara_original_motion, null, null, Color(0xFF790D32)
    ),
    NyvaraPage(
        "handmade", "EXPLORA SIN LÍMITES",
        "Lo que necesitas,", "a tu alcance.",
        "Desde moda y tecnología hasta hogar y herramientas: encuentra distintas categorías en un mismo espacio.",
        R.drawable.nyvara_moda, "Moda" to "Estilo", "Hogar" to "Tecnología", Color(0xFFA3174C)
    ),
    NyvaraPage(
        "quality", "INFORMACIÓN PARA ELEGIR",
        "Compra de forma", "más inteligente.",
        "Consulta fotografías, precios y descripciones publicadas para comparar opciones antes de comprar.",
        R.drawable.nyvara_calidad, "Detalles" to "Claros", "Precios" to "Visibles", Color(0xFF87502C)
    ),
    NyvaraPage(
        "gift", "IDEAS PARA CADA DÍA",
        "Encuentra ese", "detalle especial.",
        "Ideas para regalar, decorar, renovar tus espacios o consentirte, con opciones para diferentes momentos.",
        R.drawable.nyvara_regalo, "Regalos" to "Detalles", "Novedades" to "Ocasiones", Color(0xFF72234D)
    ),
    NyvaraPage(
        "xp", "CLUB NYVARA XP",
        "Tus actividades", "pueden sumar XP.",
        "Participa en actividades elegibles y consulta tus beneficios. Las recompensas XP se verifican en el servidor.",
        null, "XP" to "Verificados", "Beneficios" to "Club", Color(0xFF92184E)
    ),
    NyvaraPage(
        "xp-discount", "BENEFICIOS QUE CUENTAN",
        "Usa tus XP", "para ahorrar.",
        "Consulta los requisitos de canje y utiliza los beneficios disponibles para obtener descuentos en compras elegibles.",
        null, "Canje" to "Condiciones", "Ahorro" to "Descuentos", Color(0xFF854378)
    )
)

@Composable
fun NyvaraApprovedLogo(modifier: Modifier = Modifier, side: Dp = 86.dp, animated: Boolean = false) {
    var shown by remember { mutableStateOf(!animated) }
    LaunchedEffect(animated) { shown = true }
    val progress by animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        animationSpec = tween(1450),
        label = "nyvara-left-to-right"
    )
    Image(
        painter = painterResource(R.drawable.nyvara_original_motion),
        contentDescription = "Logotipo original NYVARA MARKET, monograma NY",
        contentScale = ContentScale.Fit,
        modifier = modifier.size(side)
            .drawWithContent {
                clipRect(right = size.width * progress) { drawContent() }
            }
    )
}

@Composable
fun NyvaraLogoBadge() {
    Box(
        Modifier.size(64.dp).background(Color.White, RoundedCornerShape(18.dp))
            .border(1.dp, NBorder, RoundedCornerShape(18.dp)),
        contentAlignment = Alignment.Center
    ) { NyvaraApprovedLogo(side = 56.dp) }
}

@Composable
private fun NyvaraTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 7.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
    ) {
        Text("NYVARA", color = Color(0xFF78183F), fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold, letterSpacing = 1.5.sp, fontSize = 21.sp)
        Spacer(Modifier.width(13.dp))
        Box(Modifier.width(1.dp).height(20.dp).background(Color(0xFFD19DAF)))
        Spacer(Modifier.width(13.dp))
        Text("MARKETPLACE", color = Color(0xFF78183F),
            fontWeight = FontWeight.Bold, letterSpacing = 2.1.sp, fontSize = 10.sp)
    }
}

@Composable
private fun NyvaraHero(page: NyvaraPage, number: Int, compact: Boolean) {
    val height = if (compact) 213.dp else 292.dp
    var arrived by remember(number) { mutableStateOf(false) }
    LaunchedEffect(number) { arrived = true }
    val reveal by animateFloatAsState(
        if (arrived) 1f else 0f,
        animationSpec = tween(durationMillis = 740),
        label = "hero-entrance"
    )
    Box(
        Modifier.fillMaxWidth().height(height).graphicsLayer {
            alpha = reveal
            translationY = (1f - reveal) * 25f
        }, contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(if (compact) 207.dp else 270.dp)) {
            drawCircle(Color(0xFFEAB9D1).copy(alpha = 0.22f),
                radius = size.minDimension * 0.47f,
                style = Stroke(width = 1.8.dp.toPx()))
            drawCircle(Color(0xFFD3A075).copy(alpha = 0.26f),
                radius = size.minDimension * 0.35f,
                style = Stroke(width = 1.8.dp.toPx()))
        }
        Box(
            Modifier.size(if (compact) 202.dp else 266.dp)
                .background(
                    Brush.linearGradient(listOf(Color.White, Color(0xFFFFF0F7), Color(0xFFF8CEE2))),
                    RoundedCornerShape(35.dp)
                )
                .border(1.8.dp, Color.White, RoundedCornerShape(35.dp)),
            contentAlignment = Alignment.Center
        ) {
            when (page.id) {
                "inicio" -> NyvaraApprovedLogo(side = if (compact) 160.dp else 209.dp, animated = true)
                "handmade", "quality", "gift" -> Image(
                    painter = painterResource(page.image!!),
                    contentDescription = "Ilustración original de NYVARA: " + page.id,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(if (compact) 186.dp else 250.dp)
                )
                "xp", "xp-discount" -> NyvaraXpIllustration(page.id == "xp", compact)
            }
        }
        if (number > 0) {
            val left = when (page.id) {
                "handmade" -> "MODA"
                "quality" -> "DETALLES"
                "gift" -> "REGALOS"
                "xp" -> "ACTIVIDADES"
                else -> "ACUMULA"
            }
            val right = when (page.id) {
                "handmade" -> "TECNOLOGÍA"
                "quality" -> "PRECIOS"
                "gift" -> "NOVEDADES"
                "xp" -> "BENEFICIOS"
                else -> "CANJEA"
            }
            NyvaraFloatingTag(left, Modifier.align(Alignment.CenterStart).padding(bottom = 73.dp))
            NyvaraFloatingTag(right, Modifier.align(Alignment.CenterEnd).padding(top = 70.dp))
        }
    }
}

@Composable
private fun NyvaraXpIllustration(isClub: Boolean, compact: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(if (compact) 114.dp else 151.dp)
                .background(Color.White.copy(alpha = 0.81f), RoundedCornerShape(34.dp))
                .border(1.dp, Color(0xFFEDC38C), RoundedCornerShape(34.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(if (isClub) "✦" else "%", color = NWine,
                fontSize = if (compact) 77.sp else 104.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(7.dp))
        Text(if (isClub) "CLUB XP" else "XP + AHORRO", color = NInk,
            fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
    }
}

@Composable
private fun NyvaraFloatingTag(value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(Color(0xFFFFF9FD), CircleShape)
            .border(1.dp, Color.White, CircleShape)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(5.dp).background(NGold, CircleShape))
        Spacer(Modifier.width(6.dp))
        Text(value, color = Color(0xFF851D43), fontSize = 9.sp,
            fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
    }
}

@Composable
private fun NyvaraHighlights(page: NyvaraPage, compact: Boolean) {
    if (page.left == null || page.right == null) return
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        listOf(page.left, page.right).forEachIndexed { i, item ->
            Column(
                Modifier.weight(1f)
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .border(1.dp, Color(0xFFF3D9E5), RoundedCornerShape(20.dp))
                    .padding(vertical = if (compact) 10.dp else 15.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(if (i == 0) "✧" else "♡", color = if(i==0) NWine else NRose,
                    fontSize = if (compact) 19.sp else 26.sp)
                Text(item.first, color = if(i==0) NWine else NRose,
                    fontWeight = FontWeight.Bold, fontSize = if (compact) 13.sp else 16.sp)
                Text(item.second, color = NText, fontSize = if (compact) 10.sp else 12.sp)
            }
        }
    }
}

@Composable
fun NyvaraSixIntro(onDone: () -> Unit) {
    var index by rememberSaveable { mutableIntStateOf(0) }
    val compact = LocalConfiguration.current.screenHeightDp < 760
    val page = nyvaraPages[index]
    var entered by remember(index) { mutableStateOf(false) }
    LaunchedEffect(index) { entered = true }
    val opacity by animateFloatAsState(if (entered) 1f else 0f, tween(480), label = "content-reveal")
    Column(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(NCanvas, Color(0xFFFFF1F8), Color(0xFFF9DDEB), NCanvas))
        ).padding(horizontal = 19.dp)
    ) {
        NyvaraTopBar()
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(if (compact) 2.dp else 13.dp))
            NyvaraHero(page, index, compact)
            Spacer(Modifier.height(if (compact) 7.dp else 18.dp))
            Column(
                Modifier.fillMaxWidth().graphicsLayer { alpha = opacity },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.background(Color(0xFFFFF9FC), CircleShape)
                        .border(1.dp, Color(0xFFF3CADB), CircleShape)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(6.dp).background(NWine, CircleShape))
                    Spacer(Modifier.width(8.dp))
                    Text(page.eyebrow, color = NWine, fontSize = 10.sp,
                        fontWeight = FontWeight.Bold, letterSpacing = 0.7.sp)
                }
                Spacer(Modifier.height(15.dp))
                Text(page.title, color = NInk,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = if (compact) 29.sp else 35.sp,
                    lineHeight = if (compact) 35.sp else 42.sp,
                    textAlign = TextAlign.Center)
                Text(page.accent, color = page.color,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = if (compact) 29.sp else 35.sp,
                    lineHeight = if (compact) 35.sp else 42.sp,
                    textAlign = TextAlign.Center)
                Spacer(Modifier.height(11.dp))
                Text(page.description, color = NText,
                    fontSize = if (compact) 12.sp else 13.sp,
                    lineHeight = 21.sp, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 11.dp))
                Spacer(Modifier.height(if (compact) 14.dp else 22.dp))
                NyvaraHighlights(page,compact)
                Spacer(Modifier.height(14.dp))
            }
        }
        NyvaraFooter(index, onBack = { if(index>0) index-- },
            onNext = { if(index < nyvaraPages.lastIndex) index++ else onDone() },
            compact = compact)
    }
}

@Composable
private fun NyvaraFooter(index: Int, onBack: () -> Unit, onNext: () -> Unit, compact: Boolean) {
    Column(
        Modifier.fillMaxWidth()
            .background(Color(0xFFFFF9FD).copy(alpha = 0.96f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .border(1.dp, Color(0xFFF6D9E6), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(horizontal = 11.dp, vertical = if (compact) 12.dp else 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp), verticalAlignment = Alignment.CenterVertically) {
            repeat(6) { dot ->
                Box(Modifier.size(if(dot==index) 13.dp else 7.dp)
                    .background(if(dot==index) NWine else Color(0xFFF0CADA), CircleShape))
            }
        }
        Spacer(Modifier.height(13.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
            if(index>0) {
                Box(
                    Modifier.weight(0.33f).height(50.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFF0CADA), RoundedCornerShape(16.dp))
                        .clickable(onClick=onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Text("ATRÁS", color = NWine, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Box(Modifier.weight(0.67f).height(50.dp)
                .background(Brush.horizontalGradient(listOf(NWine,NRose)),RoundedCornerShape(16.dp))
                .border(1.dp, NGold.copy(alpha=0.65f), RoundedCornerShape(16.dp))
                .clickable(onClick=onNext),
                contentAlignment = Alignment.Center) {
                Text(if(index==5) "IR A INICIAR SESIÓN  →" else "CONTINUAR  →",
                    color = Color.White, fontSize = 12.sp,
                    fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
            }
        }
    }
}

@Composable
fun NyvaraMockLogin(onBack: () -> Unit, onGuest: () -> Unit) {
    var register by rememberSaveable { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    val compact = LocalConfiguration.current.screenHeightDp < 760

    if (showError) {
        AlertDialog(
            onDismissRequest = { showError = false },
            title = { Text("Acceso no disponible", color = NInk, fontWeight = FontWeight.Bold) },
            text = { Text("Falló. Contacte al desarrollador.", color = NText) },
            confirmButton = { TextButton(onClick = { showError = false }) { Text("ACEPTAR", color = NWine) } },
            containerColor = Color(0xFFFFFBFD)
        )
    }
    Column(
        Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(NCanvas,Color(0xFFFFECF5),NCanvas)))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 11.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically) {
            Text("‹  VOLVER", modifier = Modifier.clickable(onClick=onBack).padding(9.dp),
                color = NWine, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Text("NYVARA  /  MARKETPLACE", color = NWine,
                fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.1.sp)
        }
        Spacer(Modifier.height(if (compact) 3.dp else 11.dp))
        Box(
            Modifier.fillMaxWidth().height(if(compact) 174.dp else 204.dp)
                .background(Brush.linearGradient(listOf(Color.White, Color(0xFFFFF0F7), Color(0xFFF5CCDE))),RoundedCornerShape(28.dp))
                .border(1.dp,Color.White,RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(if(compact) 119.dp else 143.dp)
                    .background(Color.White, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center) {
                    NyvaraApprovedLogo(side = if(compact) 112.dp else 133.dp, animated = true)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("EL MARKETPLACE PARA TI", color = NWine, fontSize = 9.sp, fontWeight=FontWeight.Bold)
                    Spacer(Modifier.height(7.dp))
                    Text("NYVARA", color = NInk, fontSize = 22.sp, fontWeight = FontWeight.Bold, fontFamily=FontFamily.Serif)
                    Text("MARKETPLACE", color = NWine, fontSize = 10.sp, letterSpacing=2.sp)
                    Spacer(Modifier.height(8.dp))
                    Box(Modifier.width(55.dp).height(1.dp).background(NGold))
                    Spacer(Modifier.height(7.dp))
                    Text("Moda, tecnología, hogar y más.", color = NText, fontSize=10.sp, lineHeight=15.sp)
                }
            }
        }
        Spacer(Modifier.height(17.dp))
        Text(if(register) "Crea tu" else "Bienvenido", color=NInk,fontFamily=FontFamily.Serif,
            fontSize=34.sp,fontWeight=FontWeight.SemiBold)
        Text(if(register) "cuenta" else "a NYVARA", color=NWine,fontFamily=FontFamily.Serif,
            fontSize=34.sp,fontWeight=FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(if(register) "Descubre productos y crea un espacio a tu estilo." else "Accede al marketplace, tus pedidos y los beneficios del Club XP.",
            color=NText,fontSize=12.sp,lineHeight=20.sp,textAlign=TextAlign.Center)
        Spacer(Modifier.height(18.dp))
        Column(
            Modifier.fillMaxWidth().background(
                Brush.linearGradient(listOf(Color.White,Color(0xFFFFFAFD),Color(0xFFFFEAF3))),
                RoundedCornerShape(26.dp)
            ).border(1.dp, Color.White,RoundedCornerShape(26.dp))
                .padding(17.dp)
        ) {
            Row(verticalAlignment=Alignment.CenterVertically) {
                Box(Modifier.size(39.dp).background(Color(0xFFFFF2F8),RoundedCornerShape(13.dp)),
                    contentAlignment=Alignment.Center) { Text("✦",color=NWine,fontSize=25.sp) }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(if(register) "Empecemos contigo" else "Tu acceso NYVARA",
                        color=NInk,fontSize=17.sp,fontWeight=FontWeight.Bold)
                    Text(if(register) "Registro de demostración" else "Continúa con tu cuenta de Google",
                        color=NText,fontSize=11.sp)
                }
            }
            Spacer(Modifier.height(17.dp))
            Button(
                onClick={ showError = true },
                colors=ButtonDefaults.buttonColors(containerColor=NWine),
                modifier=Modifier.fillMaxWidth().height(51.dp),
                shape=RoundedCornerShape(15.dp)
            ) {
                Text("G",fontSize=20.sp,color=Color.White,fontWeight=FontWeight.Black)
                Spacer(Modifier.width(12.dp))
                Text(if(register) "REGISTRARME CON GOOGLE" else "CONTINUAR CON GOOGLE",
                    fontSize=12.sp,fontWeight=FontWeight.Black,letterSpacing=0.5.sp)
            }
            Spacer(Modifier.height(11.dp))
            Text("Pantalla de demostración. No se conecta a una cuenta real.",
                color=NText,fontSize=10.sp,textAlign=TextAlign.Center,
                modifier=Modifier.fillMaxWidth())
        }
        Spacer(Modifier.height(16.dp))
        Text(if(register) "¿Ya tienes una cuenta?" else "¿Todavía no tienes cuenta?",
            color=NText,fontSize=12.sp)
        Spacer(Modifier.height(10.dp))
        Box(
            Modifier.fillMaxWidth().height(47.dp)
                .background(Color.White,RoundedCornerShape(14.dp))
                .border(1.dp,Color(0xFFF0CADA),RoundedCornerShape(14.dp))
                .clickable { register = !register },
            contentAlignment=Alignment.Center
        ) {
            Text(if(register) "INICIAR SESIÓN" else "CREAR UNA CUENTA",
                color=NWine,fontWeight=FontWeight.Bold,fontSize=12.sp)
        }
        Spacer(Modifier.height(16.dp))
        Text("EXPLORAR SIN INICIAR SESIÓN  ↗", color=NWine,
            modifier=Modifier.clickable(onClick=onGuest).padding(9.dp),
            fontWeight=FontWeight.Bold,fontSize=11.sp)
        Text("Solo el botón de acceso muestra el fallo. El modo local conserva la captura autorizada.",
            color=NText,fontSize=10.sp,textAlign=TextAlign.Center)
        Spacer(Modifier.height(15.dp))
    }
}
