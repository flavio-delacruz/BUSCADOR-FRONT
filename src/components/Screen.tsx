import React from "react";
import { SafeAreaView } from "react-native-safe-area-context";
import { StyleSheet, Text, View } from "react-native";
import { theme } from "../theme";
import { Brand } from "./Brand";

export function Screen({ title, subtitle, children }: { title: string; subtitle?: string; children: React.ReactNode }) {
  return (
    <SafeAreaView style={styles.screen} edges={["top"]}>
      <View style={styles.header}>
        <Brand size={43} />
        <View style={{ flex: 1 }}>
          <Text style={styles.title}>{title}</Text>
          {subtitle ? <Text style={styles.subtitle}>{subtitle}</Text> : null}
        </View>
      </View>
      {children}
    </SafeAreaView>
  );
}
const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: theme.background },
  header: { flexDirection: "row", alignItems: "center", gap: 13, paddingHorizontal: 22, paddingTop: 18, paddingBottom: 20 },
  title: { color: theme.white, fontSize: 24, fontWeight: "800", letterSpacing: -0.6 },
  subtitle: { color: theme.muted, fontSize: 12, marginTop: 3 }
});
