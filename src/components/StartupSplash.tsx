import React, { useEffect, useRef, useState } from "react";
import { AccessibilityInfo, Animated, Easing, StyleSheet, Text, View } from "react-native";
import { Brand } from "./Brand";
import { theme } from "../theme";

/** JS transition is visible even in development clients after Metro loads. */
export function StartupSplash() {
  const [visible, setVisible] = useState(true);
  const opacity = useRef(new Animated.Value(1)).current;
  const logoScale = useRef(new Animated.Value(0.88)).current;

  useEffect(() => {
    let active = true;
    let animation: Animated.CompositeAnimation | undefined;
    AccessibilityInfo.isReduceMotionEnabled()
      .then(reduceMotion => {
        if (!active) return;
        if (reduceMotion) {
          setVisible(false);
          return;
        }
        animation = Animated.sequence([
          Animated.timing(logoScale, {
            toValue: 1,
            duration: 360,
            easing: Easing.out(Easing.cubic),
            useNativeDriver: true
          }),
          Animated.delay(470),
          Animated.timing(opacity, {
            toValue: 0,
            duration: 340,
            easing: Easing.out(Easing.quad),
            useNativeDriver: true
          })
        ]);
        animation.start(({ finished }) => {
          if (active && finished) setVisible(false);
        });
      })
      .catch(() => {
        if (active) setVisible(false);
      });

    return () => {
      active = false;
      animation?.stop();
    };
  }, [logoScale, opacity]);

  if (!visible) return null;

  return (
    <Animated.View
      pointerEvents="none"
      accessibilityElementsHidden
      importantForAccessibility="no-hide-descendants"
      style={[StyleSheet.absoluteFill, styles.overlay, { opacity }]}
    >
      <Animated.View style={{ transform: [{ scale: logoScale }] }}>
        <Brand size={110} />
      </Animated.View>
      <Text style={styles.brand}>BUSCADOR<Text style={{ color: theme.red }}>.</Text></Text>
      <View style={styles.divider} />
      <Text style={styles.caption}>DESCUBRE · PROTEGE · RECUERDA</Text>
    </Animated.View>
  );
}

const styles = StyleSheet.create({
  overlay: { backgroundColor: "#090B11", zIndex: 999, alignItems: "center", justifyContent: "center" },
  brand: { color: theme.white, fontWeight: "900", letterSpacing: 5, fontSize: 25, marginTop: 27 },
  divider: { backgroundColor: theme.red, height: 2, width: 40, borderRadius: 3, marginTop: 17 },
  caption: { color: theme.soft, fontSize: 10, fontWeight: "900", letterSpacing: 1.7, marginTop: 19 }
});
