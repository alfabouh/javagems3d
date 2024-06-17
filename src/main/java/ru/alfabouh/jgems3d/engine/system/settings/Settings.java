package ru.alfabouh.jgems3d.engine.system.settings;

import ru.alfabouh.jgems3d.engine.system.settings.basic.SettingObject;
import ru.alfabouh.jgems3d.engine.system.settings.basic.SettingSlot;
import ru.alfabouh.jgems3d.engine.system.settings.objects.SettingFloatBar;
import ru.alfabouh.jgems3d.engine.system.settings.objects.SettingString;
import ru.alfabouh.jgems3d.engine.system.settings.objects.SettingTrueFalse;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;
import ru.alfabouh.jgems3d.proxy.logger.SystemLogging;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public abstract class Settings {
    private final Set<SettingObject> settingObjectSet;
    private final File optionsFile;

    public Settings(File optionsFile) {
        this.settingObjectSet = new HashSet<>();
        this.optionsFile = optionsFile;
    }

    protected void addSetting(SettingObject settingObject) {
        this.settingObjectSet.add(settingObject);
    }

    public void saveOptions() {
        SystemLogging.get().getLogManager().log("Saving settings...");
        try {
            PrintWriter printwriter = new PrintWriter(new FileWriter(this.getOptionsFile()));
            for (SettingObject settingObject : this.settingObjectSet) {
                if (settingObject instanceof SettingFloatBar) {
                    SettingFloatBar settingFloatBar = (SettingFloatBar) settingObject;
                    printwriter.println(settingObject.getName() + "=" + settingFloatBar.getValue());
                } else if (settingObject instanceof SettingTrueFalse) {
                    SettingTrueFalse settingTrueFalse = (SettingTrueFalse) settingObject;
                    printwriter.println(settingObject.getName() + "=" + settingTrueFalse.isFlag());
                } else if (settingObject instanceof SettingSlot) {
                    SettingSlot settingIntSlots = (SettingSlot) settingObject;
                    printwriter.println(settingObject.getName() + "=" + settingIntSlots.getValue());
                } else if (settingObject instanceof SettingString) {
                    SettingString settingString = (SettingString) settingObject;
                    printwriter.println(settingObject.getName() + "=" + settingString.getValue());
                }
            }
            printwriter.close();
        } catch (Exception e) {
            throw new JGemsException(e);
        }
        SystemLogging.get().getLogManager().log("Settings successfully saved!");
    }

    public void loadOptions() {
        try {
            if (!this.getOptionsFile().exists()) {
                if (!this.getOptionsFile().mkdirs()) {
                    throw new JGemsException("Failed to create settings file!");
                }
                this.saveOptions();
                return;
            }
            BufferedReader bufferedreader = new BufferedReader(new FileReader(this.getOptionsFile()));
            String s;
            while ((s = bufferedreader.readLine()) != null) {
                String[] string = s.split("=");
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
                        } else if (settingObject instanceof SettingSlot) {
                            SettingSlot settingIntSlots = (SettingSlot) settingObject;
                            settingIntSlots.setValue(Integer.parseInt(value));
                        } else if (settingObject instanceof SettingString) {
                            SettingString settingString = (SettingString) settingObject;
                            settingString.setValue(value);
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

    public File getOptionsFile() {
        return this.optionsFile;
    }

    public Set<SettingObject> getSettingObjectSet() {
        return this.settingObjectSet;
    }
}
