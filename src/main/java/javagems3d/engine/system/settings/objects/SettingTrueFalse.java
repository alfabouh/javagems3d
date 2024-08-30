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

public class SettingTrueFalse extends SettingObject<Boolean> {
    public SettingTrueFalse(String name, boolean defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public Boolean tryParseFromString(String string) {
        return Boolean.parseBoolean(string);
    }
}