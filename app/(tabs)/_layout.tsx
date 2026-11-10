import React, { useEffect, useRef } from "react";
import { Tabs } from "expo-router";
import { Animated, Platform, Text, View, type ColorValue } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { theme } from "../../src/theme";

function TabIcon({
  symbol,
  color,
  focused
}: {
  symbol: string;
  color: ColorValue | null | undefined;
  focused: boolean;
}) {
  const progress = useRef(new Animated.Value(focused ? 1 : 0)).current;

  useEffect(() => {
    const animation = Animated.spring(progress, {
      toValue: focused ? 1 : 0,
      useNativeDriver: true,
      tension: 90,
      friction: 10
    });
    animation.start();
    return () => animation.stop();
  }, [focused, progress]);

  return (
    <View style={{ alignItems: "center", justifyContent: "center", minWidth: 42, height: 34 }}>
      <Animated.View
        style={{
          alignItems: "center",
          justifyContent: "center",
          transform: [
            { scale: progress.interpolate({ inputRange: [0, 1], outputRange: [0.93, 1.14] }) },
            { translateY: progress.interpolate({ inputRange: [0, 1], outputRange: [1, -2] }) }
          ]
        }}
      >
        <Text style={{
          color: focused ? theme.red : (color ?? theme.soft),
          fontWeight: focused ? "900" : "500",
          fontSize: 24
        }}>{symbol}</Text>
      </Animated.View>
      <Animated.View
        style={{
          position: "absolute",
          bottom: -1,
          width: 5,
          height: 5,
          borderRadius: 3,
          backgroundColor: theme.red,
          opacity: progress
        }}
      />
    </View>
  );
}

export default function TabsLayout() {
  const insets = useSafeAreaInsets();
  // Expo SDK 58 usa edge-to-edge en Android. El navbar no debe invadir
  // los botones del sistema (atrás / inicio / aplicaciones recientes).
  const bottomInset = Math.max(insets.bottom, Platform.OS === "android" ? 22 : 10);

  return (
    <Tabs screenOptions={{
      headerShown: false,
      tabBarActiveTintColor: theme.red,
      tabBarInactiveTintColor: theme.soft,
      tabBarHideOnKeyboard: true,
      tabBarStyle: {
        backgroundColor: "#11151F",
        borderTopColor: theme.border,
        borderTopWidth: 1,
        height: 59 + bottomInset,
        paddingTop: 6,
        paddingBottom: bottomInset
      },
      tabBarItemStyle: { paddingTop: 2 },
      tabBarLabelStyle: { fontSize: 10, fontWeight: "800", letterSpacing: 0.2 }
    }}>
      <Tabs.Screen name="index" options={{ title: "Hoy", tabBarIcon: ({ color, focused }) => <TabIcon symbol="⌂" color={color} focused={focused} /> }} />
      <Tabs.Screen name="radar" options={{ title: "Radar", tabBarIcon: ({ color, focused }) => <TabIcon symbol="◉" color={color} focused={focused} /> }} />
      <Tabs.Screen name="search" options={{ title: "Explorar", tabBarIcon: ({ color, focused }) => <TabIcon symbol="⌕" color={color} focused={focused} /> }} />
      <Tabs.Screen name="saved" options={{ title: "Guardados", tabBarIcon: ({ color, focused }) => <TabIcon symbol="♡" color={color} focused={focused} /> }} />
      <Tabs.Screen name="settings" options={{ title: "Ajustes", tabBarIcon: ({ color, focused }) => <TabIcon symbol="☷" color={color} focused={focused} /> }} />
    </Tabs>
  );
}
