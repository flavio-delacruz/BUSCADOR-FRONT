import React from "react";
import { Tabs } from "expo-router";
import { Text, type ColorValue } from "react-native";
import { theme } from "../../src/theme";

function TabIcon({ symbol, color, focused }: { symbol: string; color: ColorValue | null | undefined; focused: boolean }) {
  return <Text style={{ color: focused ? theme.red : (color ?? theme.soft), fontWeight: focused ? "900" : "500", fontSize: 24 }}>{symbol}</Text>;
}

export default function TabsLayout() {
  return (
    <Tabs screenOptions={{
      headerShown: false,
      tabBarActiveTintColor: theme.red,
      tabBarInactiveTintColor: theme.soft,
      tabBarHideOnKeyboard: true,
      tabBarStyle: { backgroundColor: "#11151F", borderTopColor: theme.border, borderTopWidth: 1, height: 69, paddingTop: 6, paddingBottom: 6 },
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
