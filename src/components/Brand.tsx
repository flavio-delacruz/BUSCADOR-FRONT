import React from "react";
import { StyleSheet, Text, View } from "react-native";
import { theme } from "../theme";

export function Brand({ size = 46 }: { size?: number }) {
  return (
    <View style={[styles.logo, { width: size, height: size, borderRadius: size * 0.28 }]}>
      <View style={[styles.glow, { width: size * 0.8, height: size * 0.8, borderRadius: size * 0.4 }]} />
      <Text style={{ color: theme.red, fontSize: size * 0.76, fontWeight: "900", lineHeight: size * 0.91, textShadowColor: "#B71C3A", textShadowRadius: 8 }}>B</Text>
      <View style={[styles.lens, { width: size * 0.27, height: size * 0.27, borderRadius: size * 0.14, borderWidth: Math.max(2, size * 0.048), left: size * 0.28, top: size * 0.37 }]} />
      <View style={[styles.handle, { width: size * 0.25, height: Math.max(2, size * 0.052), left: size * 0.5, top: size * 0.65 }]} />
    </View>
  );
}
const styles = StyleSheet.create({
  logo: { alignItems: "center", justifyContent: "center", backgroundColor: "#111017", borderWidth: 1, borderColor: "#612236", overflow: "hidden" },
  glow: { position: "absolute", backgroundColor: "#26121C" },
  lens: { position: "absolute", borderColor: "#FFFFFF" },
  handle: { position: "absolute", borderRadius: 5, backgroundColor: "#FFFFFF", transform: [{ rotate: "44deg" }] }
});
