package toolbox.settings;

import javagems3d.system.settings.base.Settings;
import javagems3d.system.settings.objects.SettingString;

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