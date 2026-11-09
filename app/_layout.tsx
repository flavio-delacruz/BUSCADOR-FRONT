import "react-native-gesture-handler";
import React from "react";
import { Stack } from "expo-router";
import { StatusBar } from "react-native";
import { FavoritesProvider } from "../src/state/FavoritesContext";
import { theme } from "../src/theme";

export default function RootLayout() {
  return (
    <FavoritesProvider>
      <StatusBar barStyle="light-content" />
      <Stack screenOptions={{ headerShown: false, contentStyle: { backgroundColor: theme.background } }} />
    </FavoritesProvider>
  );
}
