package com.flaviodelacruz.buscador

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** A persistent opt-in switch, never a hidden or per-notification upload. */
@Composable
fun AutoSyncCard() {
    val context = LocalContext.current
    var enabled by remember { mutableStateOf(AutoSyncSettings.isEnabled(context)) }
    var server by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var agreed by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf("") }
    var workerStatus by remember { mutableStateOf(AutoSyncSettings.status(context)) }

    SurfacePanel {
        Label("3 / RESPALDO REMOTO AUTOMÁTICO", Cyan)
        Spacer(Modifier.height(8.dp))
        Text(
            "MongoDB · Sincronización automática",
            color = White, fontSize = 16.sp, fontWeight = FontWeight.Black
        )
        Spacer(Modifier.height(9.dp))
        BodyText(
            "Con tu autorización, cada notificación nueva que Buscador capture " +
            "se enviará automáticamente por HTTPS al backend Express, incluso en segundo plano. " +
            "El servidor almacena el remitente y el texto cifrados en MongoDB. " +
            "El contenido ya no estará solamente en este teléfono."
        )
        Spacer(Modifier.height(11.dp))
        BodyText(
            "Desactivado por defecto. No se enviarán notificaciones anteriores " +
            "a tu autorización. Puedes cortar el envío en cualquier momento, " +
            "sin desactivar la captura local."
        )

        if (!enabled) {
            Spacer(Modifier.height(13.dp))
            OutlinedTextField(
                value = server,
                onValueChange = { server = it },
                label = { Text("URL de tu servidor HTTPS") },
                placeholder = { Text("https://tu-api.ejemplo.com") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = token,
                onValueChange = { token = it },
                label = { Text("Token privado del servidor") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = agreed, onCheckedChange = { agreed = it })
                Text(
                    "Autorizo que mis nuevas notificaciones de WhatsApp, Facebook y TikTok, " +
                    "con remitente y texto, se suban automáticamente a mi servidor, " +
                    "aunque la aplicación no esté abierta.",
                    color = Muted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    if (enabled) "Sincronización activada" else "Sincronización apagada",
                    color = White, fontWeight = FontWeight.Bold, fontSize = 15.sp
                )
                BodyText(
                    if (enabled) "Envíos automáticos al recibir nuevas notificaciones."
                    else "Actívala una sola vez; no hay que enviar cada mensaje manualmente.",
                    Soft
                )
            }
            Switch(
                checked = enabled,
                onCheckedChange = { turnOn ->
                    try {
                        if (turnOn) {
                            if (!agreed) {
                                feedback = "Confirma primero tu autorización de sincronización remota."
                            } else {
                                AutoSyncSettings.enable(context, server, token)
                                token = "" // The token now exists only in Keystore-encrypted local storage.
                                server = ""
                                agreed = false
                                enabled = true
                                feedback = "Autorizada. Los próximos avisos se enviarán automáticamente."
                                NotificationAutoSyncWorker.enqueue(context)
                            }
                        } else {
                            // Disables uploads immediately and clears credentials and queued jobs.
                            AutoSyncSettings.disable(context)
                            NotificationAutoSyncWorker.cancel(context)
                            enabled = false
                            feedback = "Envío automático desactivado y token local eliminado."
                            workerStatus = ""
                        }
                    } catch (_: Exception) {
                        feedback = "No se pudo configurar. Revisa la URL HTTPS y el token privado."
                    }
                }
            )
        }
        if (feedback.isNotBlank()) {
            Spacer(Modifier.height(9.dp))
            BodyText(feedback, if (enabled) Cyan else Muted)
        }
        if (enabled) {
            Spacer(Modifier.height(10.dp))
            Text(
                "ESTADO DEL ÚLTIMO ENVÍO · CONSULTAR ↻",
                color = Cyan, fontSize = 10.sp, fontWeight = FontWeight.Black,
                modifier = Modifier
                    .padding(vertical = 7.dp)
                    .clickable { workerStatus = AutoSyncSettings.status(context) }
            )
            if (workerStatus.isNotBlank()) BodyText(workerStatus, Soft)
        }
        Spacer(Modifier.height(9.dp))
        BodyText(
            "El teléfono necesita conexión para enviar. Android puede retrasar " +
            "los trabajos en segundo plano; se reintentan cuando es posible. " +
            "Para borrar copias remotas usa el endpoint protegido DELETE del servidor.",
            Soft
        )
    }
}
