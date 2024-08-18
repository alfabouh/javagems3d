/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package ru.jgems3d.engine.system.settings.objects;

import ru.jgems3d.engine.system.resources.localisation.Lang;

import java.util.HashMap;
import java.util.Map;

public class SettingChooseLanguage extends SettingSlot {
    private final Map<Integer, Lang> integerLangMap;

    public SettingChooseLanguage(String name, Lang defaultLang) {
        super(name, 0, 0, Lang.getAllLanguages().size() - 1);
        this.integerLangMap = new HashMap<>();

        int i = 0;
        for (Lang l : Lang.getAllLanguages()) {
            this.integerLangMap.put(i++, l);
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
