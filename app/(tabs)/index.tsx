import React from "react";
import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { useRouter } from "expo-router";
import { Screen } from "../../src/components/Screen";
import { CatalogCard } from "../../src/components/CatalogCard";
import { catalog } from "../../src/data/catalog";
import { theme } from "../../src/theme";

export default function Home() {
  const router = useRouter();
  return (
    <Screen title="Buscador" subtitle="Todo lo que necesitas, en un lugar">
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <View style={styles.hero}>
          <Text style={styles.eyebrow}>EXPLORA SIN LÍMITES</Text>
          <Text style={styles.headline}>Encuentra lo que{"\n"}<Text style={{ color: theme.red }}>te inspira.</Text></Text>
          <Text style={styles.copy}>Busca ideas, organiza recursos y descubre contenido de forma sencilla.</Text>
          <Pressable style={styles.searchButton} onPress={() => router.push("/search")}>
            <Text style={styles.buttonLabel}>⌕  Comenzar a buscar</Text>
            <Text style={styles.buttonArrow}>›</Text>
          </Pressable>
        </View>
        <View style={styles.titleRow}><Text style={styles.sectionTitle}>Descubre hoy</Text><Text style={styles.sectionMeta}>PARA TI</Text></View>
        {catalog.slice(0, 3).map(item => <CatalogCard key={item.id} item={item} />)}
        <Text style={styles.footnote}>Contenido de demostración para la presentación del proyecto.</Text>
      </ScrollView>
    </Screen>
  );
}
const styles = StyleSheet.create({
  content: { paddingHorizontal: 22, paddingBottom: 40 },
  hero: { backgroundColor: "#1A0E13", borderColor: "#5F1828", borderWidth: 1, borderRadius: 26, padding: 24, marginTop: 5, marginBottom: 26 },
  eyebrow: { color: "#FF8898", fontWeight: "800", letterSpacing: 2.5, fontSize: 10, marginBottom: 14 },
  headline: { color: theme.white, fontSize: 32, fontWeight: "900", lineHeight: 39, letterSpacing: -1.3 },
  copy: { color: "#C1ADB4", fontSize: 13, lineHeight: 21, marginTop: 12, marginBottom: 22 },
  searchButton: { padding: 14, backgroundColor: theme.red, borderRadius: 14, flexDirection: "row", justifyContent: "space-between", alignItems: "center" },
  buttonLabel: { color: "#FFFFFF", fontWeight: "800", fontSize: 14 },
  buttonArrow: { color: "#FFFFFF", fontSize: 23 },
  titleRow: { flexDirection: "row", justifyContent: "space-between", alignItems: "center", marginBottom: 14 },
  sectionTitle: { color: theme.white, fontWeight: "800", fontSize: 21 },
  sectionMeta: { color: theme.red, fontSize: 10, letterSpacing: 1.7, fontWeight: "800" },
  footnote: { color: theme.soft, textAlign: "center", fontSize: 11, marginTop: 16 }
});
