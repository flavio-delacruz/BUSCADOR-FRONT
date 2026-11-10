import React from "react";
import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { useRouter } from "expo-router";
import { SafeAreaView } from "react-native-safe-area-context";
import { Artwork } from "../src/components/Artwork";
import { briefIds, catalog } from "../src/data/catalog";
import { theme } from "../src/theme";

export default function Briefing() {
  const router = useRouter();
  const items = briefIds.map(id => catalog.find(item => item.id === id)).filter((item): item is (typeof catalog)[number] => Boolean(item));
  return (
    <SafeAreaView edges={["top"]} style={styles.screen}>
      <ScrollView contentContainerStyle={styles.content}>
        <Pressable onPress={() => router.back()} accessibilityRole="button" accessibilityLabel="Regresar" style={styles.back}><Text style={styles.backText}>‹  VOLVER A HOY</Text></Pressable>
        <View style={styles.label}><Text style={styles.labelText}>B / BRIEFING</Text></View>
        <Text style={styles.headline}>Tres ideas.{"\n"}<Text style={{ color: theme.red }}>Una nueva mirada.</Text></Text>
        <Text style={styles.deck}>Un recorrido corto por temas que vale la pena conocer. Selecciona una historia para leerla completa.</Text>
        <View style={styles.divider} />
        {items.map((item, index) => (
          <Pressable key={item.id} onPress={() => router.push({ pathname: "/detail/[id]", params: { id: item.id } })} style={styles.story}>
            <View style={styles.storyTop}><Text style={styles.number}>0{index + 1}</Text><Text style={styles.category}>{item.category.toUpperCase()}  ·  {item.minutes} MIN</Text><Text style={styles.open}>↗</Text></View>
            <Artwork accent={item.accent} icon={item.icon} height={110} number={String(index + 1).padStart(2, "0")} compact={false} />
            <Text style={styles.title}>{item.title}</Text>
            <Text style={styles.description}>{item.description}</Text>
          </Pressable>
        ))}
        <Text style={styles.footer}>Contenido editorial de demostración. No son noticias verificadas en tiempo real.</Text>
      </ScrollView>
    </SafeAreaView>
  );
}
const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: theme.background },
  content: { paddingHorizontal: 22, paddingTop: 20, paddingBottom: 40 },
  back: { marginBottom: 28 },
  backText: { color: theme.muted, fontWeight: "900", fontSize: 11, letterSpacing: 1.4 },
  label: { backgroundColor: "#351D2D", borderRadius: 9, alignSelf: "flex-start", paddingHorizontal: 12, paddingVertical: 8 },
  labelText: { color: theme.red, fontWeight: "900", fontSize: 10, letterSpacing: 1.5 },
  headline: { color: theme.white, fontWeight: "900", fontSize: 37, letterSpacing: -1.5, lineHeight: 43, marginTop: 18 },
  deck: { color: theme.muted, fontSize: 14, lineHeight: 22, marginTop: 15 },
  divider: { backgroundColor: theme.border, height: 1, marginVertical: 29 },
  story: { padding: 16, marginBottom: 16, backgroundColor: theme.card, borderColor: theme.border, borderWidth: 1, borderRadius: 22 },
  storyTop: { flexDirection: "row", alignItems: "center", gap: 12, marginBottom: 12 },
  number: { color: theme.red, fontSize: 23, fontWeight: "900" },
  category: { color: theme.soft, fontSize: 10, fontWeight: "900", flex: 1, letterSpacing: 1 },
  open: { color: theme.white, fontSize: 21 },
  title: { color: theme.white, fontSize: 20, fontWeight: "900", letterSpacing: -0.4, marginTop: 14 },
  description: { color: theme.muted, lineHeight: 20, fontSize: 12, marginTop: 8 },
  footer: { color: theme.soft, textAlign: "center", fontSize: 11, lineHeight: 17, marginTop: 14 }
});
