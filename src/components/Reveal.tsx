import React, { useEffect, useRef, type ReactNode } from "react";
import {
  AccessibilityInfo,
  Animated,
  Easing,
  type StyleProp,
  type ViewStyle
} from "react-native";

/** Animación de entrada suave. Respeta la preferencia de movimiento reducido. */
export function Reveal({
  children,
  delay = 0,
  distance = 14,
  style
}: {
  children: ReactNode;
  delay?: number;
  distance?: number;
  style?: StyleProp<ViewStyle>;
}) {
  const progress = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    let active = true;
    let motion: Animated.CompositeAnimation | undefined;

    AccessibilityInfo.isReduceMotionEnabled()
      .then(reduced => {
        if (!active) return;
        if (reduced) {
          progress.setValue(1);
          return;
        }
        motion = Animated.timing(progress, {
          toValue: 1,
          duration: 460,
          delay,
          easing: Easing.out(Easing.cubic),
          useNativeDriver: true
        });
        motion.start();
      })
      .catch(() => {
        if (active) progress.setValue(1);
      });

    return () => {
      active = false;
      motion?.stop();
    };
  }, [progress, delay]);

  return (
    <Animated.View style={[
      style,
      {
        opacity: progress,
        transform: [{
          translateY: progress.interpolate({
            inputRange: [0, 1],
            outputRange: [distance, 0]
          })
        }]
      }
    ]}>
      {children}
    </Animated.View>
  );
}
