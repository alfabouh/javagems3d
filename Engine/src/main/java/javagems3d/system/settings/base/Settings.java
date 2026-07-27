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

package javagems3d.system.settings.base;

import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.settings.objects.SettingObject;
import logger.Log;

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

    public void addSetting(SettingObject<? extends Serializable> settingObject) {
        this.getSettingObjectMap().put(settingObject.getName(), settingObject);
    }

    public void saveOptions() {
        Log.get().trace("Saving settings...");
        try {
            PrintWriter printwriter = new PrintWriter(new FileWriter(this.getOptionsFile()));
            for (SettingObject<? extends Serializable> settingObject : this.getSettingObjectMap().values()) {
                printwriter.println(settingObject.getName() + "=" + settingObject.getValue());
            }
            printwriter.close();
        } catch (Exception e) {
            throw new JGemsRuntimeException(e);
        }
        Log.get().info("Settings successfully saved");
    }

    public boolean makeSettingDirs() {
        if (!this.getOptionsFile().exists()) {
            try {
                this.getOptionsFile().getParentFile().mkdirs();
                if (!this.getOptionsFile().createNewFile()) {
                    throw new JGemsRuntimeException("Failed to create settings files");
                }
            } catch (JGemsRuntimeException | IOException e) {
                throw new JGemsRuntimeException(e);
            }
            this.saveOptions();
            return true;
        }
        return false;
    }

    public void loadOptions() {
        try {
            BufferedReader bufferedreader = new BufferedReader(new FileReader(this.getOptionsFile()));
            String s;
            while ((s = bufferedreader.readLine()) != null) {
                String[] string = s.split("=");
                if (string.length != 2) {
                    continue;
                }
                String txt = string[0];
                String value = string[1];
                SettingObject<? extends Serializable> settingObject = this.getSettingObjectMap().get(txt);
                if (settingObject != null) {
                    try {
                        settingObject.setValue(settingObject.tryParseFromString(value));
                    } catch (Exception e) {
                        Log.get().exception(e);
                        settingObject.setDefault();
                    }
                }
            }
            bufferedreader.close();
        } catch (Exception e) {
            Log.get().exception(e);
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
