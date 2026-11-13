import React, { useCallback, useEffect, useRef, useState } from "react";
import {
  AppState,
  NativeModules,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  View
} from "react-native";
import { useRouter } from "expo-router";
import { SafeAreaView } from "react-native-safe-area-context";
import { requireOptionalNativeModule } from "expo";
import { theme } from "../src/theme";

type ArchiveStatus = {
  accessGranted: boolean;
  recordingEnabled: boolean;
  setupFinished?: boolean;
  setupPending?: boolean;
  sources: string[];
  serviceRegistered?: boolean;
};

type NativeArchive = {
  getStatus(): Promise<ArchiveStatus>;
  setSources(packages: string[]): Promise<boolean>;
  setRecordingEnabled(enabled: boolean): Promise<boolean>;
  openAccessSettings(): Promise<boolean>;
  setSetupPending(pending: boolean): Promise<boolean>;
  setSetupFinished(finished: boolean): Promise<boolean>;
};

const archive: NativeArchive | null = Platform.OS === "android"
  ? ((NativeModules.BuscadorNotificationsLegacy as NativeArchive | undefined)
    ?? requireOptionalNativeModule<NativeArchive>("BuscadorNotifications"))
  : null;

const sources = [
  { id: "whatsapp", name: "WhatsApp", packages: ["com.whatsapp", "com.whatsapp.w4b"], color: "#43D38E" },
  { id: "facebook", name: "Facebook", packages: ["com.facebook.katana", "com.facebook.lite"], color: "#70A4FF" },
  { id: "messenger", name: "Messenger", packages: ["com.facebook.orca"], color: "#AD8BFF" },
  { id: "tiktok", name: "TikTok", packages: ["com.zhiliaoapp.musically"], color: "#F27E95" }
] as const;

const defaultSelection = ["whatsapp", "facebook", "tiktok"];

