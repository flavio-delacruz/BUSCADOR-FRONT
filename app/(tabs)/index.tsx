import React, { useEffect, useRef } from "react";
import { Animated, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { useRouter } from "expo-router";
import { Screen } from "../../src/components/Screen";
import { Artwork } from "../../src/components/Artwork";
import { CatalogCard } from "../../src/components/CatalogCard";
import { catalog, categories, type Category } from "../../src/data/catalog";
import { theme } from "../../src/theme";

export default function Home() {
  const router = useRouter();
  const intro = useRef(new Animated.Value(0)).current;
  const lead = catalog[0];
  useEffect(() => {
    Animated.timing(intro, { toValue: 1, duration: 550, useNativeDriver: true }).start();
  }, [intro]);

  function openCategory(category: Category) {
    router.push({ pathname: "/(tabs)/search", params: { category } });
  }

  return (
    <Screen title="BUSCADOR" subtitle="IDEAS · CONTEXTO · DESCUBRIMIENTO">
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <Animated.View style={{ opacity: intro, transform: [{ translateY: intro.interpolate({ inputRange: [0, 1], outputRange: [20, 0] }) }] }}>
          <View style={styles.introRow}>
            <View>
              <Text style={styles.kicker}>TU EDICIÓN PERSONAL</Text>
              <Text style={styles.heading}>Mira más allá{"\n"}<Text style={{ color: theme.red }}>del titular.</Text></Text>
            </View>
            <View style={styles.issue}><Text style={styles.issueBig}>B.</Text><Text style={styles.issueSmall}>01 / EDICIÓN</Text></View>
          </View>

          <Pressable accessibilityRole="button" accessibilityLabel={"Leer destacado: " + lead.title} onPress={() => router.push({ pathname: "/detail/[id]", params: { id: lead.id } })} style={styles.feature}>
            <View style={styles.featureTop}>
              <View style={styles.live}><View style={styles.liveDot} /><Text style={styles.liveText}>EN PORTADA</Text></View>
              <Text style={styles.featureNumber}>01 — 08</Text>
            </View>
            <Artwork accent={lead.accent} icon={lead.icon} number="B / 001" height={174} />
            <View style={styles.featureCopy}>
              <Text style={styles.featureKicker}>{lead.category.toUpperCase()}  /  {lead.minutes} MIN DE LECTURA</Text>
              <Text style={styles.featureTitle}>{lead.title}</Text>
              <Text style={styles.featureBody}>{lead.description}</Text>
              <View style={styles.featureFooter}><Text style={styles.featureLink}>EXPLORAR HISTORIA</Text><Text style={styles.arrow}>↗</Text></View>
            </View>
          </Pressable>
        </Animated.View>

        <Pressable style={styles.briefing} onPress={() => router.push("/briefing")} accessibilityRole="button" accessibilityLabel="Abrir briefing de tres historias">
          <View style={styles.briefIcon}><Text style={styles.briefSymbol}>✳</Text></View>
          <View style={{ flex: 1 }}><Text style={styles.briefSmall}>TU DOSIS DE CONTEXTO</Text><Text style={styles.briefTitle}>El briefing de hoy</Text><Text style={styles.briefSub}>Tres historias para descubrir</Text></View>
          <Text style={styles.briefArrow}>↗</Text>
        </Pressable>

        <View style={styles.sectionTitleRow}><Text style={styles.sectionTitle}>Explora por interés</Text><Text style={styles.sectionMicro}>05 TEMAS</Text></View>
        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.topics}>
          {categories.map((category, index) => <Pressable key={category} style={[styles.topic, index === 0 && styles.topicActive]} onPress={() => openCategory(category)}>
            <Text style={[styles.topicText, index === 0 && styles.topicTextActive]}>{category}  ↗</Text>
          </Pressable>)}
        </ScrollView>

        <View style={styles.sectionTitleRow}><Text style={styles.sectionTitle}>Más para descubrir</Text><Text style={styles.sectionMicro}>SELECCIÓN</Text></View>
        {catalog.slice(1, 5).map(item => <CatalogCard key={item.id} item={item} />)}
        <View style={styles.disclaimer}><Text style={styles.disclaimerText}>◌  Vista previa editorial. Las historias y tendencias son ejemplos, no noticias en tiempo real.</Text></View>
      </ScrollView>
    </Screen>
  );
}

