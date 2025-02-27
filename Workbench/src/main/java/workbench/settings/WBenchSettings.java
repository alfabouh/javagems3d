package workbench.settings;

import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.json.JSONFileManaging;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

public class WBenchSettings implements Serializable {
    public static final String extension = "settings.json";

    public static final long serialVersionUID = 143L;
    private final Deque<String> recentProjects;

    public WBenchSettings() {
        this.recentProjects = new ArrayDeque<>();
    }

    public static void save(WBenchSettings wBenchSettings, JGemsPath path) throws JGemsIOException {
        JSONFileManaging jsonFileManaging = JSONFileManaging.create();
        jsonFileManaging.writeToFile(wBenchSettings, new File(path.getFullPath(), WBenchSettings.extension), null);
    }

    @SuppressWarnings("all")
    public static @NotNull WBenchSettings load(JGemsPath path) throws JGemsIOException {
        File file = new File(path.getFullPath(), WBenchSettings.extension);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
        JSONFileManaging jsonFileManaging = JSONFileManaging.create();
        return jsonFileManaging.readFromFile(file, WBenchSettings.class, null);
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

    public Deque<String> getRecentProjects() {
        return this.recentProjects;
    }
}
