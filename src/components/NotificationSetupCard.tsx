import React, { useEffect, useState } from "react";
import { Alert, AppState, Pressable, StyleSheet, Text, View } from "react-native";
import { useRouter } from "expo-router";
import { requireOptionalNativeModule } from "expo";
import { theme } from "../theme";

type Status = { accessGranted: boolean; recordingEnabled: boolean; sources: string[] };
type Bridge = { getStatus(): Promise<Status> };
const notifications = requireOptionalNativeModule<Bridge>("BuscadorNotifications");

let introShownThisSession = false;

export function NotificationSetupCard() {
  const router = useRouter();
  const [status, setStatus] = useState<Status | null>(null);

  useEffect(() => {
    if (!notifications || introShownThisSession) return;
    const timer = setTimeout(() => {
      notifications.getStatus()
        .then(info => {
          if (info.accessGranted || introShownThisSession) return;
          introShownThisSession = true;
          Alert.alert(
            "Respaldo de notificaciones",
            "Buscador puede guardar en este teléfono los textos que Android muestre en notificaciones nuevas de las redes que elijas. El acceso es opcional, y podrás detenerlo y borrar el historial.",
            [
              { text: "Ahora no", style: "cancel" },
              { text: "Ver permisos", onPress: () => router.push("/notification-center") }
            ]
          );
        })
        .catch(() => undefined);
    }, 1600);
    return () => clearTimeout(timer);
  }, [router]);

  useEffect(() => {
    let active = true;
    function refresh() {
      if (!notifications) return;
      notifications.getStatus().then(value => {
        if (active) setStatus(value);
      }).catch(() => {
        if (active) setStatus(null);
      });
    }
    refresh();
    const subscription = AppState.addEventListener("change", state => {
      if (state === "active") refresh();
    });
    return () => { active = false; subscription.remove(); };
  }, []);

  const enabled = status?.accessGranted && status.recordingEnabled;
  const label = enabled ? "HISTORIAL ENCENDIDO" : "HISTORIAL PERSONAL · CONFIGURACIÓN";
  return (
    <Pressable
      onPress={() => router.push("/notification-center")}
      accessibilityRole="button"
      accessibilityLabel="Abrir centro de notificaciones y configurar permisos Android"
      style={styles.card}
    >
      <View style={styles.header}>
        <View style={styles.statusDot} />
        <Text style={styles.eyebrow}>{label}</Text>
        <Text style={styles.chevron}>↗</Text>
      </View>
      <Text style={styles.title}>
        {enabled ? "Tus notificaciones, a salvo." : "Protege las notificaciones que recibes."}
      </Text>
      <Text style={styles.description}>
        {enabled
          ? "Consulta el historial local o cambia las redes que deseas guardar."
          : "Activa voluntariamente el historial de WhatsApp, Facebook, Messenger y TikTok. Solo se guarda lo que Android muestre desde ese momento."}
      </Text>
      <View style={styles.footer}>
        <Text style={styles.cta}>ABRIR PERMISOS Y CONTROLES</Text>
        <Text style={styles.arrow}>→</Text>
      </View>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: "#231724", borderColor: "#75384B", borderWidth: 1,
    borderRadius: 22, padding: 18, marginTop: 8, marginBottom: 23
  },
  header: { flexDirection: "row", alignItems: "center", gap: 8 },
  statusDot: { width: 8, height: 8, backgroundColor: theme.red, borderRadius: 4 },
  eyebrow: { flex: 1, color: "#F7A3B6", fontSize: 10, letterSpacing: 1.3, fontWeight: "900" },
  chevron: { color: theme.red, fontSize: 21 },
  title: { color: theme.white, fontWeight: "900", fontSize: 20, marginTop: 13, lineHeight: 26 },
  description: { color: theme.muted, fontSize: 12, marginTop: 10, lineHeight: 20 },
  footer: { borderTopColor: "#533040", borderTopWidth: 1, marginTop: 17, paddingTop: 13, flexDirection: "row", alignItems: "center", justifyContent: "space-between" },
  cta: { color: "#FF8FA4", fontWeight: "900", letterSpacing: 0.8, fontSize: 10 },
  arrow: { color: theme.red, fontWeight: "900", fontSize: 19 }
});
