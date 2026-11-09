import React from "react";
import { StyleSheet, Text, View } from "react-native";
import { theme } from "../theme";

export function Brand({ size = 58 }: { size?: number }) {
  return (
    <View style={[styles.logo, { width: size, height: size, borderRadius: size * 0.25 }]}>
      <Text style={{ color: theme.red, fontSize: size * 0.82, fontWeight: "900", lineHeight: size * 0.96 }}>B</Text>
      <View style={[styles.lens, { width: size * 0.27, height: size * 0.27, borderRadius: size * 0.14, borderWidth: Math.max(2, size * 0.045), left: size * 0.31, top: size * 0.39 }]} />
      <View style={[styles.handle, { width: size * 0.22, height: Math.max(2, size * 0.055), left: size * 0.53, top: size * 0.68 }]} />
    </View>
  );
}
const styles = StyleSheet.create({
  logo: { alignItems: "center", justifyContent: "center", backgroundColor: "#08090C", borderWidth: 1, borderColor: "#6F1220", overflow: "hidden" },
  lens: { position: "absolute", borderColor: "#FFFFFF", backgroundColor: "transparent" },
  handle: { position: "absolute", borderRadius: 4, backgroundColor: "#FFFFFF", transform: [{ rotate: "44deg" }] }
});
