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

package javagems3d.engine.system.settings.objects;

import javagems3d.engine.system.resources.localisation.Lang;

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
