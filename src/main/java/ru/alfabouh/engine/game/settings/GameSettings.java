package ru.alfabouh.engine.game.settings;

import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.exception.GameException;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class GameSettings {
    private final Set<SettingObject> settingObjectSet;
    public File optionsFile;
    public SettingFloatBar soundGain;
    public SettingFloatBar msaa;
    public SettingFloatBar shadowQuality;
    public SettingTrueFalse fullScreen;
    public SettingTrueFalse anisotropicFiltering;
    public SettingTrueFalse textureFiltering;
    public SettingTrueFalse vSync;

    public GameSettings() {
        this.settingObjectSet = new HashSet<>();

        this.optionsFile = new File(Game.getGameFilesFolder().toFile(), "settings.txt");
        this.soundGain = new SettingFloatBar("sound_gain", 1.0f);
        this.msaa = new SettingFloatBar("msaa", 0.0f);
        this.shadowQuality = new SettingFloatBar("shadowQuality", 2.0f);
        this.fullScreen = new SettingTrueFalse("fullscreen", true);
        this.anisotropicFiltering = new SettingTrueFalse("anisotropicFiltering", false);
        this.textureFiltering = new SettingTrueFalse("textureFiltering", false);
        this.vSync = new SettingTrueFalse("vSync", true);

        this.addSetting(this.soundGain);
        this.addSetting(this.msaa);
        this.addSetting(this.shadowQuality);
        this.addSetting(this.fullScreen);
        this.addSetting(this.anisotropicFiltering);
        this.addSetting(this.textureFiltering);
        this.addSetting(this.vSync);
    }

    public File getOptionsFile() {
        return this.optionsFile;
    }

    private void addSetting(SettingObject settingObject) {
        this.settingObjectSet.add(settingObject);
    }

    public void saveOptions() {
        try {
            PrintWriter printwriter = new PrintWriter(new FileWriter(this.getOptionsFile()));
            for (SettingObject settingObject : this.settingObjectSet) {
                if (settingObject instanceof SettingFloatBar) {
                    SettingFloatBar settingFloatBar = (SettingFloatBar) settingObject;
                    printwriter.println(settingObject.getName() + ":" + settingFloatBar.getValue());
                } else if (settingObject instanceof SettingTrueFalse) {
                    SettingTrueFalse settingTrueFalse = (SettingTrueFalse) settingObject;
                    printwriter.println(settingObject.getName() + ":" + settingTrueFalse.isFlag());
                }
            }
            printwriter.close();
        } catch (Exception e) {
            throw new GameException(e);
        }
    }

    public void loadOptions() {
        try {
            if (!this.getOptionsFile().exists()) {
                if (!this.getOptionsFile().createNewFile()) {
                    throw new GameException("Failed to create settings file!");
                }
                this.saveOptions();
                return;
            }
            BufferedReader bufferedreader = new BufferedReader(new FileReader(this.getOptionsFile()));
            String s;
            while ((s = bufferedreader.readLine()) != null) {
                String[] string = s.split(":");
                String txt = string[0];
                String value = string[1];
                for (SettingObject settingObject : this.settingObjectSet) {
                    if (settingObject.getName().equals(txt)) {
                        if (settingObject instanceof SettingFloatBar) {
                            SettingFloatBar settingFloatBar = (SettingFloatBar) settingObject;
                            settingFloatBar.setValue(Float.parseFloat(value));
                        } else if (settingObject instanceof SettingTrueFalse) {
                            SettingTrueFalse settingTrueFalse = (SettingTrueFalse) settingObject;
                            settingTrueFalse.setFlag(value.equals("true"));
                        }
                        break;
                    }
                }
            }
            bufferedreader.close();
        } catch (Exception e) {
            throw new GameException(e);
        }
    }
}