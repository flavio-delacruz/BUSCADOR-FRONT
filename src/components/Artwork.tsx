import React from "react";
import { StyleSheet, Text, View } from "react-native";
import { theme } from "../theme";

type ArtworkProps = { accent: string; icon: string; height?: number; number?: string; compact?: boolean };

export function Artwork({ accent, icon, height = 160, number = "01", compact = false }: ArtworkProps) {
  return (
    <View style={[styles.wrapper, { height, backgroundColor: "#171D2A" }]}>
      <View style={[styles.wash, { backgroundColor: accent, opacity: 0.17 }]} />
      <View style={[styles.circle, { borderColor: accent, width: compact ? 85 : 176, height: compact ? 85 : 176, borderRadius: compact ? 43 : 88, top: compact ? -31 : -56, right: compact ? -25 : -37 }]} />
      <View style={[styles.circle, { borderColor: accent, width: compact ? 45 : 122, height: compact ? 45 : 122, borderRadius: compact ? 23 : 62, top: compact ? -10 : -30, right: compact ? -8 : -12, opacity: 0.55 }]} />
      <Text style={[styles.glyph, { color: accent, fontSize: compact ? 50 : 106, left: compact ? 15 : 25, top: compact ? 22 : 31 }]}>{icon}</Text>
      {!compact ? <Text style={styles.number}>{number} / B</Text> : null}
      <View style={[styles.dot, { backgroundColor: accent }]} />
    </View>
  );
}
const styles = StyleSheet.create({
  wrapper: { overflow: "hidden", borderRadius: 18 },
  wash: { position: "absolute", top: 0, left: 0, right: 0, bottom: 0 },
  circle: { position: "absolute", borderWidth: 1.2, opacity: 0.85 },
  glyph: { position: "absolute", fontWeight: "300", lineHeight: 125 },
  number: { position: "absolute", color: theme.white, bottom: 16, left: 17, fontSize: 10, fontWeight: "800", letterSpacing: 3 },
  dot: { position: "absolute", width: 7, height: 7, borderRadius: 4, right: 15, bottom: 15 }
});
