package ru.alfabouh.engine.system.settings.objects;

import ru.alfabouh.engine.system.localisation.Lang;
import ru.alfabouh.engine.system.settings.basic.SettingSlot;

import java.util.HashMap;
import java.util.Map;

public class SettingChooseLanguage extends SettingSlot {
    private final Map<Integer, Lang> integerLangMap;

    public SettingChooseLanguage(String name, Lang defaultLang) {
        super(name, 0, 0, Lang.values().length - 1);
        this.integerLangMap = new HashMap<>();

        for (int i = 0; i < Lang.values().length; i++) {
            this.integerLangMap.put(i, Lang.values()[i]);
        }

        this.setValue(this.getLocalLangId(defaultLang));
    }

    private int getLocalLangId(Lang lang) {
        for (Map.Entry<Integer, Lang> langEntry : this.integerLangMap.entrySet()) {
            if (lang.equals(langEntry.getValue())) {
                return langEntry.getKey();
            }
        }
        return 0;
    }

    public String getName(int i) {
        return this.integerLangMap.containsKey(i) ? this.integerLangMap.get(i).getFullName() : Integer.toString(i);
    }

    public String getCurrentName() {
        return this.getName(this.getValue());
    }

    public Lang getCurrentLanguage() {
        return this.integerLangMap.get(this.getValue());
    }
}
