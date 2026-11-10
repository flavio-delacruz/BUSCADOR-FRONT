import React, { useEffect, useMemo, useState } from "react";
import { Pressable, ScrollView, StyleSheet, Text, TextInput, View } from "react-native";
import { useLocalSearchParams } from "expo-router";
import { Screen } from "../../src/components/Screen";
import { CatalogCard } from "../../src/components/CatalogCard";
import { catalog, categories, type Category } from "../../src/data/catalog";
import { theme } from "../../src/theme";

const suggestions = ["Ciudades", "Inteligencia artificial", "Océano"];

export default function Search() {
  const params = useLocalSearchParams<{ category?: string }>();
  const [query, setQuery] = useState("");
  const [category, setCategory] = useState<Category | "Todo">("Todo");

  useEffect(() => {
    if (categories.some(value => value === params.category)) setCategory(params.category as Category);
  }, [params.category]);

  const results = useMemo(() => {
    const term = query.trim().toLocaleLowerCase("es");
    return catalog.filter(item =>
      (category === "Todo" || item.category === category) &&
      (!term || [item.title, item.description, item.kicker, item.category, ...item.tags]
        .some(value => value.toLocaleLowerCase("es").includes(term)))
    );
  }, [query, category]);

  return (
    <Screen title="Explorar" subtitle="BUSCA · DESCUBRE · GUARDA">
      <ScrollView keyboardShouldPersistTaps="handled" contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <Text style={styles.eyebrow}>BUSCADOR / EXPLORAR</Text>
        <Text style={styles.heading}>Toda gran idea{"\n"}<Text style={{ color: theme.red }}>empieza aquí.</Text></Text>
        <Text style={styles.deck}>Encuentra nuevas perspectivas sobre los temas que te interesan.</Text>

        <View style={styles.searchBox}>
          <Text style={styles.searchIcon}>⌕</Text>
          <TextInput value={query} onChangeText={setQuery} placeholder="Busca una idea, tema o palabra" placeholderTextColor={theme.soft} style={styles.input} returnKeyType="search" accessibilityLabel="Buscar en las historias" />
          {query.length > 0 ? <Pressable accessibilityRole="button" accessibilityLabel="Limpiar búsqueda" onPress={() => setQuery("")}><Text style={styles.clear}>×</Text></Pressable> : null}
        </View>

        {query.length === 0 ? <View style={styles.suggestBlock}>
          <Text style={styles.suggestTitle}>BÚSQUEDAS PARA EMPEZAR</Text>
          <View style={styles.suggestRow}>
            {suggestions.map(text => <Pressable key={text} onPress={() => { setCategory("Todo"); setQuery(text); }} style={styles.suggestion}><Text style={styles.suggestText}>↗  {text}</Text></Pressable>)}
          </View>
        </View> : null}

        <Text style={styles.filtersLabel}>FILTRAR POR UNIVERSO</Text>
        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.filters}>
          {(["Todo", ...categories] as const).map(value => (
            <Pressable key={value} onPress={() => setCategory(value)} accessibilityRole="button" style={[styles.chip, category === value && styles.activeChip]}>
              <Text style={[styles.chipLabel, category === value && styles.activeLabel]}>{value}</Text>
            </Pressable>
          ))}
        </ScrollView>

        <View style={styles.resultHead}><Text style={styles.resultTitle}>Historias</Text><Text style={styles.resultCount}>{results.length} RESULTADOS</Text></View>
        {results.map(item => <CatalogCard key={item.id} item={item} />)}
        {results.length === 0 ? <View style={styles.empty}><Text style={styles.emptyGlyph}>⌕</Text><Text style={styles.emptyTitle}>Sin coincidencias</Text><Text style={styles.emptyCopy}>Prueba con otra palabra o cambia el filtro.</Text><Pressable style={styles.reset} onPress={() => { setQuery(""); setCategory("Todo"); }}><Text style={styles.resetText}>REINICIAR BÚSQUEDA  ↗</Text></Pressable></View> : null}
        <Text style={styles.note}>Catálogo de demostración. Aún no hay noticias en vivo ni búsqueda en internet.</Text>
      </ScrollView>
    </Screen>
  );
}
const styles = StyleSheet.create({
  content: { paddingHorizontal: 20, paddingBottom: 37 },
  eyebrow: { color: theme.red, fontWeight: "900", letterSpacing: 2, fontSize: 10, marginTop: 10 },
  heading: { color: theme.white, fontWeight: "900", fontSize: 31, lineHeight: 38, letterSpacing: -1.1, marginTop: 10 },
  deck: { color: theme.muted, fontSize: 13, lineHeight: 20, marginTop: 9, marginBottom: 23 },
  searchBox: { backgroundColor: "#1C2230", borderColor: "#505169", borderWidth: 1, borderRadius: 18, height: 61, flexDirection: "row", alignItems: "center", paddingHorizontal: 16 },
  searchIcon: { color: theme.red, fontSize: 32, marginRight: 10 },
  input: { color: theme.white, flex: 1, fontSize: 13 },
  clear: { color: theme.muted, fontSize: 24, paddingLeft: 8 },
  suggestBlock: { marginTop: 21 },
  suggestTitle: { color: theme.soft, fontSize: 10, fontWeight: "900", letterSpacing: 1.2 },
  suggestRow: { flexDirection: "row", flexWrap: "wrap", gap: 8, marginTop: 12 },
  suggestion: { backgroundColor: "#201D2B", paddingHorizontal: 12, paddingVertical: 10, borderColor: "#3A3347", borderWidth: 1, borderRadius: 11 },
  suggestText: { color: "#D7C3DF", fontSize: 11, fontWeight: "800" },
  filtersLabel: { color: theme.soft, fontSize: 10, fontWeight: "900", letterSpacing: 1.3, marginTop: 29 },
  filters: { gap: 9, paddingTop: 14, paddingBottom: 27 },
  chip: { borderWidth: 1, borderColor: theme.border, backgroundColor: theme.card, borderRadius: 12, paddingVertical: 11, paddingHorizontal: 15 },
  activeChip: { backgroundColor: theme.red, borderColor: theme.red },
  chipLabel: { color: theme.muted, fontWeight: "800", fontSize: 12 },
  activeLabel: { color: theme.white },
  resultHead: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", marginBottom: 15 },
  resultTitle: { color: theme.white, fontSize: 21, fontWeight: "900" },
  resultCount: { color: theme.soft, fontWeight: "900", fontSize: 10, letterSpacing: 1 },
  empty: { backgroundColor: theme.card, borderRadius: 22, padding: 30, alignItems: "center" },
  emptyGlyph: { color: theme.red, fontSize: 57 },
  emptyTitle: { color: theme.white, fontSize: 18, fontWeight: "900", marginTop: 10 },
  emptyCopy: { color: theme.muted, fontSize: 12, marginTop: 8, textAlign: "center" },
  reset: { backgroundColor: "#342235", padding: 13, borderRadius: 12, marginTop: 18 },
  resetText: { color: theme.red, fontSize: 10, fontWeight: "900", letterSpacing: 1 },
  note: { color: theme.soft, fontSize: 11, textAlign: "center", marginTop: 22, lineHeight: 17 }
});
