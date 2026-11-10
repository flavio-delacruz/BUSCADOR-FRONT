import React from "react";
import { Pressable, StyleSheet, Text, View } from "react-native";
import { useRouter } from "expo-router";
import { type CatalogItem } from "../data/catalog";
import { useFavorites } from "../state/FavoritesContext";
import { Artwork } from "./Artwork";
import { theme } from "../theme";

export function CatalogCard({ item }: { item: CatalogItem }) {
  const router = useRouter();
  const { isFavorite, toggle } = useFavorites();
  const saved = isFavorite(item.id);
  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel={"Leer: " + item.title}
      style={styles.card}
      onPress={() => router.push({ pathname: "/detail/[id]", params: { id: item.id } })}
    >
      <View style={styles.visual}><Artwork accent={item.accent} icon={item.icon} height={112} compact /></View>
      <View style={styles.textCol}>
        <Text style={styles.eyebrow} numberOfLines={1}>{item.category.toUpperCase()}  ·  {item.minutes} MIN</Text>
        <Text numberOfLines={2} style={styles.title}>{item.title}</Text>
        <Text numberOfLines={2} style={styles.description}>{item.description}</Text>
        <View style={styles.bottom}>
          <Text style={styles.read}>LEER AHORA  ↗</Text>
          <Pressable
            accessibilityLabel={saved ? "Quitar de guardados" : "Guardar lectura"}
            accessibilityRole="button"
            hitSlop={12}
            onPress={event => { event.stopPropagation(); toggle(item.id); }}
          >
            <Text style={[styles.bookmark, saved && { color: theme.red }]}>{saved ? "♥" : "♡"}</Text>
          </Pressable>
        </View>
      </View>
    </Pressable>
  );
}
const styles = StyleSheet.create({
  card: { backgroundColor: theme.card, borderColor: theme.border, borderWidth: 1, borderRadius: 22, padding: 12, marginBottom: 12, flexDirection: "row", gap: 12 },
  visual: { width: 102, overflow: "hidden", borderRadius: 14 },
  textCol: { flex: 1, paddingTop: 3 },
  eyebrow: { color: theme.red, fontWeight: "800", letterSpacing: 1.0, fontSize: 9 },
  title: { color: theme.white, fontWeight: "800", lineHeight: 20, fontSize: 15, marginTop: 7 },
  description: { color: theme.muted, fontSize: 11, lineHeight: 16, marginTop: 5 },
  bottom: { flexDirection: "row", justifyContent: "space-between", alignItems: "center", marginTop: 9 },
  read: { color: theme.soft, letterSpacing: 1, fontWeight: "900", fontSize: 9 },
  bookmark: { color: theme.muted, fontSize: 24, lineHeight: 25 }
});
