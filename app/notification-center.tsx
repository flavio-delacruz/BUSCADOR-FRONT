import React from "react";
import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { useRouter } from "expo-router";
import { SafeAreaView } from "react-native-safe-area-context";
import { theme } from "../src/theme";

const sources = [
  { name: "WhatsApp", detail: "Solo notificaciones visibles que Android permita leer.", accent: "#4BCB91" },
  { name: "Facebook", detail: "Solo avisos publicados en la bandeja de Android.", accent: "#81A8FF" },
  { name: "TikTok", detail: "Solo notificaciones que la aplicación muestre al sistema.", accent: "#F28CBF" }
];

export default function NotificationCenter() {
  const router = useRouter();
  return (
    <SafeAreaView style={styles.screen} edges={["top", "bottom"]}>
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <Pressable onPress={() => router.back()} accessibilityRole="button" accessibilityLabel="Regresar" style={styles.back}>
          <Text style={styles.backText}>‹   REGRESAR</Text>
        </Pressable>
        <Text style={styles.kicker}>PRIVACIDAD / PERMISOS</Text>
        <Text style={styles.heading}>Centro de{"\n"}<Text style={{ color: theme.red }}>notificaciones.</Text></Text>
        <Text style={styles.intro}>Esta sección explica cómo funcionaría un historial personal de notificaciones. La función todavía no está activa.</Text>
        <View style={styles.status}>
          <View style={styles.indicator} />
          <View style={{ flex: 1 }}>
            <Text style={styles.statusTitle}>Acceso deshabilitado</Text>
            <Text style={styles.statusBody}>Esta versión no solicita permisos ni registra mensajes.</Text>
          </View>
        </View>

        <Text style={styles.section}>FUENTES PROPUESTAS</Text>
        {sources.map(source => (
          <View key={source.name} style={styles.source}>
            <View style={[styles.sourceDot, { backgroundColor: source.accent }]} />
            <View style={{ flex: 1 }}>
              <Text style={styles.sourceTitle}>{source.name}</Text>
              <Text style={styles.sourceDetail}>{source.detail}</Text>
            </View>
            <Text style={styles.off}>INACTIVO</Text>
          </View>
        ))}

        <Text style={styles.section}>ANTES DE ACTIVAR</Text>
        <View style={styles.info}>
          <Text style={styles.infoTitle}>Permiso comprensible y voluntario</Text>
          <Text style={styles.infoBody}>Android pide autorizar el acceso a notificaciones en Ajustes. Este permiso puede exponer mensajes privados, por lo que siempre debe explicarse de forma visible.</Text>
          <View style={styles.line} />
          <Text style={styles.infoTitle}>Control en el teléfono</Text>
          <Text style={styles.infoBody}>Una futura versión deberá incluir inicio y detención, eliminación del historial y almacenamiento local protegido. Ningún contenido debe enviarse automáticamente a MongoDB.</Text>
          <View style={styles.line} />
          <Text style={styles.infoTitle}>Límites de Android</Text>
          <Text style={styles.infoBody}>No recupera mensajes anteriores, conversaciones completas ni contenido que la aplicación de origen haya decidido ocultar.</Text>
        </View>
        <Text style={styles.footer}>PREPARACIÓN DE FUNCIONALIDAD · SIN CAPTURA ACTIVA</Text>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: theme.background },
  content: { paddingHorizontal: 22, paddingTop: 18, paddingBottom: 45 },
  back: { paddingVertical: 10, marginBottom: 25 },
  backText: { color: theme.muted, fontSize: 11, letterSpacing: 1.3, fontWeight: "900" },
  kicker: { color: theme.red, fontSize: 10, fontWeight: "900", letterSpacing: 2 },
  heading: { color: theme.white, fontSize: 34, lineHeight: 40, fontWeight: "900", letterSpacing: -1.2, marginTop: 10 },
  intro: { color: theme.muted, fontSize: 13, lineHeight: 21, marginTop: 14, marginBottom: 22 },
  status: { flexDirection: "row", alignItems: "center", gap: 12, borderRadius: 18, padding: 18, backgroundColor: "#241F2B", borderWidth: 1, borderColor: "#544054" },
  indicator: { width: 11, height: 11, borderRadius: 6, backgroundColor: theme.soft },
  statusTitle: { color: theme.white, fontSize: 15, fontWeight: "900" },
  statusBody: { color: theme.muted, fontSize: 11, lineHeight: 18, marginTop: 4 },
  section: { color: theme.soft, letterSpacing: 1.8, fontSize: 10, fontWeight: "900", marginTop: 27, marginBottom: 13 },
  source: { flexDirection: "row", alignItems: "center", gap: 12, padding: 15, backgroundColor: theme.card, borderRadius: 15, borderWidth: 1, borderColor: theme.border, marginBottom: 10 },
  sourceDot: { width: 10, height: 10, borderRadius: 5 },
  sourceTitle: { color: theme.white, fontWeight: "900", fontSize: 14 },
  sourceDetail: { color: theme.muted, fontSize: 11, lineHeight: 17, marginTop: 5 },
  off: { color: theme.soft, fontSize: 9, fontWeight: "900", letterSpacing: 0.7 },
  info: { borderRadius: 19, borderWidth: 1, borderColor: theme.border, backgroundColor: theme.card, padding: 18 },
  infoTitle: { color: theme.white, fontWeight: "900", fontSize: 14 },
  infoBody: { color: theme.muted, fontSize: 12, lineHeight: 19, marginTop: 9 },
  line: { height: 1, backgroundColor: theme.border, marginVertical: 17 },
  footer: { color: theme.soft, textAlign: "center", marginTop: 26, fontSize: 10, fontWeight: "800", letterSpacing: 0.5 }
});
