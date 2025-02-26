package javagems3d.help;

import javagems3d.JGems3D;
import javagems3d.system.resources.localisation.JGemsLocalisation;
import javagems3d.system.resources.localisation.Lang;
import javagems3d.system.service.path.JGemsPath;

public abstract class JGemsLocalisationHelper {
    public static Lang createLocalisation(String langName, JGemsPath path) {
        return JGemsLocalisation.createLocalisation(langName, path);
    }

    public static void setLangLocalisationPath(Lang lang, JGemsPath path) {
        JGemsLocalisation.setLangLocalisationPath(lang, path);
    }

    public static JGemsLocalisation getLocalisation() {
        return JGems3D.get().getLocalisation();
    }
}
