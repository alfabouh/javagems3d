/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

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
