package com.flaviodelacruz.buscador

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * User-initiated HTTPS export only. No background sync, telemetry, saved
 * token, or uploads without a visible tap and confirmation.
 */
private fun postBatch(endpoint: URL, token: String, items: List<Map<String, Any>>) {
    val payload = JSONObject()
    val arr = JSONArray()
    for (item in items) {
        arr.put(
            JSONObject()
                .put("id", item["id"] as? String ?: "")
                .put("source", item["source"] as? String ?: "")
                .put("title", item["title"] as? String ?: "")
                .put("message", item["message"] as? String ?: "")
                .put("time", (item["time"] as? Number)?.toLong() ?: 0)
        )
    }
    payload.put("items", arr)
    val connection = (endpoint.openConnection() as HttpURLConnection)
    try {
        connection.requestMethod = "POST"
        connection.connectTimeout = 12_000
        connection.readTimeout = 12_000
        connection.doOutput = true
        connection.setRequestProperty("Authorization", "Bearer $token")
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
        connection.setRequestProperty("Accept", "application/json")
        connection.outputStream.use { it.write(payload.toString().toByteArray(Charsets.UTF_8)) }
        val resultCode = connection.responseCode
        if (resultCode !in 200..299) {
            throw IllegalStateException("Servidor HTTP $resultCode")
        }
    } finally {
        connection.disconnect()
    }
}

private fun endpointFor(base: String): URL {
    val url = URL(base.trim().trimEnd('/'))
    require(url.protocol.equals("https", ignoreCase = true)) { "Usa una dirección HTTPS segura." }
    require(url.host.isNotBlank() && url.userInfo.isNullOrBlank()) { "Dirección de servidor inválida." }
    require(url.query == null && url.ref == null) { "Introduce solo la dirección base del servidor." }
    require(url.path.isBlank() || url.path == "/") {
        "Introduce la URL base (sin rutas), por ejemplo https://api.ejemplo.com"
    }
    return URL("https", url.host, if (url.port == -1) 443 else url.port, "/api/notifications/sync")
}

@Composable
fun ManualSyncCard(records: List<Map<String, Any>>) {
    var server by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var understood by remember { mutableStateOf(false) }
    var confirmation by remember { mutableStateOf(false) }
    var sending by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    if (confirmation) {
        AlertDialog(
            onDismissRequest = { confirmation = false },
            title = { Text("Confirmar envío al servidor") },
            text = {
                Text(
                    "Se enviarán ${records.size} notificaciones, con sus remitentes y vistas previas, " +
                    "al servidor que indicaste. Los mensajes dejarán de estar solo en tu teléfono. " +
                    "¿Quieres continuar?"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmation = false
                    if (sending) return@TextButton
                    val endpoint = try { endpointFor(server) }
                        catch (_: Exception) {
                            feedback = "La dirección debe ser HTTPS y contener solo el dominio."
                            return@TextButton
                        }
                    val auth = token.trim()
                    if (auth.length < 32) {
                        feedback = "El token no es válido."
                        return@TextButton
                    }
                    val selected = records.toList()
                    sending = true
                    feedback = ""
                    scope.launch {
                        val success = withContext(Dispatchers.IO) {
                            try {
                                selected.chunked(40).forEach { postBatch(endpoint, auth, it) }
                                true
                            } catch (_: Exception) {
                                false
                            }
                        }
                        feedback = if (success)
                            "Historial enviado al servidor. Puedes verificarlo con tu cuenta autorizada."
                        else "No se completó el envío. Comprueba HTTPS, el token y la API. Puedes reintentar sin duplicar registros."
                        sending = false
                        token = ""
                        understood = false
                    }
                }) { Text("ENVIAR AHORA", color = Red, fontWeight = FontWeight.Black) }
            },
            dismissButton = {
                TextButton(onClick = { confirmation = false }) { Text("CANCELAR") }
            }
        )
    }

    SurfacePanel {
        Label("3 / SINCRONIZACIÓN REMOTA OPCIONAL", Cyan)
        Spacer(Modifier.height(9.dp))
        Text("Enviar historial a MongoDB", color = White, fontWeight = FontWeight.Black, fontSize = 17.sp)
        Spacer(Modifier.height(7.dp))
        BodyText(
            "Desactivado por defecto. Solo se transmite cuando presionas Enviar y confirmas. " +
            "Los datos permanecen cifrados en el servidor. No se capturan mensajes enviados por ti desde otras aplicaciones."
        )
        Spacer(Modifier.height(15.dp))
        OutlinedTextField(
            value = server, onValueChange = { server = it },
            singleLine = true, label = { Text("Servidor HTTPS") },
            placeholder = { Text("https://tu-api.ejemplo.com") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = token, onValueChange = { token = it },
            singleLine = true, label = { Text("Token privado de sincronización") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = understood, onCheckedChange = { understood = it },
                enabled = !sending
            )
            Text(
                "Autorizo subir el contenido de mis notificaciones al servidor indicado.",
                color = Muted, fontSize = 12.sp, lineHeight = 18.sp,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(11.dp))
        RedAction(
            label = if (sending) "ENVIANDO…" else "ENVIAR ${records.size} NOTIFICACIONES",
            modifier = Modifier,
            onClick = {
                if (!sending && understood && token.isNotBlank() && server.isNotBlank() && records.isNotEmpty()) {
                    confirmation = true
                } else {
                    feedback = "Indica el servidor y el token, marca tu autorización y asegúrate de tener registros."
                }
            }
        )
        if (feedback.isNotBlank()) {
            Spacer(Modifier.height(9.dp))
            BodyText(feedback, if (feedback.startsWith("Historial enviado")) Cyan else Red)
        }
        Spacer(Modifier.height(7.dp))
        BodyText("La app no guarda el token después del envío. Usa solo un servidor de confianza.", Soft)
    }
}
