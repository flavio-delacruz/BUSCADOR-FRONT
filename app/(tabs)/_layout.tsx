import React from "react";
import { Tabs } from "expo-router";
import { Text, type ColorValue } from "react-native";
import { theme } from "../../src/theme";

function TabIcon({ symbol, color }: { symbol: string; color: ColorValue | null | undefined }) {
  return <Text style={{ color: color ?? theme.muted, fontWeight: "800", fontSize: 23 }}>{symbol}</Text>;
}

export default function TabsLayout() {
  return (
    <Tabs screenOptions={{
      headerShown: false,
      tabBarActiveTintColor: theme.red,
      tabBarInactiveTintColor: theme.muted,
      tabBarStyle: { backgroundColor: "#111217", borderTopColor: theme.border, height: 66, paddingTop: 6 },
      tabBarLabelStyle: { fontSize: 10, fontWeight: "700" }
    }}>
      <Tabs.Screen name="index" options={{ title: "Inicio", tabBarIcon: ({ color }) => <TabIcon symbol="⌂" color={color} /> }} />
      <Tabs.Screen name="search" options={{ title: "Buscar", tabBarIcon: ({ color }) => <TabIcon symbol="⌕" color={color} /> }} />
      <Tabs.Screen name="saved" options={{ title: "Guardados", tabBarIcon: ({ color }) => <TabIcon symbol="♡" color={color} /> }} />
      <Tabs.Screen name="settings" options={{ title: "Ajustes", tabBarIcon: ({ color }) => <TabIcon symbol="⚙" color={color} /> }} />
    </Tabs>
  );
}
