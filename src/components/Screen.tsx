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
        <View style={styles.wordmark}>
          <Text style={styles.title}>{title}</Text>
          {subtitle ? <Text style={styles.subtitle}>{subtitle}</Text> : null}
        </View>
        <View style={styles.badge}><Text style={styles.badgeText}>DEMO</Text></View>
      </View>
      {children}
    </SafeAreaView>
  );
}
const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: theme.background },
  header: { flexDirection: "row", alignItems: "center", gap: 11, paddingHorizontal: 22, paddingTop: 12, paddingBottom: 20 },
  wordmark: { flex: 1 },
  title: { color: theme.white, fontSize: 21, fontWeight: "900", letterSpacing: -0.6 },
  subtitle: { color: theme.soft, fontSize: 10, marginTop: 2, letterSpacing: 0.4 },
  badge: { backgroundColor: "#25151E", borderColor: "#603246", borderWidth: 1, paddingHorizontal: 9, paddingVertical: 6, borderRadius: 9 },
  badgeText: { color: "#FF91A5", fontWeight: "800", letterSpacing: 1.5, fontSize: 9 }
});
