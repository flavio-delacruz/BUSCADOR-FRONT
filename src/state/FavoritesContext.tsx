import React, { createContext, useContext, useMemo, useState } from "react";

type FavoritesContextValue = { favorites: string[]; toggle: (id: string) => void; isFavorite: (id: string) => boolean };
const FavoritesContext = createContext<FavoritesContextValue | null>(null);

export function FavoritesProvider({ children }: { children: React.ReactNode }) {
  const [favorites, setFavorites] = useState<string[]>([]);
  const toggle = (id: string) => setFavorites(current =>
    current.includes(id) ? current.filter(item => item !== id) : [...current, id]
  );
  const value = useMemo(() => ({
    favorites,
    toggle,
    isFavorite: (id: string) => favorites.includes(id)
  }), [favorites]);
  return <FavoritesContext.Provider value={value}>{children}</FavoritesContext.Provider>;
}

export function useFavorites() {
  const context = useContext(FavoritesContext);
  if (!context) throw new Error("useFavorites necesita FavoritesProvider");
  return context;
}
