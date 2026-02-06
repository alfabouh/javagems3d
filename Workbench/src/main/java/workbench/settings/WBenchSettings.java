package workbench.settings;

import com.google.gson.reflect.TypeToken;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.json.JSONFileManaging;
import javagems3d.system.service.files.JGemsPath;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

public class WBenchSettings implements Serializable {
    public static final String extension = "settings.json";
    public static final long serialVersionUID = 143L;

    private final Deque<String> recentProjects;
    private float camSpeed;

    public WBenchSettings() {
        this.recentProjects = new ArrayDeque<>();
        this.camSpeed = JGemsConfig.SYSTEM.CAM_SENS;
    }

    @SuppressWarnings("all")
    public static void save(WBenchSettings wBenchSettings, JGemsPath path) throws JGemsIOException {
        JSONFileManaging jsonFileManaging = JSONFileManaging.createSerializationRules();
        jsonFileManaging.writeToFile(wBenchSettings, new File(path.getFullPath(), WBenchSettings.extension), null);
    }

    @SuppressWarnings("all")
    public static WBenchSettings load(JGemsPath path) throws JGemsIOException {
        File file = new File(path.getFullPath(), WBenchSettings.extension);
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

    public WBenchSettings setCamSpeed(float camSpeed) {
        this.camSpeed = camSpeed;
        return this;
    }

    public float getCamSpeed() {
        return this.camSpeed;
    }

    public Deque<String> getRecentProjects() {
        return this.recentProjects;
    }
}