export default function NotificationSetup() {
  const router = useRouter();
  const [chosen, setChosen] = useState<string[]>(defaultSelection);
  const [status, setStatus] = useState<ArchiveStatus | null>(null);
  const [pending, setPending] = useState(false);
  const [busy, setBusy] = useState(false);
  const [notice, setNotice] = useState("");
  const completing = useRef(false);

  const completeAfterPermission = useCallback(async () => {
    if (!archive || completing.current) return;
    completing.current = true;
    try {
      const fresh = await archive.getStatus();
      setStatus(fresh);
      // Never start capture unless the user explicitly tapped the consent
      // button and Android subsequently granted its special access.
      if (!fresh.setupPending || !fresh.accessGranted) {
        if (fresh.setupPending && !fresh.accessGranted) {
          setNotice("Android aún no autorizó el acceso. Puedes volver a abrir sus ajustes.");
        }
        return;
      }
      if (fresh.sources.length === 0) {
        setNotice("Debes seleccionar tres aplicaciones antes de continuar.");
        return;
      }
      await archive.setRecordingEnabled(true);
      await archive.setSetupFinished(true);
      setPending(false);
      router.replace("/notification-center");
    } catch {
      setNotice("No se pudo activar la captura. Comprueba el permiso y vuelve a intentarlo.");
    } finally {
      completing.current = false;
    }
  }, [router]);

  useEffect(() => {
    if (!archive) return;
    archive.getStatus().then(s => {
      setStatus(s);
      setPending(Boolean(s.setupPending));
      const stored = sources.filter(source => source.packages.some(pkg => s.sources.includes(pkg))).map(s => s.id);
      if (stored.length > 0) setChosen(stored);
      if (s.setupPending && s.accessGranted) void completeAfterPermission();
    }).catch(() => setNotice("No se puede comunicar con el módulo nativo de Android."));
    const listener = AppState.addEventListener("change", state => {
      if (state === "active") void completeAfterPermission();
    });
    return () => listener.remove();
  }, [completeAfterPermission]);

  function toggle(id: string) {
    if (pending || busy) return;
    setNotice("");
    setChosen(current => current.includes(id)
      ? current.filter(value => value !== id)
      : current.length < 3 ? [...current, id] : current
    );
  }

  async function grantPermission() {
    if (!archive || busy || chosen.length !== 3) return;
    setBusy(true);
    setNotice("");
    try {
      const selected = sources.filter(source => chosen.includes(source.id))
        .flatMap(source => [...source.packages]);
      await archive.setSources(selected);
      await archive.setSetupPending(true);
      setPending(true);
      const fresh = await archive.getStatus();
      setStatus(fresh);
      if (fresh.accessGranted) {
        await completeAfterPermission();
      } else {
        await archive.openAccessSettings();
      }
    } catch {
      setNotice("No pudimos abrir los ajustes de Android. Puedes hacerlo manualmente en Ajustes → Acceso a notificaciones.");
    } finally {
      setBusy(false);
    }
  }

  async function skip() {
    if (!archive || busy) return;
    setBusy(true);
    try {
      await archive.setSetupPending(false);
      await archive.setSetupFinished(true);
      router.replace("/(tabs)");
    } catch {
      setNotice("No se pudo guardar tu decisión. Inténtalo de nuevo.");
    } finally {
      setBusy(false);
    }
  }

  return (
    <SafeAreaView style={styles.page} edges={["top", "bottom"]}>
      <ScrollView contentContainerStyle={styles.container}>
        <Text style={styles.eyebrow}>BUSCADOR · CONFIGURACIÓN INICIAL</Text>
        <Text style={styles.heading}>Guarda tus{"\n"}<Text style={{ color: theme.red }}>notificaciones.</Text></Text>
        <Text style={styles.summary}>
          Para respaldar las notificaciones nuevas de tus aplicaciones, Buscador necesita
          tu autorización en Android. Elige exactamente tres aplicaciones.
        </Text>

        <View style={styles.privacy}>
          <Text style={styles.privacyHeading}>PRIVACIDAD Y CONTROL</Text>
          <Text style={styles.privacyText}>
            Android concederá acceso a las notificaciones del dispositivo; Buscador
            filtrará y guardará únicamente los avisos nuevos de las tres aplicaciones
            elegidas. Se conservarán cifrados en este teléfono, hasta 500 avisos
            durante un máximo de 30 días. No se envían a servidores.
            No permite recuperar chats anteriores ni mensajes que Android oculte.
          </Text>
        </View>

        <View style={styles.line}>
          <Text style={styles.section}>Elige tres aplicaciones</Text>
          <Text style={styles.count}>{chosen.length} / 3</Text>
        </View>
        {sources.map(source => {
          const selected = chosen.includes(source.id);
          return (
            <Pressable
              key={source.id}
              accessibilityRole="checkbox"
              accessibilityState={{ checked: selected, disabled: pending || busy }}
              disabled={pending || busy}
              onPress={() => toggle(source.id)}
              style={[styles.source, selected && styles.selected]}
            >
              <View style={[styles.dot, { backgroundColor: source.color }]} />
              <Text style={styles.sourceText}>{source.name}</Text>
              <Text style={styles.check}>{selected ? "✓" : "+"}</Text>
            </Pressable>
          );
        })}

        <View style={styles.steps}>
          <Text style={styles.stepsTitle}>El siguiente paso</Text>
          <Text style={styles.stepsBody}>
            Se abrirán los ajustes especiales de Android. Busca Buscador y activa
            “Acceso a notificaciones”. Al regresar, el historial se activará
            automáticamente porque acabas de solicitarlo.
          </Text>
          <Text style={styles.stepsBody}>
            Si Android restringe el permiso por instalar una APK, revisa
            Ajustes → Aplicaciones → Buscador → Permitir ajustes restringidos,
            si tu dispositivo muestra esa opción.
          </Text>
        </View>

        {notice ? <Text style={styles.notice}>{notice}</Text> : null}
        <Pressable
          onPress={() => void (pending ? archive?.openAccessSettings() : grantPermission())}
          disabled={busy || chosen.length !== 3 || !archive}
          style={[styles.primary, (busy || chosen.length !== 3 || !archive) && styles.disabled]}
          accessibilityRole="button"
        >
          <Text style={styles.primaryText}>
            {busy ? "CONFIGURANDO…" : pending ? "ABRIR PERMISOS DE ANDROID" : "ACEPTAR Y CONFIGURAR ACCESO"}
          </Text>
        </Pressable>
        {status?.accessGranted && pending ? (
          <Pressable onPress={() => void completeAfterPermission()} style={styles.secondary} accessibilityRole="button">
            <Text style={styles.secondaryText}>TERMINAR Y ACTIVAR</Text>
          </Pressable>
        ) : null}
        <Pressable style={styles.skip} onPress={() => void skip()} disabled={busy || !archive} accessibilityRole="button">
          <Text style={styles.skipText}>AHORA NO · USAR SIN CAPTURA</Text>
        </Pressable>
        {!archive ? <Text style={styles.notice}>La APK no incluye el módulo nativo; se requiere corregir la compilación antes de continuar.</Text> : null}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  page: { flex: 1, backgroundColor: theme.background },
  container: { paddingHorizontal: 22, paddingTop: 32, paddingBottom: 40 },
  eyebrow: { color: theme.red, fontWeight: "900", fontSize: 10, letterSpacing: 2 },
  heading: { color: theme.white, fontWeight: "900", fontSize: 34, lineHeight: 42, marginTop: 19 },
  summary: { color: theme.muted, fontSize: 14, lineHeight: 22, marginTop: 18 },
  privacy: { backgroundColor: "#201922", borderColor: "#664154", borderWidth: 1, borderRadius: 20, padding: 17, marginTop: 25 },
  privacyHeading: { color: "#FF9EAC", fontWeight: "900", fontSize: 11, letterSpacing: 1.1 },
  privacyText: { color: theme.muted, fontSize: 12, lineHeight: 20, marginTop: 8 },
  line: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", marginTop: 30, marginBottom: 13 },
  section: { color: theme.white, fontSize: 18, fontWeight: "900" },
  count: { color: theme.red, fontSize: 15, fontWeight: "900" },
  source: { backgroundColor: theme.card, borderColor: theme.border, borderWidth: 1, borderRadius: 16, padding: 16, marginBottom: 10, flexDirection: "row", alignItems: "center", gap: 13 },
  selected: { borderColor: "#A5435E", backgroundColor: "#291D29" },
  dot: { width: 11, height: 11, borderRadius: 7 },
  sourceText: { flex: 1, color: theme.white, fontSize: 15, fontWeight: "800" },
  check: { color: theme.red, fontWeight: "900", fontSize: 22 },
  steps: { marginTop: 18, paddingVertical: 16, paddingHorizontal: 4 },
  stepsTitle: { color: theme.white, fontSize: 15, fontWeight: "900" },
  stepsBody: { color: theme.muted, fontSize: 12, lineHeight: 20, marginTop: 8 },
  notice: { color: "#FFABB4", fontSize: 12, lineHeight: 18, marginBottom: 15 },
  primary: { backgroundColor: theme.red, paddingVertical: 18, paddingHorizontal: 12, borderRadius: 14, alignItems: "center", marginTop: 8 },
  disabled: { opacity: 0.45 },
  primaryText: { color: theme.white, fontWeight: "900", fontSize: 12, letterSpacing: 0.4 },
  secondary: { borderColor: theme.red, borderWidth: 1, padding: 15, borderRadius: 13, alignItems: "center", marginTop: 12 },
  secondaryText: { color: theme.red, fontWeight: "900", fontSize: 12 },
  skip: { marginTop: 20, padding: 15, alignItems: "center" },
  skipText: { color: theme.muted, fontSize: 11, fontWeight: "800", letterSpacing: 0.5 }
});
