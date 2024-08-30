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

package javagems3d.toolbox.settings;

import javagems3d.engine.system.settings.base.Settings;
import javagems3d.engine.system.settings.objects.SettingString;

import java.io.File;

public class TBoxSettings extends Settings {
    public SettingString recentPathSave;
    public SettingString recentPathOpen;

    public TBoxSettings(File file) {
        super(file);
        this.recentPathSave = new SettingString("recent_path_save", "");
        this.recentPathOpen = new SettingString("recent_path_open", "");

        this.addSetting(this.recentPathSave);
        this.addSetting(this.recentPathOpen);
    }
}