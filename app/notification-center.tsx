import React, { useCallback, useEffect, useState } from "react";
import {
  Alert,
  AppState,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Switch,
  Text,
  View
} from "react-native";
import { useRouter } from "expo-router";
import { SafeAreaView } from "react-native-safe-area-context";
import { requireOptionalNativeModule } from "expo-modules-core";
import { theme } from "../src/theme";

type ArchiveStatus = {
  accessGranted: boolean;
  recordingEnabled: boolean;
  sources: string[];
  recordsCount: number;
};

type ArchiveRecord = {
  id: string;
  source: string;
  title: string;
  message: string;
  time: number;
};

type ArchiveModule = {
  getStatus(): Promise<ArchiveStatus>;
  openAccessSettings(): Promise<boolean>;
  setRecordingEnabled(enabled: boolean): Promise<boolean>;
  setSources(sources: string[]): Promise<boolean>;
  getRecords(): Promise<ArchiveRecord[]>;
  clearRecords(): Promise<boolean>;
};

const native: ArchiveModule | null = Platform.OS === "android"
  ? requireOptionalNativeModule<ArchiveModule>("BuscadorNotifications")
  : null;

const sourceOptions = [
  { id: "whatsapp", name: "WhatsApp", packages: ["com.whatsapp", "com.whatsapp.w4b"], accent: "#57DCA1" },
  { id: "facebook", name: "Facebook", packages: ["com.facebook.katana", "com.facebook.lite"], accent: "#82A9FF" },
  { id: "messenger", name: "Messenger", packages: ["com.facebook.orca"], accent: "#B39CFF" },
  { id: "tiktok", name: "TikTok", packages: ["com.zhiliaoapp.musically"], accent: "#FF8DAB" }
] as const;

const initialStatus: ArchiveStatus = {
  accessGranted: false,
  recordingEnabled: false,
  sources: [],
  recordsCount: 0
};

function sourceLabel(pkg: string) {
  if (pkg.startsWith("com.whatsapp")) return "WhatsApp";
  if (pkg === "com.facebook.orca") return "Messenger";
  if (pkg === "com.facebook.katana" || pkg === "com.facebook.lite") return "Facebook";
  if (pkg === "com.zhiliaoapp.musically") return "TikTok";
  return "Notificación";
}

