import React from "react";
import { ScrollView, StyleSheet, Text, View } from "react-native";
import { Screen } from "../../src/components/Screen";
import { CatalogCard } from "../../src/components/CatalogCard";
import { catalog } from "../../src/data/catalog";
import { useFavorites } from "../../src/state/FavoritesContext";
import { theme } from "../../src/theme";

export default function Saved() {
  const { favorites } = useFavorites();
  const items = catalog.filter(item => favorites.includes(item.id));
  return (
    <Screen title="Guardados" subtitle="Tus recursos favoritos">
      <ScrollView contentContainerStyle={styles.content}>
        <Text style={styles.counter}>{items.length} elementos guardados</Text>
        {items.map(item => <CatalogCard key={item.id} item={item} />)}
        {items.length === 0 ? <View style={styles.empty}><Text style={styles.symbol}>♡</Text><Text style={styles.title}>Tu biblioteca está vacía</Text><Text style={styles.copy}>Toca el corazón de un resultado para guardarlo aquí.</Text></View> : null}
        <Text style={styles.note}>Versión inicial: los guardados se mantienen mientras la aplicación esté abierta.</Text>
      </ScrollView>
    </Screen>
  );
}
const styles = StyleSheet.create({
  content: { flexGrow: 1, paddingHorizontal: 22, paddingBottom: 30 },
  counter: { color: theme.muted, fontSize: 12, marginBottom: 16 },
  empty: { flex: 1, alignItems: "center", justifyContent: "center", paddingBottom: 55 },
  symbol: { color: theme.red, fontSize: 68, marginBottom: 12 },
  title: { color: theme.white, fontSize: 20, fontWeight: "800" },
  copy: { color: theme.muted, textAlign: "center", marginTop: 12, lineHeight: 20 },
  note: { color: theme.soft, fontSize: 11, textAlign: "center", marginTop: 10 }
});
