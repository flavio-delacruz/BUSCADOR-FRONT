import React from "react";
import { Pressable, StyleSheet, Text, View } from "react-native";
import { useRouter } from "expo-router";
import { type CatalogItem } from "../data/catalog";
import { useFavorites } from "../state/FavoritesContext";
import { theme } from "../theme";

export function CatalogCard({ item }: { item: CatalogItem }) {
  const router = useRouter();
  const { isFavorite, toggle } = useFavorites();
  const saved = isFavorite(item.id);
  return (
    <Pressable style={styles.card} onPress={() => router.push({ pathname: "/detail/[id]", params: { id: item.id } })}>
      <View style={styles.top}>
        <View style={styles.category}><Text style={styles.categoryText}>{item.category}</Text></View>
        <Pressable hitSlop={12} accessibilityRole="button" accessibilityLabel={saved ? "Quitar de guardados" : "Guardar"} onPress={() => toggle(item.id)}>
          <Text style={[styles.bookmark, saved && { color: theme.red }]}>{saved ? "♥" : "♡"}</Text>
        </Pressable>
      </View>
      <Text style={styles.title}>{item.title}</Text>
      <Text style={styles.description}>{item.description}</Text>
      <View style={styles.tags}>{item.tags.map(tag => <Text key={tag} style={styles.tag}>#{tag}</Text>)}</View>
    </Pressable>
  );
}
const styles = StyleSheet.create({
  card: { backgroundColor: theme.card, borderColor: theme.border, borderWidth: 1, borderRadius: 20, padding: 18, marginBottom: 12 },
  top: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", marginBottom: 12 },
  category: { backgroundColor: "#34151D", borderRadius: 9, paddingHorizontal: 10, paddingVertical: 5 },
  categoryText: { color: "#FF8796", fontSize: 11, fontWeight: "700" },
  bookmark: { color: theme.muted, fontSize: 27, lineHeight: 30 },
  title: { color: theme.white, fontWeight: "800", fontSize: 18, marginBottom: 6 },
  description: { color: theme.muted, lineHeight: 20, fontSize: 13 },
  tags: { flexDirection: "row", gap: 12, marginTop: 15 },
  tag: { color: theme.soft, fontSize: 11 }
});
