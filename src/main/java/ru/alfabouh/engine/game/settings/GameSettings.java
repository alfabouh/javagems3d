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
    public SettingIntSlots shadowQuality;
    public SettingIntSlots windowMode;
    public SettingIntSlots vSync;
    public SettingIntSlots anisotropic;
    public SettingIntSlots msaa;
    public SettingIntSlots texturesQuality;
    public SettingIntSlots texturesFiltering;
    public SettingIntSlots bloom;

    public GameSettings() {
        this.settingObjectSet = new HashSet<>();

        this.optionsFile = new File(Game.getGameFilesFolder().toFile(), "settings.txt");
        this.soundGain = new SettingFloatBar("sound_gain", 1.0f);

        //this.windowMode = new SettingIntSlots("windowMode", 0, 0, 1);
        //this.shadowQuality = new SettingIntSlots("shadowQuality", 1, 0, 2);
        //this.vSync = new SettingIntSlots("vSync", 1, 0, 1);
        //this.anisotropic = new SettingIntSlots("anisotropic", 1, 0, 1);
        //this.msaa = new SettingIntSlots("msaa", 2, 0, 3);
        //this.texturesQuality = new SettingIntSlots("texturesQuality", 2, 0, 2);
        //this.bloom = new SettingIntSlots("bloom", 1, 0, 1);
        //this.texturesFiltering = new SettingIntSlots("texturesFiltering", 1, 0, 1);

        this.windowMode = new SettingIntSlots("windowMode", 0, 0, 1);
        this.shadowQuality = new SettingIntSlots("shadowQuality", 0, 0, 2);
        this.vSync = new SettingIntSlots("vSync", 1, 0, 1);
        this.anisotropic = new SettingIntSlots("anisotropic", 0, 0, 1);
        this.msaa = new SettingIntSlots("msaa", 0, 0, 3);
        this.texturesQuality = new SettingIntSlots("texturesQuality", 0, 0, 2);
        this.bloom = new SettingIntSlots("bloom", 0, 0, 1);
        this.texturesFiltering = new SettingIntSlots("texturesFiltering", 0, 0, 1);

        this.texturesFiltering.addName(0, "Off");
        this.texturesFiltering.addName(1, "On");

        this.bloom.addName(0, "Off");
        this.bloom.addName(1, "On");

        this.anisotropic.addName(0, "Off");
        this.anisotropic.addName(1, "On");

        this.vSync.addName(0, "Off");
        this.vSync.addName(1, "On");

        this.windowMode.addName(0, "FullScreen");
        this.windowMode.addName(1, "Windowed");

        this.shadowQuality.addName(0, "Low");
        this.shadowQuality.addName(1, "Medium");
        this.shadowQuality.addName(2, "High");

        this.texturesQuality.addName(0, "Low");
        this.texturesQuality.addName(1, "Medium");
        this.texturesQuality.addName(2, "High");

        this.msaa.addName(0, "Off");
        this.msaa.addName(1, "2x");
        this.msaa.addName(2, "4x");
        this.msaa.addName(3, "8x");

        this.addSetting(this.windowMode);
        this.addSetting(this.soundGain);
        this.addSetting(this.shadowQuality);
        this.addSetting(this.vSync);
        this.addSetting(this.anisotropic);
        this.addSetting(this.msaa);
        this.addSetting(this.texturesQuality);
        this.addSetting(this.bloom);
        this.addSetting(this.texturesFiltering);
    }

    public File getOptionsFile() {
        return this.optionsFile;
    }

    private void addSetting(SettingObject settingObject) {
        this.settingObjectSet.add(settingObject);
    }

    public void saveOptions() {
        Game.getGame().getLogManager().log("Saving settings...");
        try {
            PrintWriter printwriter = new PrintWriter(new FileWriter(this.getOptionsFile()));
            for (SettingObject settingObject : this.settingObjectSet) {
                if (settingObject instanceof SettingFloatBar) {
                    SettingFloatBar settingFloatBar = (SettingFloatBar) settingObject;
                    printwriter.println(settingObject.getName() + ":" + settingFloatBar.getValue());
                } else if (settingObject instanceof SettingTrueFalse) {
                    SettingTrueFalse settingTrueFalse = (SettingTrueFalse) settingObject;
                    printwriter.println(settingObject.getName() + ":" + settingTrueFalse.isFlag());
                } else if (settingObject instanceof SettingIntSlots) {
                    SettingIntSlots settingTrueFalse = (SettingIntSlots) settingObject;
                    printwriter.println(settingObject.getName() + ":" + settingTrueFalse.getValue());
                }
            }
            printwriter.close();
        } catch (Exception e) {
            throw new GameException(e);
        }
        Game.getGame().getLogManager().log("Settings successfully saved!");
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
                        } else if (settingObject instanceof SettingIntSlots) {
                            SettingIntSlots settingTrueFalse = (SettingIntSlots) settingObject;
                            settingTrueFalse.setValue(Integer.parseInt(value));
                        }
                        break;
                    }
                }
            }
            bufferedreader.close();
        } catch (Exception e) {
            if (this.getOptionsFile().exists()) {
                this.getOptionsFile().delete();
            }
        }
    }
}