const styles = StyleSheet.create({
  content: { paddingHorizontal: 20, paddingBottom: 40 },
  introRow: { flexDirection: "row", justifyContent: "space-between", alignItems: "flex-end", marginTop: 11, marginBottom: 23 },
  kicker: { color: theme.red, fontWeight: "900", letterSpacing: 2.8, fontSize: 10, marginBottom: 10 },
  heading: { color: theme.white, fontSize: 31, lineHeight: 37, fontWeight: "900", letterSpacing: -1.1 },
  issue: { width: 71, height: 70, borderWidth: 1, borderColor: "#4B2940", borderRadius: 16, backgroundColor: "#241824", justifyContent: "center", alignItems: "center" },
  issueBig: { color: theme.red, fontWeight: "900", fontSize: 25, lineHeight: 30 },
  issueSmall: { color: "#F0A4B6", fontSize: 8, fontWeight: "800", letterSpacing: 0.5 },
  feature: { backgroundColor: theme.card, borderWidth: 1, borderColor: "#3B3043", borderRadius: 26, padding: 12, overflow: "hidden" },
  featureTop: { paddingHorizontal: 6, marginBottom: 12, flexDirection: "row", alignItems: "center", justifyContent: "space-between" },
  live: { flexDirection: "row", alignItems: "center", gap: 7 },
  liveDot: { width: 7, height: 7, borderRadius: 4, backgroundColor: theme.red },
  liveText: { color: theme.white, fontWeight: "900", fontSize: 10, letterSpacing: 1.6 },
  featureNumber: { color: theme.soft, fontWeight: "800", fontSize: 11, letterSpacing: 1 },
  featureCopy: { padding: 9, paddingBottom: 5 },
  featureKicker: { color: "#F88FA2", fontSize: 10, fontWeight: "900", letterSpacing: 1.2, marginTop: 10 },
  featureTitle: { color: theme.white, fontSize: 25, lineHeight: 30, fontWeight: "900", letterSpacing: -0.8, marginTop: 10 },
  featureBody: { color: theme.muted, fontSize: 13, lineHeight: 19, marginTop: 9 },
  featureFooter: { marginTop: 20, borderTopWidth: 1, borderTopColor: theme.border, paddingTop: 13, flexDirection: "row", alignItems: "center", justifyContent: "space-between" },
  featureLink: { color: theme.white, fontWeight: "900", letterSpacing: 1.4, fontSize: 10 },
  arrow: { color: theme.red, fontWeight: "900", fontSize: 24 },
  briefing: { backgroundColor: "#2C2130", borderWidth: 1, borderColor: "#5E3B53", borderRadius: 22, padding: 16, marginTop: 18, marginBottom: 32, flexDirection: "row", gap: 12, alignItems: "center" },
  briefIcon: { width: 49, height: 49, borderRadius: 15, backgroundColor: "#4B3046", justifyContent: "center", alignItems: "center" },
  briefSymbol: { color: theme.lilac, fontSize: 29 },
  briefSmall: { color: "#D1A2CA", fontSize: 9, fontWeight: "800", letterSpacing: 1.3 },
  briefTitle: { color: theme.white, fontSize: 16, fontWeight: "900", marginTop: 4 },
  briefSub: { color: "#BEAFC2", fontSize: 11, marginTop: 4 },
  briefArrow: { color: theme.lilac, fontSize: 24 },
  sectionTitleRow: { flexDirection: "row", justifyContent: "space-between", alignItems: "center", marginBottom: 14 },
  sectionTitle: { color: theme.white, fontSize: 21, fontWeight: "900", letterSpacing: -0.6 },
  sectionMicro: { color: theme.soft, fontWeight: "900", letterSpacing: 1.4, fontSize: 9 },
  topics: { gap: 9, paddingBottom: 29 },
  topic: { borderRadius: 14, borderColor: theme.border, borderWidth: 1, paddingHorizontal: 14, paddingVertical: 12, backgroundColor: theme.card },
  topicActive: { borderColor: "#81354B", backgroundColor: "#361E2B" },
  topicText: { color: theme.muted, fontSize: 12, fontWeight: "800" },
  topicTextActive: { color: "#FFC2CF" },
  disclaimer: { paddingVertical: 17 },
  disclaimerText: { color: theme.soft, lineHeight: 17, fontSize: 11, textAlign: "center" }
});
