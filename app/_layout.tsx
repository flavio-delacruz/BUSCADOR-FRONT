import "react-native-gesture-handler";
import React from "react";
import { Stack } from "expo-router";
import { StatusBar, View } from "react-native";
import { FavoritesProvider } from "../src/state/FavoritesContext";
import { StartupSplash } from "../src/components/StartupSplash";
import { theme } from "../src/theme";

export default function RootLayout() {
  return (
    <FavoritesProvider>
      <StatusBar barStyle="light-content" />
      <View style={{ flex: 1, backgroundColor: theme.background }}>
        <Stack screenOptions={{ headerShown: false, contentStyle: { backgroundColor: theme.background } }} />
        <StartupSplash />
      </View>
    </FavoritesProvider>
  );
}
