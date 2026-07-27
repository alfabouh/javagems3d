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

package workbench.settings;

import com.google.gson.reflect.TypeToken;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.json.JSONFileManaging;
import javagems3d.system.service.files.JGemsPath;
import logger.managers.LoggingManager;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

public class WBenchSettings implements Serializable {
    public static final String extension = "settings.json";
    public static final long serialVersionUID = 143L;

    private final Deque<String> recentProjects;
    private float camSens;
    private float camSpeed;

    public WBenchSettings() {
        this.recentProjects = new ArrayDeque<>();
        this.camSens = JGemsConfig.SYSTEM.CAM_SENS;
        this.camSpeed = JGemsConfig.SYSTEM.CAM_SPEED;
    }

    @SuppressWarnings("all")
    public static void save(WBenchSettings wBenchSettings, JGemsPath path) throws JGemsIOException {
        JSONFileManaging jsonFileManaging = JSONFileManaging.createSerializationRules();
        jsonFileManaging.writeToFile(wBenchSettings, new File(path.fullPath(), WBenchSettings.extension), null);
    }

    @SuppressWarnings("all")
    public static WBenchSettings load(JGemsPath path) throws JGemsIOException {
        File file = new File(path.fullPath(), WBenchSettings.extension);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
        JSONFileManaging jsonFileManaging = JSONFileManaging.createSerializationRules();
        WBenchSettings settings = jsonFileManaging.readFromFile(file, new TypeToken<WBenchSettings>(){}, null);
        return settings == null ? new WBenchSettings() : settings;
    }

    public void addPath(String path) {
        if (this.getRecentProjects().contains(path)) {
            return;
        }
        this.getRecentProjects().addFirst(path);
        if (this.getRecentProjects().size() > 6) {
            this.getRecentProjects().removeLast();
        }
    }

    public float getCamSpeed() {
        return this.camSpeed;
    }

    public WBenchSettings setCamSpeed(float camSpeed) {
        this.camSpeed = camSpeed;
        return this;
    }

    public WBenchSettings setCamSens(float camSens) {
        this.camSens = camSens;
        return this;
    }

    public float getCamSens() {
        return this.camSens;
    }

    public Deque<String> getRecentProjects() {
        return this.recentProjects;
    }
}
