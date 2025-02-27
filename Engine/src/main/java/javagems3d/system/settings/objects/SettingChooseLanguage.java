package javagems3d.system.settings.objects;

import javagems3d.system.resources.localisation.Lang;

public class SettingChooseLanguage extends SettingSlot {

    public SettingChooseLanguage(String name, Lang defaultLang) {
        super(name, 0, 0, 1);
        this.setValue(0);
    }

    public int getMax() {
        return Lang.getAllLanguages().size() - 1;
    }

    public String getName(int i) {
        return Lang.getAllLanguages().get(i).getFullName();
    }

    public String getCurrentName() {
        return this.getName(this.getValue());
    }

    public Lang getCurrentLanguage() {
        return Lang.getAllLanguages().get(this.getValue());
    }
}
