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

import javagems3d.JGems3D;
import javagems3d.system.service.collections.Pair;

import java.util.HashMap;
import java.util.Map;

public class SettingIntSlots extends SettingSlot {
    private final Map<Integer, Pair<String, Boolean>> intNames;

    public SettingIntSlots(String name, int defaultValue, int min, int max) {
        super(name, defaultValue, min, max);
        this.intNames = new HashMap<>();
    }

    public void addArticle(int i, String name, boolean isKeyForI18n) {
        this.intNames.put(i, new Pair<>(name, isKeyForI18n));
    }

    public String getName(int i) {
        return this.intNames.containsKey(i) ? (this.intNames.get(i).second() ? JGems3D.get().I18n(this.intNames.get(i).first()) : this.intNames.get(i).first()) : Integer.toString(i);
    }

    public String getCurrentName() {
        return this.getName(this.getValue());
    }
}
