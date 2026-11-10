import React from "react";
import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { useRouter } from "expo-router";
import { Screen } from "../../src/components/Screen";
import { CatalogCard } from "../../src/components/CatalogCard";
import { Reveal } from "../../src/components/Reveal";
import { RadarPulse } from "../../src/components/RadarPulse";
import { catalog, radarSignals } from "../../src/data/catalog";
import { theme } from "../../src/theme";

export default function Radar() {
  const router = useRouter();
  return (
    <Screen title="Radar" subtitle="MAPA DE IDEAS · VERSIÓN DEMO">
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <Reveal delay={40}>
        <Text style={styles.eyebrow}>SEÑALES PARA EXPLORAR</Text>
        <Text style={styles.headline}>Detecta temas.{"\n"}<Text style={{ color: theme.red }}>Conecta ideas.</Text></Text>
        <Text style={styles.intro}>Una perspectiva diferente para descubrir conversaciones y campos que vale la pena investigar.</Text>

        </Reveal>
        <Reveal delay={150}>
        <View style={styles.radialCard}>
          <View style={styles.radialTop}><View><Text style={styles.micro}>EL MAPA RADAR</Text><Text style={styles.panelTitle}>Panorama de intereses</Text></View><View style={styles.mock}><Text style={styles.mockText}>SIMULADO</Text></View></View>
          <View style={styles.orbit}>
            <RadarPulse />
            <View style={styles.orbitOuter}><View style={styles.orbitMiddle}><View style={styles.orbitInner}><Text style={styles.orbitCore}>B</Text></View></View></View>
            <View style={[styles.orbitDot, { left: "19%", top: "26%", backgroundColor: theme.red }]} />
            <View style={[styles.orbitDot, { left: "72%", top: "35%", backgroundColor: theme.cyan }]} />
            <View style={[styles.orbitDot, { left: "43%", top: "85%", backgroundColor: theme.lilac }]} />
            <Text style={[styles.orbitLabel, { left: 3, top: 14 }]}>TECH</Text>
            <Text style={[styles.orbitLabel, { right: 0, top: 45 }]}>CIENCIA</Text>
            <Text style={[styles.orbitLabel, { left: 20, bottom: 8 }]}>IDEAS</Text>
          </View>
          <Text style={styles.orbitNote}>Representación visual ilustrativa. No se utilizan datos de seguimiento ni estadísticas reales.</Text>
        </View>

        </Reveal>
        <Reveal delay={230}>
        <View style={styles.sectionRow}><Text style={styles.sectionTitle}>Índice de interés</Text><Text style={styles.demoText}>EJEMPLO</Text></View>
        <View style={styles.signals}>
          {radarSignals.map((signal, i) => (
            <Pressable key={signal.title} onPress={() => router.push({ pathname: "/detail/[id]", params: { id: signal.articleId } })} style={styles.signal} accessibilityRole="button" accessibilityLabel={"Explorar " + signal.title}>
              <View style={styles.signalTop}><Text style={styles.index}>0{i + 1}</Text><Text style={styles.signalTitle}>{signal.title}</Text><Text style={styles.score}>{signal.score}</Text></View>
              <View style={styles.track}><View style={[styles.fill, { width: `${signal.score}%` as `${number}%`, backgroundColor: signal.tint }]} /></View>
              <Text style={styles.signalCategory}>{signal.category.toUpperCase()}  ·  VER HISTORIA  ↗</Text>
            </Pressable>
          ))}
        </View>
        </Reveal>
        <View style={styles.sectionRow}><Text style={styles.sectionTitle}>Para profundizar</Text><Text style={styles.demoText}>LECTURAS</Text></View>
        {catalog.slice(4, 7).map((item, index) => <Reveal key={item.id} delay={300 + index * 60}><CatalogCard item={item} /></Reveal>)}
      </ScrollView>
    </Screen>
  );
}

const styles = StyleSheet.create({
  content: { paddingHorizontal: 20, paddingBottom: 40 },
  eyebrow: { color: theme.red, fontSize: 10, fontWeight: "900", letterSpacing: 2, marginTop: 10 },
  headline: { color: theme.white, fontSize: 31, fontWeight: "900", lineHeight: 39, letterSpacing: -1, marginTop: 8 },
  intro: { color: theme.muted, fontSize: 13, lineHeight: 21, marginTop: 11, marginBottom: 23 },
  radialCard: { backgroundColor: "#161B28", borderColor: "#30374B", borderWidth: 1, borderRadius: 24, padding: 17, marginBottom: 30 },
  radialTop: { flexDirection: "row", alignItems: "center", justifyContent: "space-between" },
  micro: { color: theme.cyan, fontSize: 9, fontWeight: "900", letterSpacing: 1.7 },
  panelTitle: { color: theme.white, fontWeight: "900", fontSize: 16, marginTop: 6 },
  mock: { backgroundColor: "#193339", paddingHorizontal: 8, paddingVertical: 6, borderRadius: 7 },
  mockText: { color: theme.cyan, fontSize: 9, fontWeight: "900", letterSpacing: 0.8 },
  orbit: { height: 225, justifyContent: "center", alignItems: "center" },
  orbitOuter: { width: 202, height: 202, borderRadius: 101, borderColor: "#415166", borderWidth: 1, alignItems: "center", justifyContent: "center" },
  orbitMiddle: { width: 149, height: 149, borderRadius: 75, borderColor: "#485574", borderWidth: 1, alignItems: "center", justifyContent: "center" },
  orbitInner: { width: 91, height: 91, borderRadius: 46, backgroundColor: "#2A1729", borderColor: "#96304E", borderWidth: 1, justifyContent: "center", alignItems: "center" },
  orbitCore: { color: theme.red, fontWeight: "900", fontSize: 42 },
  orbitDot: { width: 11, height: 11, borderRadius: 6, position: "absolute" },
  orbitLabel: { position: "absolute", color: theme.soft, fontWeight: "900", fontSize: 8, letterSpacing: 1 },
  orbitNote: { color: theme.soft, textAlign: "center", lineHeight: 16, fontSize: 10 },
  sectionRow: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", marginBottom: 15 },
  sectionTitle: { color: theme.white, fontSize: 21, fontWeight: "900", letterSpacing: -0.4 },
  demoText: { color: theme.soft, fontSize: 9, fontWeight: "900", letterSpacing: 1.2 },
  signals: { marginBottom: 31, backgroundColor: theme.card, borderWidth: 1, borderColor: theme.border, borderRadius: 22, padding: 18, gap: 18 },
  signal: { gap: 8 },
  signalTop: { flexDirection: "row", alignItems: "center", gap: 11 },
  index: { color: theme.red, fontWeight: "900", fontSize: 11 },
  signalTitle: { color: theme.white, fontWeight: "800", fontSize: 13, flex: 1 },
  score: { color: theme.white, fontSize: 17, fontWeight: "900" },
  track: { backgroundColor: "#343A4B", height: 5, borderRadius: 5, overflow: "hidden" },
  fill: { height: 5, borderRadius: 5 },
  signalCategory: { color: theme.soft, fontSize: 9, fontWeight: "800", letterSpacing: 1 }
});
