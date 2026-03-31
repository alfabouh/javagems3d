package javagems3d.system.settings.objects;

import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.localisation.LocalisationManager;

public class SettingChooseLanguage extends SettingSlot {

    public SettingChooseLanguage(String name, LocalisationManager.Lang defaultLang) {
        super(name, 0, 0, 1);
        this.setValue(0);
    }

    public int getMax() {
        return JGemsHelper.localisation().getLocalisation().max() - 1;
    }

    public String getName(int i) {
        return JGemsHelper.localisation().getLocalisation().getLangByID(i).lang();
    }

    public String getCurrentName() {
        return this.getName(this.getValue());
    }

    public LocalisationManager.Lang getCurrentLanguage() {
        return JGemsHelper.localisation().getCurrentLanguage();
    }
}
