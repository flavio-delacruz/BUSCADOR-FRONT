package com.flaviodelacruz.buscador

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DateFormat
import java.util.Date
import java.util.Locale

data class NotificationGroup(val name: String, val packages: List<String>, val description: String, val color: Color)

val notificationGroups = listOf(
    NotificationGroup("WhatsApp", listOf("com.whatsapp","com.whatsapp.w4b"),
        "WhatsApp y WhatsApp Business", Color(0xFF43D38E)),
    NotificationGroup("Facebook", listOf("com.facebook.katana","com.facebook.lite"),
        "Facebook y Facebook Lite", Color(0xFF70A4FF)),
    NotificationGroup("TikTok", listOf("com.zhiliaoapp.musically"),
        "TikTok", Color(0xFFF27E95))
)

fun sourceName(pkg: String): String = when (pkg) {
    "com.whatsapp", "com.whatsapp.w4b" -> "WhatsApp"
    "com.facebook.katana", "com.facebook.lite" -> "Facebook"
    "com.facebook.orca" -> "Messenger"
    "com.zhiliaoapp.musically" -> "TikTok"
    else -> "Notificación"
}

@Composable
fun NotificationCenterScreen(
    status: ArchiveSnapshot,
    records: List<Map<String, Any>>,
    error: String?,
    onBack: () -> Unit,
    onConsent: () -> Unit,
    onOpenAccess: () -> Unit,
    onStopCapture: () -> Unit,
    onSourceChange: (List<String>, Boolean) -> Unit,
    onClear: () -> Unit,
    onRefresh: () -> Unit
) {
    var showClearConfirmation by remember { mutableStateOf(false) }
    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            title = { Text("Borrar todo el historial") },
            text = { Text("Se eliminarán permanentemente los registros cifrados de Buscador. No se modificarán las aplicaciones de origen.") },
            confirmButton = {
                TextButton(onClick = { showClearConfirmation = false; onClear() }) {
                    Text("BORRAR TODO", color = Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmation = false }) { Text("CANCELAR") }
            }
        )
    }

    ScrollPage {
        Text(
            "‹  REGRESAR", color = Muted,
            fontSize = 12.sp, fontWeight = FontWeight.Black,
            modifier = Modifier.clickable { onBack() }.padding(vertical = 10.dp)
        )
        Spacer(Modifier.height(18.dp))
        Label("BUSCADOR / TU PRIVACIDAD")
        Spacer(Modifier.height(11.dp))
        Headline("Historial de\nnotificaciones.")
        Spacer(Modifier.height(12.dp))
        BodyText("Solo guardamos las notificaciones nuevas que Android muestre de las aplicaciones autorizadas. El contenido se cifra y permanece en este teléfono.")
        Spacer(Modifier.height(20.dp))

        SurfacePanel {
            Label(if (status.accessGranted && status.recordingEnabled) "● HISTORIAL ACTIVADO"
                else if (status.accessGranted) "● LISTO PARA ACTIVAR" else "● PERMISO PENDIENTE")
            Spacer(Modifier.height(8.dp))
            Text(
                if (status.accessGranted && status.recordingEnabled) "Capturando notificaciones"
                else "Captura desactivada",
                color = White, fontWeight = FontWeight.Black, fontSize = 19.sp
            )
            Spacer(Modifier.height(7.dp))
            BodyText(
                if (status.accessGranted && status.recordingEnabled)
                    "Se guardarán notificaciones futuras de las fuentes seleccionadas."
                else "Aún no se guardan nuevas notificaciones."
            )
            Spacer(Modifier.height(12.dp))
            Label("PERMISO: ${if (status.accessGranted) "AUTORIZADO" else "SIN AUTORIZAR"}", Cyan)
            Spacer(Modifier.height(4.dp))
            Label("SERVICIO: ${if (status.serviceConnected) "CONECTADO" else "NO CONECTADO"}", Soft)
            if (status.accessGranted && !status.serviceConnected) {
                Spacer(Modifier.height(10.dp))
                BodyText("Android todavía no ha conectado el servicio. Comprueba el permiso especial.", Red)
            }
        }

        Spacer(Modifier.height(19.dp))
        Label("1 / PERMISO DEL SISTEMA", Cyan)
        Spacer(Modifier.height(10.dp))
        if (!status.recordingEnabled || !status.accessGranted) {
            RedAction(
                if (status.accessGranted) "INICIAR CAPTURA" else "ACTIVAR ACCESO Y CAPTURA"
            ) { onConsent() }
            Spacer(Modifier.height(9.dp))
        }
        SurfacePanel {
            Text(
                "REVISAR PERMISO ANDROID  ↗",
                color = White, fontWeight = FontWeight.Black, fontSize = 12.sp,
                modifier = Modifier.clickable { onOpenAccess() }.padding(vertical = 6.dp)
            )
            Spacer(Modifier.height(5.dp))
            BodyText("El acceso a notificaciones se autoriza y revoca en los Ajustes especiales de Android.")
        }

        Spacer(Modifier.height(23.dp))
        Label("2 / FUENTES AUTORIZADAS", Cyan)
        Spacer(Modifier.height(10.dp))
        notificationGroups.forEach { source ->
            val selected = source.packages.any { it in status.sources }
            SurfacePanel(Modifier.padding(bottom = 9.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(source.name, color = White, fontWeight = FontWeight.Black, fontSize = 15.sp)
                        Spacer(Modifier.height(3.dp))
                        BodyText(source.description, Soft)
                    }
                    Switch(
                        checked = selected,
                        onCheckedChange = { onSourceChange(source.packages, it) }
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))
        if (status.recordingEnabled) {
            SurfacePanel {
                Text("Pausar captura", color = White, fontWeight = FontWeight.Black,
                    fontSize = 16.sp)
                Spacer(Modifier.height(6.dp))
                BodyText("No se añadirán avisos nuevos hasta que vuelvas a activarla.")
                Spacer(Modifier.height(12.dp))
                Text(
                    "DETENER CAPTURA",
                    color = Red, fontWeight = FontWeight.Black, fontSize = 12.sp,
                    modifier = Modifier.clickable { onStopCapture() }.padding(vertical = 7.dp)
                )
            }
            Spacer(Modifier.height(18.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("Historial local", color = White, fontSize = 21.sp, fontWeight = FontWeight.Black,
                modifier = Modifier.weight(1f))
            Text("ACTUALIZAR ↻", color = Red, fontSize = 11.sp, fontWeight = FontWeight.Black,
                modifier = Modifier.clickable { onRefresh() }.padding(7.dp))
        }
        Spacer(Modifier.height(12.dp))
        SurfacePanel {
            Text(status.count.toString(), color = Red, fontSize = 39.sp, fontWeight = FontWeight.Black)
            Label("NOTIFICACIONES GUARDADAS", Soft)
            Spacer(Modifier.height(4.dp))
            BodyText("Máximo 500 · Conservación hasta 30 días")
        }
        Spacer(Modifier.height(12.dp))
        if (records.isEmpty()) {
            SurfacePanel {
                Text("Todavía no hay notificaciones guardadas", color = White,
                    fontWeight = FontWeight.Black, fontSize = 15.sp)
                Spacer(Modifier.height(7.dp))
                BodyText("Concede el acceso y recibe una notificación nueva de WhatsApp, Facebook o TikTok para comprobar la captura.")
            }
        } else records.forEach { record ->
            val pkg = record["source"] as? String ?: ""
            val title = record["title"] as? String ?: ""
            val message = record["message"] as? String ?: ""
            val time = (record["time"] as? Number)?.toLong() ?: 0L
            SurfacePanel(Modifier.padding(bottom = 10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Label(sourceName(pkg).uppercase())
                    Spacer(Modifier.weight(1f))
                    Text(
                        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale("es","PE"))
                            .format(Date(time)),
                        color = Soft, fontSize = 10.sp
                    )
                }
                if (title.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    Text(title, color = White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text(message, color = Muted, fontSize = 13.sp, lineHeight = 19.sp)
            }
        }

        Spacer(Modifier.height(21.dp))
        AutoSyncCard()

        if (error != null) {
            Spacer(Modifier.height(12.dp))
            BodyText(error, Red)
        }
        Spacer(Modifier.height(16.dp))
        SurfacePanel {
            Label("PRIVACIDAD Y CONTROL", Cyan)
            Spacer(Modifier.height(9.dp))
            BodyText("Buscador guarda las nuevas notificaciones localmente. Si activas voluntariamente la sincronización automática, las nuevas vistas previas también se envían a tu servidor HTTPS, incluso en segundo plano. Puedes detener esos envíos desde aquí. No recupera chats antiguos.")
            Spacer(Modifier.height(12.dp))
            Text(
                "BORRAR TODO EL HISTORIAL LOCAL",
                color = if (records.isNotEmpty()) Red else Soft,
                fontWeight = FontWeight.Black, fontSize = 11.sp,
                modifier = Modifier.clickable(enabled = records.isNotEmpty()) { showClearConfirmation = true }
                    .padding(vertical = 8.dp)
            )
        }
    }
}
