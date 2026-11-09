import React, { useMemo, useState } from "react";
import { Pressable, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";
import { Screen } from "../../src/components/Screen";
import { CatalogCard } from "../../src/components/CatalogCard";
import { catalog, categories, type Category } from "../../src/data/catalog";
import { theme } from "../../src/theme";

export default function Search() {
  const [query, setQuery] = useState("");
  const [category, setCategory] = useState<Category | "Todas">("Todas");
  const results = useMemo(() => {
    const text = query.trim().toLocaleLowerCase("es");
    return catalog.filter(item =>
      (category === "Todas" || item.category === category) &&
      (!text || [item.title, item.description, item.category, ...item.tags].some(value => value.toLocaleLowerCase("es").includes(text)))
    );
  }, [query, category]);
  return (
    <Screen title="Explorar" subtitle="Busca información y descubre ideas">
      <ScrollView keyboardShouldPersistTaps="handled" contentContainerStyle={styles.content}>
        <View style={styles.searchBox}>
          <Text style={styles.icon}>⌕</Text>
          <TextInput value={query} onChangeText={setQuery} placeholder="¿Qué quieres buscar?" placeholderTextColor={theme.soft} style={styles.input} returnKeyType="search" accessibilityLabel="Texto de búsqueda" />
          {query ? <Pressable onPress={() => setQuery("")} accessibilityLabel="Borrar búsqueda"><Text style={styles.clear}>×</Text></Pressable> : null}
        </View>
        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.filters}>
          {(["Todas", ...categories] as const).map(value => (
            <Pressable key={value} onPress={() => setCategory(value)} style={[styles.chip, category === value && styles.activeChip]}>
              <Text style={[styles.chipLabel, category === value && styles.activeLabel]}>{value}</Text>
            </Pressable>
          ))}
        </ScrollView>
        <Text style={styles.resultCount}>{results.length} {results.length === 1 ? "resultado" : "resultados"}</Text>
        {results.map(item => <CatalogCard key={item.id} item={item} />)}
        {results.length === 0 ? <View style={styles.empty}><Text style={styles.emptyTitle}>No encontramos resultados</Text><Text style={styles.emptyCopy}>Prueba con otra palabra o categoría.</Text></View> : null}
      </ScrollView>
    </Screen>
  );
}
const styles = StyleSheet.create({
  content: { paddingHorizontal: 22, paddingBottom: 38 },
  searchBox: { backgroundColor: theme.cardAlt, borderColor: theme.border, borderWidth: 1, borderRadius: 17, height: 56, flexDirection: "row", alignItems: "center", paddingHorizontal: 16 },
  icon: { color: theme.red, fontSize: 31, marginRight: 10 },
  input: { flex: 1, color: theme.white, fontSize: 14 },
  clear: { color: theme.muted, fontSize: 24 },
  filters: { gap: 8, paddingVertical: 19 },
  chip: { borderWidth: 1, borderColor: theme.border, borderRadius: 22, paddingVertical: 10, paddingHorizontal: 15 },
  activeChip: { backgroundColor: theme.red, borderColor: theme.red },
  chipLabel: { color: theme.muted, fontWeight: "700", fontSize: 12 },
  activeLabel: { color: "#FFFFFF" },
  resultCount: { color: theme.muted, fontSize: 12, marginBottom: 15 },
  empty: { padding: 30, alignItems: "center" },
  emptyTitle: { color: theme.white, fontSize: 17, fontWeight: "800" },
  emptyCopy: { color: theme.muted, textAlign: "center", marginTop: 7 }
});
