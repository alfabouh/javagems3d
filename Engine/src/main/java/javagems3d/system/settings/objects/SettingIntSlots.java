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
        return this.intNames.containsKey(i) ? (this.intNames.get(i).getSecond() ? JGems3D.get().I18n(this.intNames.get(i).getFirst()) : this.intNames.get(i).getFirst()) : Integer.toString(i);
    }

    public String getCurrentName() {
        return this.getName(this.getValue());
    }
}
