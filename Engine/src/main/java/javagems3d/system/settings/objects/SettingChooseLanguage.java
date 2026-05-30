package javagems3d.system.settings.objects;

import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.localisation.LocalizationManager;

public class SettingChooseLanguage extends SettingSlot {

    public SettingChooseLanguage(String name, LocalizationManager.Lang defaultLang) {
        super(name, 0, 0, 1);
        this.setValue(0);
    }

    public int getMax() {
        return JGemsHelper.localisation().getLocalization().max() - 1;
    }

    public String getName(int i) {
        return JGemsHelper.localisation().getLocalization().getLangByID(i).lang();
    }

    public String getCurrentName() {
        return this.getName(this.getValue());
    }

    public LocalizationManager.Lang getCurrentLanguage() {
        return JGemsHelper.localisation().getCurrentLanguage();
    }
}
