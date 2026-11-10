import React from "react";
import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { useRouter } from "expo-router";
import { Screen } from "../../src/components/Screen";
import { CatalogCard } from "../../src/components/CatalogCard";
import { Reveal } from "../../src/components/Reveal";
import { catalog } from "../../src/data/catalog";
import { useFavorites } from "../../src/state/FavoritesContext";
import { theme } from "../../src/theme";

export default function Saved() {
  const router = useRouter();
  const { favorites } = useFavorites();
  const items = catalog.filter(item => favorites.includes(item.id));
  return (
    <Screen title="Mi biblioteca" subtitle="LEE MÁS TARDE · TU SELECCIÓN">
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <Reveal delay={40}>
        <View style={styles.hero}>
          <Text style={styles.eyebrow}>TU COLECCIÓN PERSONAL</Text>
          <Text style={styles.count}>{String(items.length).padStart(2, "0")}</Text>
          <Text style={styles.countLabel}>{items.length === 1 ? "historia guardada" : "historias guardadas"}</Text>
          <View style={styles.arc} />
          <Text style={styles.heroFoot}>Aquello que te inspira merece un segundo vistazo.</Text>
        </View>
        </Reveal>
        <View style={styles.header}><Text style={styles.headerTitle}>Tu selección</Text><Text style={styles.headerCount}>{items.length} ELEMENTOS</Text></View>
        {items.map((item, index) => <Reveal key={item.id} delay={100 + index * 65}><CatalogCard item={item} /></Reveal>)}
        {items.length === 0 ? <View style={styles.empty}>
          <Text style={styles.emptySymbol}>♡</Text>
          <Text style={styles.emptyTitle}>Aquí empiezan las buenas ideas.</Text>
          <Text style={styles.emptyBody}>Guarda historias desde Inicio, Radar o Explorar. Encontrarás todo aquí.</Text>
          <Pressable onPress={() => router.push("/(tabs)/search")} style={styles.explore} accessibilityRole="button"><Text style={styles.exploreText}>DESCUBRIR HISTORIAS  ↗</Text></Pressable>
        </View> : null}
        <Text style={styles.note}>Por ahora, los guardados se conservan solo durante esta sesión de la aplicación.</Text>
      </ScrollView>
    </Screen>
  );
}
const styles = StyleSheet.create({
  content: { flexGrow: 1, paddingHorizontal: 20, paddingBottom: 36 },
  hero: { backgroundColor: "#211928", borderColor: "#523046", borderWidth: 1, borderRadius: 24, padding: 23, marginTop: 11, marginBottom: 30, overflow: "hidden" },
  eyebrow: { color: "#E8A1B9", fontSize: 10, fontWeight: "900", letterSpacing: 1.8 },
  count: { color: theme.white, fontSize: 75, fontWeight: "900", letterSpacing: -5, marginTop: 12 },
  countLabel: { color: theme.red, fontSize: 15, fontWeight: "800" },
  arc: { position: "absolute", right: -45, top: -52, borderRadius: 150, width: 240, height: 240, borderColor: "#6B3857", borderWidth: 1, opacity: 0.8 },
  heroFoot: { color: theme.muted, fontSize: 12, marginTop: 22 },
  header: { flexDirection: "row", justifyContent: "space-between", alignItems: "center", marginBottom: 15 },
  headerTitle: { color: theme.white, fontSize: 22, fontWeight: "900" },
  headerCount: { color: theme.soft, fontSize: 9, fontWeight: "900", letterSpacing: 1.3 },
  empty: { alignItems: "center", paddingVertical: 25, paddingHorizontal: 15 },
  emptySymbol: { color: theme.red, fontSize: 64 },
  emptyTitle: { color: theme.white, textAlign: "center", fontSize: 20, fontWeight: "900", marginTop: 8 },
  emptyBody: { color: theme.muted, textAlign: "center", lineHeight: 21, fontSize: 12, marginTop: 12 },
  explore: { backgroundColor: theme.red, paddingHorizontal: 23, paddingVertical: 15, borderRadius: 14, marginTop: 23 },
  exploreText: { color: theme.white, fontWeight: "900", fontSize: 11, letterSpacing: 0.5 },
  note: { color: theme.soft, fontSize: 11, textAlign: "center", marginTop: 23, lineHeight: 17 }
});
