import React from "react";
import { useLocalSearchParams, useRouter } from "expo-router";
import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Artwork } from "../../src/components/Artwork";
import { catalog } from "../../src/data/catalog";
import { useFavorites } from "../../src/state/FavoritesContext";
import { theme } from "../../src/theme";

export default function Detail() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const router = useRouter();
  const { toggle, isFavorite } = useFavorites();
  const item = catalog.find(candidate => candidate.id === id);
  return (
    <SafeAreaView edges={["top"]} style={styles.screen}>
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <Pressable accessibilityRole="button" accessibilityLabel="Regresar" onPress={() => router.back()} style={styles.back}><Text style={styles.backText}>‹   VOLVER</Text></Pressable>
        {item ? <>
          <View style={styles.topRow}><Text style={styles.kicker}>{item.kicker}</Text><Text style={styles.demo}>LECTURA DEMO</Text></View>
          <Text style={styles.title}>{item.title}</Text>
          <Text style={styles.subtitle}>{item.description}</Text>
          <View style={styles.meta}><Text style={styles.metaText}>{item.category.toUpperCase()}</Text><Text style={styles.metaDot}>·</Text><Text style={styles.metaText}>{item.minutes} MIN DE LECTURA</Text></View>
          <Artwork accent={item.accent} icon={item.icon} number={"B / " + item.id.padStart(3, "0")} height={218} />
          <View style={styles.actions}>
            <Text style={styles.actionLabel}>UNA HISTORIA PARA EXPLORAR</Text>
            <Pressable accessibilityRole="button" accessibilityLabel={isFavorite(item.id) ? "Quitar guardado" : "Guardar historia"} onPress={() => toggle(item.id)} style={styles.save}>
              <Text style={styles.saveText}>{isFavorite(item.id) ? "♥  GUARDADA" : "♡  GUARDAR"}</Text>
            </Pressable>
          </View>
          {item.body.map((paragraph, index) => <Text key={index} style={styles.paragraph}>{paragraph}</Text>)}
          <View style={styles.tags}>{item.tags.map(tag => <View key={tag} style={styles.tag}><Text style={styles.tagText}>#{tag}</Text></View>)}</View>
          <View style={styles.end}><Text style={styles.endSymbol}>B.</Text><Text style={styles.endText}>El conocimiento comienza con una pregunta.</Text></View>
        </> : <View style={styles.notFound}><Text style={styles.notFoundText}>No encontramos esta historia.</Text><Pressable onPress={() => router.back()}><Text style={styles.backText}>Volver</Text></Pressable></View>}
      </ScrollView>
    </SafeAreaView>
  );
}
const styles = StyleSheet.create({
  screen: { backgroundColor: theme.background, flex: 1 },
  content: { paddingHorizontal: 22, paddingTop: 19, paddingBottom: 42 },
  back: { marginBottom: 29 },
  backText: { color: theme.muted, fontWeight: "900", fontSize: 11, letterSpacing: 1.6 },
  topRow: { flexDirection: "row", justifyContent: "space-between", alignItems: "center" },
  kicker: { color: theme.red, fontSize: 10, letterSpacing: 1.8, fontWeight: "900" },
  demo: { color: theme.soft, fontSize: 9, fontWeight: "900", letterSpacing: 1.2 },
  title: { fontWeight: "900", fontSize: 34, color: theme.white, lineHeight: 40, letterSpacing: -1.2, marginTop: 17 },
  subtitle: { color: theme.muted, fontSize: 15, lineHeight: 23, marginTop: 15 },
  meta: { flexDirection: "row", alignItems: "center", gap: 12, marginVertical: 23 },
  metaText: { color: theme.soft, fontSize: 10, fontWeight: "900", letterSpacing: 1.1 },
  metaDot: { color: theme.red, fontSize: 16 },
  actions: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", borderBottomWidth: 1, borderBottomColor: theme.border, paddingVertical: 21, marginBottom: 17 },
  actionLabel: { color: theme.soft, flex: 1, fontSize: 9, fontWeight: "900", letterSpacing: 0.9 },
  save: { backgroundColor: "#342033", borderColor: "#67344F", borderWidth: 1, borderRadius: 11, paddingVertical: 12, paddingHorizontal: 14 },
  saveText: { color: "#FFADB9", fontWeight: "900", fontSize: 10 },
  paragraph: { color: "#D2D6DF", lineHeight: 27, fontSize: 15, marginTop: 18 },
  tags: { flexDirection: "row", flexWrap: "wrap", gap: 8, marginTop: 28 },
  tag: { borderRadius: 9, backgroundColor: theme.cardAlt, paddingHorizontal: 11, paddingVertical: 8 },
  tagText: { color: theme.muted, fontWeight: "800", fontSize: 11 },
  end: { borderTopWidth: 1, borderTopColor: theme.border, alignItems: "center", marginTop: 38, paddingTop: 30 },
  endSymbol: { color: theme.red, fontSize: 32, fontWeight: "900" },
  endText: { color: theme.soft, fontSize: 12, marginTop: 12 },
  notFound: { paddingTop: 50 },
  notFoundText: { color: theme.white, fontSize: 19, fontWeight: "900", marginBottom: 20 }
});
