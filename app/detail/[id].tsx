import React from "react";
import { useLocalSearchParams, useRouter } from "expo-router";
import { Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
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
      <ScrollView contentContainerStyle={styles.content}>
        <Pressable onPress={() => router.back()} style={styles.back}><Text style={styles.backText}>‹  Regresar</Text></Pressable>
        {item ? <>
          <Text style={styles.category}>{item.category.toUpperCase()}</Text>
          <Text style={styles.title}>{item.title}</Text>
          <Text style={styles.body}>{item.description}</Text>
          <View style={styles.divider}/>
          <Text style={styles.small}>PALABRAS CLAVE</Text>
          <Text style={styles.body}>{item.tags.map(t => "#" + t).join("    ")}</Text>
          <Pressable style={styles.save} onPress={() => toggle(item.id)}><Text style={styles.saveText}>{isFavorite(item.id) ? "♥  Quitar de guardados" : "♡  Guardar recurso"}</Text></Pressable>
        </> : <Text style={styles.body}>No se encontró este recurso.</Text>}
      </ScrollView>
    </SafeAreaView>
  );
}
const styles = StyleSheet.create({
  screen: { backgroundColor: theme.background, flex: 1 },
  content: { padding: 24 },
  back: { marginBottom: 48 },
  backText: { color: theme.muted, fontWeight: "700", fontSize: 16 },
  category: { color: theme.red, fontSize: 11, fontWeight: "900", letterSpacing: 2, marginBottom: 12 },
  title: { color: theme.white, fontWeight: "900", fontSize: 34, letterSpacing: -1 },
  body: { color: theme.muted, fontSize: 15, lineHeight: 24, marginTop: 22 },
  divider: { height: 1, backgroundColor: theme.border, marginTop: 30 },
  small: { color: theme.soft, fontSize: 11, letterSpacing: 2, fontWeight: "800", marginTop: 25 },
  save: { marginTop: 45, padding: 16, backgroundColor: theme.red, borderRadius: 15, alignItems: "center" },
  saveText: { color: "#FFFFFF", fontWeight: "800" }
});
