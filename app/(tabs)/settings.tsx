import React from "react";
import { ScrollView, StyleSheet, Text, View } from "react-native";
import { Screen } from "../../src/components/Screen";
import { theme } from "../../src/theme";

function Setting({ title, info }: { title: string; info: string }) {
  return <View style={styles.item}><Text style={styles.itemTitle}>{title}</Text><Text style={styles.itemInfo}>{info}</Text></View>;
}

export default function Settings() {
  return (
    <Screen title="Ajustes" subtitle="Personaliza tu experiencia">
      <ScrollView contentContainerStyle={styles.content}>
        <Text style={styles.heading}>PREFERENCIAS</Text>
        <View style={styles.card}>
          <Setting title="Tema visual" info="Oscuro · Rojo Buscador" />
          <View style={styles.divider} />
          <Setting title="Contenido" info="Catálogo de demostración" />
        </View>
        <Text style={styles.heading}>PRIVACIDAD Y TRANSPARENCIA</Text>
        <View style={styles.card}>
          <Setting title="Permisos especiales" info="Ninguno solicitado en esta versión." />
          <View style={styles.divider} />
          <Setting title="Datos personales" info="No se leen notificaciones, mensajes ni pulsaciones del teclado." />
          <View style={styles.divider} />
          <Setting title="Conexión" info="La búsqueda funciona localmente, sin enviarse al servidor." />
        </View>
        <Text style={styles.footer}>Buscador · Versión 0.1.0 · Proyecto académico</Text>
      </ScrollView>
    </Screen>
  );
}
const styles = StyleSheet.create({
  content: { paddingHorizontal: 22, paddingBottom: 40 },
  heading: { color: theme.red, fontSize: 11, fontWeight: "800", letterSpacing: 2, marginTop: 13, marginBottom: 14 },
  card: { backgroundColor: theme.card, borderColor: theme.border, borderWidth: 1, borderRadius: 20, paddingHorizontal: 18, marginBottom: 24 },
  item: { paddingVertical: 19 },
  itemTitle: { color: theme.white, fontWeight: "800", fontSize: 15, marginBottom: 6 },
  itemInfo: { color: theme.muted, fontSize: 12, lineHeight: 18 },
  divider: { backgroundColor: theme.border, height: 1 },
  footer: { color: theme.soft, fontSize: 11, textAlign: "center", marginTop: 8 }
});
