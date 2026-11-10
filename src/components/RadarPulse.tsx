import React, { useEffect, useRef } from "react";
import { AccessibilityInfo, Animated, Easing } from "react-native";
import { theme } from "../theme";

/** Pulso ambiental del Radar, sin trabajo continuo cuando la vista se desmonta. */
export function RadarPulse() {
  const progress = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    let active = true;
    let animation: Animated.CompositeAnimation | undefined;
    AccessibilityInfo.isReduceMotionEnabled()
      .then(reduced => {
        if (!active || reduced) return;
        animation = Animated.loop(Animated.timing(progress, {
          toValue: 1,
          duration: 2600,
          easing: Easing.inOut(Easing.ease),
          useNativeDriver: true
        }));
        animation.start();
      })
      .catch(() => undefined);

    return () => {
      active = false;
      animation?.stop();
    };
  }, [progress]);

  return (
    <Animated.View
      pointerEvents="none"
      style={{
        position: "absolute",
        top: 12,
        alignSelf: "center",
        height: 202,
        width: 202,
        borderWidth: 1.5,
        borderColor: theme.cyan,
        borderRadius: 101,
        opacity: progress.interpolate({ inputRange: [0, 0.5, 1], outputRange: [0.1, 0.42, 0.04] }),
        transform: [{ scale: progress.interpolate({ inputRange: [0, 1], outputRange: [0.86, 1.27] }) }]
      }}
    />
  );
}