export default function NotificationCenter() {
  const router = useRouter();
  const [status, setStatus] = useState<ArchiveStatus>(initialStatus);
  const [records, setRecords] = useState<ArchiveRecord[]>([]);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const supported = native !== null;

  const refresh = useCallback(async () => {
    if (!native) return;
    try {
      const [nextStatus, nextRecords] = await Promise.all([
        native.getStatus(),
        native.getRecords()
      ]);
      setStatus(nextStatus);
      setRecords(nextRecords);
      setError("");
    } catch {
      setError("No se pudo leer el historial. Comprueba el almacenamiento del dispositivo.");
    }
  }, []);

  useEffect(() => {
    void refresh();
    const subscription = AppState.addEventListener("change", value => {
      if (value === "active") void refresh();
    });
    return () => subscription.remove();
  }, [refresh]);

  const granted = status.accessGranted;
  const recording = granted && status.recordingEnabled;

  async function updateRecording(next: boolean) {
    if (!native || busy) return;
    setBusy(true);
    try {
      await native.setRecordingEnabled(next);
      await refresh();
    } catch (reason) {
      Alert.alert("No se pudo cambiar el estado", reason instanceof Error ? reason.message : "Revisa los permisos de Android.");
    } finally {
      setBusy(false);
    }
  }

  async function updateSource(packages: readonly string[], next: boolean) {
    if (!native || busy) return;
    setBusy(true);
    try {
      const current = status.sources.filter(pkg => !packages.includes(pkg));
      const selected = next ? [...current, ...packages] : current;
      await native.setSources(selected);
      await refresh();
    } catch {
      Alert.alert("Error", "No se pudo guardar la selección de aplicaciones.");
    } finally {
      setBusy(false);
    }
  }

  async function openSettings() {
    if (!native) return;
    try {
      await native.openAccessSettings();
    } catch {
      Alert.alert("No disponible", "Abre Ajustes > Acceso especial > Acceso a notificaciones y busca Buscador.");
    }
  }

  function confirmDelete() {
    if (!native || records.length === 0) return;
    Alert.alert(
      "Eliminar historial",
      "Esta acción borrará definitivamente todas las notificaciones guardadas en Buscador. No modifica WhatsApp, Facebook ni TikTok.",
      [
        { text: "Cancelar", style: "cancel" },
        {
          text: "Eliminar todo",
          style: "destructive",
          onPress: () => {
            setBusy(true);
            native.clearRecords()
              .then(() => refresh())
              .catch(() => Alert.alert("Error", "No se pudo borrar el historial local."))
              .finally(() => setBusy(false));
          }
        }
      ]
    );
  }

  return (
    <SafeAreaView style={styles.screen} edges={["top", "bottom"]}>
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <Pressable onPress={() => router.back()} style={styles.back} accessibilityRole="button" accessibilityLabel="Regresar">
          <Text style={styles.backText}>‹  REGRESAR</Text>
        </Pressable>

        <Text style={styles.kicker}>BUSCADOR / TU PRIVACIDAD</Text>
        <Text style={styles.heading}>Historial de{"\n"}<Text style={{ color: theme.red }}>notificaciones.</Text></Text>
        <Text style={styles.intro}>
          Guarda en este teléfono los textos de las notificaciones nuevas que Android muestre
          de las redes sociales elegidas. No permite leer chats completos ni recuperar mensajes
          recibidos antes de activar la función.
        </Text>

        {!supported ? (
          <View style={styles.notice}>
            <Text style={styles.noticeTitle}>Se necesita una nueva APK</Text>
            <Text style={styles.noticeBody}>
              Tu aplicación actual aún no incluye el módulo nativo de Android.
              Cuando instales la próxima compilación de Buscador aparecerán aquí
              los permisos y los controles.
            </Text>
          </View>
        ) : (
          <>
            <View style={styles.status}>
              <View style={[styles.indicator, recording && styles.indicatorOn]} />
              <View style={{ flex: 1 }}>
                <Text style={styles.statusTitle}>
                  {recording ? "Historial activado" : granted ? "Historial detenido" : "Permiso de Android pendiente"}
                </Text>
                <Text style={styles.statusBody}>
                  {recording
                    ? "Se conservarán nuevas notificaciones de las fuentes seleccionadas."
                    : "No se guardan nuevas notificaciones en este momento."}
                </Text>
              </View>
              <Text style={[styles.statePill, recording && styles.statePillOn]}>
                {recording ? "ACTIVO" : "INACTIVO"}
              </Text>
            </View>

            <View style={styles.controlCard}>
              <Text style={styles.controlTitle}>1. Autorizar en Android</Text>
              <Text style={styles.controlBody}>
                El acceso a notificaciones es un permiso especial. Android puede conceder acceso
                amplio a las notificaciones del dispositivo; Buscador filtra y almacena únicamente
                las fuentes seleccionadas aquí.
              </Text>
              <Pressable onPress={openSettings} style={styles.permissionButton} accessibilityRole="button">
                <Text style={styles.permissionLabel}>
                  {granted ? "REVISAR PERMISO DEL SISTEMA" : "ABRIR AJUSTES DE PERMISO"}  ↗
                </Text>
              </Pressable>
              <Text style={styles.permissionState}>{granted ? "✓ Permiso concedido por Android" : "• Aún no autorizado"}</Text>
            </View>

            <View style={styles.sectionRow}>
              <Text style={styles.section}>2. Elegir redes sociales</Text>
              <Text style={styles.smallLabel}>CONTROL INDIVIDUAL</Text>
            </View>

            {sourceOptions.map(source => {
              const selected = source.packages.some(pkg => status.sources.includes(pkg));
              return (
                <View style={styles.source} key={source.id}>
                  <View style={[styles.sourceDot, { backgroundColor: source.accent }]} />
                  <View style={{ flex: 1 }}>
                    <Text style={styles.sourceTitle}>{source.name}</Text>
                    <Text style={styles.sourceDetail}>{selected ? "Incluida en el historial" : "No registrar esta red"}</Text>
                  </View>
                  <Switch
                    value={selected}
                    disabled={busy}
                    onValueChange={value => void updateSource(source.packages, value)}
                    accessibilityLabel={"Guardar notificaciones de " + source.name}
                    trackColor={{ false: "#404453", true: "#953447" }}
                    thumbColor={selected ? theme.red : "#B8BEC8"}
                  />
                </View>
              );
            })}

            <View style={styles.recordCard}>
              <View style={{ flex: 1 }}>
                <Text style={styles.recordTitle}>3. Activar guardado</Text>
                <Text style={styles.recordSub}>Puedes detenerlo en cualquier momento.</Text>
              </View>
              <Switch
                value={recording}
                disabled={busy || !granted || status.sources.length === 0}
                onValueChange={value => void updateRecording(value)}
                accessibilityLabel="Activar o detener el historial de notificaciones"
                trackColor={{ false: "#404453", true: "#953447" }}
                thumbColor={recording ? theme.red : "#B8BEC8"}
              />
            </View>

            <View style={styles.sectionRow}>
              <Text style={styles.section}>Historial local</Text>
              <Pressable onPress={() => void refresh()} accessibilityRole="button" accessibilityLabel="Actualizar historial">
                <Text style={styles.refresh}>ACTUALIZAR ↻</Text>
              </Pressable>
            </View>

            <View style={styles.historyInfo}>
              <Text style={styles.count}>{records.length}</Text>
              <Text style={styles.countDetail}>NOTIFICACIONES GUARDADAS{"\n"}Máximo 500 · Conservación hasta 30 días</Text>
            </View>

            {records.length === 0 ? (
              <View style={styles.empty}><Text style={styles.emptyTitle}>Todavía no hay notificaciones guardadas</Text><Text style={styles.emptyText}>Activa el permiso y el guardado. Después espera una notificación nueva de las redes seleccionadas.</Text></View>
            ) : (
              records.map(item => (
                <View key={item.id} style={styles.record}>
                  <View style={styles.recordMeta}>
                    <Text style={styles.recordSource}>{sourceLabel(item.source).toUpperCase()}</Text>
                    <Text style={styles.recordTime}>{new Date(item.time).toLocaleString("es-PE")}</Text>
                  </View>
                  {item.title ? <Text style={styles.messageTitle}>{item.title}</Text> : null}
                  <Text style={styles.messageText}>{item.message}</Text>
                </View>
              ))
            )}

            <Pressable onPress={confirmDelete} disabled={busy || records.length === 0} accessibilityRole="button" style={[styles.deleteButton, records.length === 0 && styles.disabled]}>
              <Text style={styles.deleteText}>BORRAR TODO EL HISTORIAL LOCAL</Text>
            </Pressable>
          </>
        )}

        {error ? <Text style={styles.error}>{error}</Text> : null}
        <View style={styles.privacy}>
          <Text style={styles.privacyTitle}>IMPORTANTE</Text>
          <Text style={styles.privacyBody}>
            Las copias se cifran y guardan solamente en tu dispositivo. No se envían a MongoDB
            ni a servidores externos. El sistema podría ocultar o resumir el contenido,
            especialmente si la aplicación de origen protege sus notificaciones.
            El historial no restaura ni modifica conversaciones dentro de otras aplicaciones.
          </Text>
        </View>
        <Text style={styles.footer}>USO PERSONAL · AUTORIZACIÓN EXPLÍCITA · DATOS LOCALES</Text>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: theme.background },
  content: { paddingHorizontal: 20, paddingTop: 16, paddingBottom: 35 },
  back: { paddingVertical: 9, marginBottom: 23 },
  backText: { color: theme.muted, fontSize: 11, letterSpacing: 1.2, fontWeight: "900" },
  kicker: { color: theme.red, fontSize: 10, fontWeight: "900", letterSpacing: 2 },
  heading: { color: theme.white, fontSize: 32, lineHeight: 38, fontWeight: "900", letterSpacing: -0.9, marginTop: 10 },
  intro: { color: theme.muted, fontSize: 13, lineHeight: 21, marginTop: 15, marginBottom: 18 },
  notice: { padding: 18, borderRadius: 19, borderColor: "#6A4A48", backgroundColor: "#292027", borderWidth: 1 },
  noticeTitle: { color: theme.white, fontSize: 16, fontWeight: "900" },
  noticeBody: { color: theme.muted, fontSize: 12, lineHeight: 20, marginTop: 8 },
  status: { flexDirection: "row", alignItems: "center", gap: 11, padding: 16, borderWidth: 1, borderColor: theme.border, backgroundColor: theme.card, borderRadius: 19 },
  indicator: { backgroundColor: theme.soft, height: 10, width: 10, borderRadius: 5 },
  indicatorOn: { backgroundColor: "#57DCA1" },
  statusTitle: { color: theme.white, fontSize: 14, fontWeight: "900" },
  statusBody: { color: theme.muted, fontSize: 11, lineHeight: 17, marginTop: 6 },
  statePill: { color: theme.soft, fontSize: 9, fontWeight: "900", letterSpacing: 1 },
  statePillOn: { color: "#57DCA1" },
  controlCard: { borderRadius: 20, padding: 17, marginTop: 18, backgroundColor: "#211C28", borderColor: "#47364C", borderWidth: 1 },
  controlTitle: { color: theme.white, fontSize: 17, fontWeight: "900" },
  controlBody: { color: theme.muted, fontSize: 12, lineHeight: 19, marginTop: 9 },
  permissionButton: { backgroundColor: theme.red, borderRadius: 12, paddingVertical: 15, marginTop: 17, alignItems: "center" },
  permissionLabel: { color: theme.white, fontSize: 11, fontWeight: "900", letterSpacing: 0.5 },
  permissionState: { color: theme.muted, fontSize: 11, marginTop: 11 },
  sectionRow: { marginTop: 27, marginBottom: 13, flexDirection: "row", justifyContent: "space-between", alignItems: "center" },
  section: { color: theme.white, fontSize: 17, fontWeight: "900", flex: 1 },
  smallLabel: { color: theme.soft, fontSize: 9, fontWeight: "900", letterSpacing: 0.4 },
  source: { flexDirection: "row", alignItems: "center", backgroundColor: theme.card, borderColor: theme.border, borderWidth: 1, borderRadius: 17, padding: 15, gap: 12, marginBottom: 10 },
  sourceDot: { width: 11, height: 11, borderRadius: 6 },
  sourceTitle: { color: theme.white, fontSize: 14, fontWeight: "900" },
  sourceDetail: { color: theme.soft, fontSize: 11, marginTop: 5 },
  recordCard: { marginTop: 12, backgroundColor: "#2C2130", borderWidth: 1, borderColor: "#6E3A54", padding: 18, borderRadius: 18, flexDirection: "row", gap: 12, alignItems: "center" },
  recordTitle: { color: theme.white, fontWeight: "900", fontSize: 16 },
  recordSub: { color: theme.muted, fontSize: 11, marginTop: 7 },
  refresh: { color: theme.red, fontSize: 10, fontWeight: "900", letterSpacing: 0.7 },
  historyInfo: { flexDirection: "row", alignItems: "center", gap: 18, padding: 16, borderRadius: 16, backgroundColor: theme.card, borderColor: theme.border, borderWidth: 1, marginBottom: 12 },
  count: { color: theme.red, fontSize: 37, fontWeight: "900" },
  countDetail: { color: theme.muted, fontSize: 11, fontWeight: "800", lineHeight: 20 },
  empty: { padding: 25, backgroundColor: theme.card, borderRadius: 16, alignItems: "center" },
  emptyTitle: { color: theme.white, textAlign: "center", fontWeight: "900", fontSize: 14 },
  emptyText: { color: theme.muted, textAlign: "center", fontSize: 12, lineHeight: 19, marginTop: 10 },
  record: { borderWidth: 1, borderColor: theme.border, backgroundColor: theme.card, padding: 16, borderRadius: 16, marginBottom: 10 },
  recordMeta: { flexDirection: "row", justifyContent: "space-between", gap: 10 },
  recordSource: { color: theme.red, fontSize: 10, fontWeight: "900" },
  recordTime: { color: theme.soft, fontSize: 10 },
  messageTitle: { color: theme.white, fontWeight: "900", marginTop: 12, fontSize: 14 },
  messageText: { color: theme.muted, lineHeight: 20, marginTop: 8, fontSize: 12 },
  deleteButton: { padding: 16, borderColor: "#74394D", borderWidth: 1, marginTop: 16, alignItems: "center", borderRadius: 13 },
  deleteText: { color: theme.red, fontWeight: "900", fontSize: 11, letterSpacing: 0.5 },
  disabled: { opacity: 0.35 },
  error: { color: "#FF9C9C", marginTop: 20, fontSize: 12 },
  privacy: { marginTop: 28, padding: 17, backgroundColor: "#1C212B", borderRadius: 17, borderWidth: 1, borderColor: theme.border },
  privacyTitle: { color: theme.cyan, fontSize: 10, fontWeight: "900", letterSpacing: 1.5 },
  privacyBody: { color: theme.muted, fontSize: 12, lineHeight: 19, marginTop: 9 },
  footer: { color: theme.soft, fontSize: 9, fontWeight: "900", textAlign: "center", letterSpacing: 0.8, marginTop: 24 }
});
