const { withMainApplication } = require("expo/config-plugins");

/**
 * Explicitly register the fallback React Native package in MainApplication.
 * Expo's local module remains autolinked for its Android service and storage.
 * This plugin is idempotent and fails early if the native template changes.
 */
module.exports = function withBuscadorNotificationBridge(config) {
  return withMainApplication(config, config => {
    const contents = config.modResults.contents;
    const insertion = "add(expo.modules.buscadornotifications.BuscadorLegacyPackage())";
    if (contents.includes(insertion)) return config;

    const marker = "PackageList(this).packages.apply {";
    if (contents.includes(marker)) {
      config.modResults.contents = contents.replace(marker, marker + "\n              " + insertion);
      return config;
    }

    const directReturn = "return PackageList(this).packages";
    if (contents.includes(directReturn)) {
      config.modResults.contents = contents.replace(
        directReturn,
        "return PackageList(this).packages.apply { " + insertion + " }"
      );
      return config;
    }

    throw new Error(
      "Buscador: cannot register notification bridge in MainApplication; update the Android config plugin"
    );
  });
};
