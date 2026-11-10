import React from "react";
import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { useRouter } from "expo-router";
import { Screen } from "../../src/components/Screen";
import { Brand } from "../../src/components/Brand";
import { Reveal } from "../../src/components/Reveal";
import { theme } from "../../src/theme";

function InfoRow({ label, value }: { label: string; value: string }) {
  return <View style={styles.infoRow}><Text style={styles.infoLabel}>{label}</Text><Text style={styles.infoValue}>{value}</Text></View>;
}

export default function Settings() {
  const router = useRouter();
  return (
    <Screen title="Mi espacio" subtitle="LA EXPERIENCIA BUSCADOR">
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <Reveal delay={40}>
        <View style={styles.identity}>
          <Brand size={70} />
          <View style={{ flex: 1 }}><Text style={styles.name}>Buscador</Text><Text style={styles.tagline}>Una nueva forma de descubrir.</Text><Text style={styles.version}>EDICIÓN 1.1.0  ·  HISTORIAL LOCAL</Text></View>
        </View>

        </Reveal>
        <Reveal delay={135}>
        <Text style={styles.sectionTitle}>Tu experiencia</Text>
        <Pressable onPress={() => router.push("/briefing")} style={styles.linkCard} accessibilityRole="button">
          <View style={styles.iconBox}><Text style={styles.icon}>✳</Text></View><View style={{ flex: 1 }}><Text style={styles.linkTitle}>Briefing editorial</Text><Text style={styles.linkSub}>Tres historias para comenzar</Text></View><Text style={styles.linkArrow}>↗</Text>
        </Pressable>
        <Pressable onPress={() => router.push("/(tabs)/radar")} style={styles.linkCard} accessibilityRole="button">
          <View style={styles.iconBox}><Text style={styles.icon}>◎</Text></View><View style={{ flex: 1 }}><Text style={styles.linkTitle}>Mapa Radar</Text><Text style={styles.linkSub}>Explora categorías e intereses</Text></View><Text style={styles.linkArrow}>↗</Text>
        </Pressable>

        </Reveal>
        <Reveal delay={215}>
        <Text style={styles.sectionTitle}>Sobre esta versión</Text>
        <View style={styles.infoCard}>
          <InfoRow label="Estilo visual" value="Oscuro / Rojo editorial" />
          <View style={styles.divider} />
          <InfoRow label="Historias" value="Catálogo demostrativo" />
          <View style={styles.divider} />
          <InfoRow label="Conexión" value="Contenido local, sin noticias en vivo" />
        </View>

        </Reveal>
        <Reveal delay={290}>
        <Text style={styles.sectionTitle}>Privacidad y transparencia</Text>
        <Pressable
          accessibilityRole="button"
          accessibilityLabel="Abrir centro de notificaciones y permisos"
          onPress={() => router.push("/notification-center")}
          style={styles.linkCard}
        >
          <View style={styles.iconBox}><Text style={styles.icon}>◎</Text></View>
          <View style={{ flex: 1 }}>
            <Text style={styles.linkTitle}>Centro de notificaciones</Text>
            <Text style={styles.linkSub}>Transparencia, fuentes y controles de privacidad</Text>
          </View>
          <Text style={styles.linkArrow}>↗</Text>
        </Pressable>
        <View style={styles.infoCard}>
          <InfoRow label="Acceso a notificaciones" value="Disponible en APK nueva; requiere permiso y activación voluntaria." />
          <View style={styles.divider} />
          <InfoRow label="Escritura en otras apps" value="No se registra" />
          <View style={styles.divider} />
          <InfoRow label="Datos de cuenta" value="Sin inicio de sesión en esta versión" />
        </View>

        </Reveal>
        <Text style={styles.footer}>BUSCADOR / HECHO PARA DESCUBRIR{"\n"}Prototipo académico · Datos de muestra</Text>
      </ScrollView>
    </Screen>
  );
}
const styles = StyleSheet.create({
  content: { paddingHorizontal: 20, paddingBottom: 42 },
  identity: { borderRadius: 23, backgroundColor: "#1F1A29", borderColor: "#50324C", borderWidth: 1, padding: 18, marginTop: 8, marginBottom: 28, flexDirection: "row", gap: 17, alignItems: "center" },
  name: { color: theme.white, fontSize: 23, fontWeight: "900", letterSpacing: -0.6 },
  tagline: { color: theme.muted, fontSize: 11, marginTop: 5 },
  version: { color: theme.red, fontSize: 10, fontWeight: "900", letterSpacing: 1.2, marginTop: 13 },
  sectionTitle: { color: theme.white, fontSize: 20, fontWeight: "900", marginBottom: 14, marginTop: 5 },
  linkCard: { backgroundColor: theme.card, borderRadius: 17, borderWidth: 1, borderColor: theme.border, flexDirection: "row", alignItems: "center", gap: 13, padding: 14, marginBottom: 11 },
  iconBox: { backgroundColor: "#382338", width: 45, height: 45, borderRadius: 13, alignItems: "center", justifyContent: "center" },
  icon: { color: theme.lilac, fontSize: 25 },
  linkTitle: { color: theme.white, fontSize: 14, fontWeight: "900" },
  linkSub: { color: theme.soft, marginTop: 5, fontSize: 11 },
  linkArrow: { color: theme.red, fontSize: 21 },
  infoCard: { backgroundColor: theme.card, borderColor: theme.border, borderWidth: 1, borderRadius: 21, paddingHorizontal: 18, marginBottom: 27 },
  infoRow: { paddingVertical: 18 },
  infoLabel: { color: theme.white, fontSize: 12, fontWeight: "900" },
  infoValue: { color: theme.muted, fontSize: 11, lineHeight: 17, marginTop: 6 },
  divider: { backgroundColor: theme.border, height: 1 },
  footer: { color: theme.soft, textAlign: "center", marginTop: 5, fontSize: 10, lineHeight: 21, letterSpacing: 1 }
});
