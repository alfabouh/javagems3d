package ru.jgems3d.engine.system.settings.base;

import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.service.exceptions.JGemsRuntimeException;
import ru.jgems3d.engine.system.settings.objects.SettingObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public abstract class Settings {
    private final Map<String, SettingObject<? extends Serializable>> settingObjectMap;
    private final File optionsFile;

    public Settings(File optionsFile) {
        this.settingObjectMap = new HashMap<>();
        this.optionsFile = optionsFile;
    }

    protected void addSetting(SettingObject<? extends Serializable> settingObject) {
        this.getSettingObjectMap().put(settingObject.getName(), settingObject);
    }

    public void saveOptions() {
        JGemsHelper.getLogger().log("Saving settings...");
        try {
            PrintWriter printwriter = new PrintWriter(new FileWriter(this.getOptionsFile()));
            for (SettingObject<? extends Serializable> settingObject : this.getSettingObjectMap().values()) {
                printwriter.println(settingObject.getName() + "=" + settingObject.getValue());
            }
            printwriter.close();
        } catch (Exception e) {
            throw new JGemsRuntimeException(e);
        }
        JGemsHelper.getLogger().log("Settings successfully saved!");
    }

    public void loadOptions() {
        try {
            if (!this.getOptionsFile().exists()) {
                if (!this.getOptionsFile().mkdirs()) {
                    throw new JGemsRuntimeException("Failed to create settings file!");
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
                SettingObject<? extends Serializable> settingObject = this.getSettingObjectMap().get(txt);
                if (settingObject != null) {
                    try {
                        settingObject.setValue(settingObject.tryParseFromString(value));
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                        settingObject.setDefault();
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

    public Map<String, SettingObject<? extends Serializable>> getSettingObjectMap() {
        return this.settingObjectMap;
    }
}
