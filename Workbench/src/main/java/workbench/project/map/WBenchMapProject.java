package workbench.project.map;

import api.scripting.functions.APIScriptsListing;
import javagems3d.JGems3D;
import javagems3d.mapping.data.MapProjectData;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import workbench.project.managing.instances.IAsset;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Iterator;
import java.util.List;

public class WBenchMapProject extends MapProjectData {
    private transient JGemsPath currentProjectPath;

    public WBenchMapProject(@NotNull String version, @NotNull String projectName) {
        super(JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_DATA_INFO, projectName, version, "");
        this.currentProjectPath = null;
    }

    public JGemsPath getScriptPathTo(String name) {
        return new JGemsPath(this.getCurrentProjectPath().getAbsolutePathDirectory(), WBenchMapProjectManager.SCRIPTS_PATH, name);
    }

    public void reviseScripts() {
        List<String> scriptFiles = this.getScriptFiles();

        Iterator<String> iterator = scriptFiles.iterator();
        while (iterator.hasNext()) {
            String scriptName = iterator.next();
            Path path = this.getScriptPathTo(scriptName).toPath();
            if (!Files.exists(path)) {
                Log.get().warn("Script file not found, removing from list: " + path);
                iterator.remove();
            }
        }

        Path scriptsDir = this.getScriptPathTo("").toPath();
        if (Files.exists(scriptsDir) && Files.isDirectory(scriptsDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(scriptsDir, "*" + JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_SCRIPT_FILE)) {
                for (Path scriptPath : stream) {
                    String fileName = scriptPath.getFileName().toString();
                    if (!scriptFiles.contains(fileName)) {
                        Log.get().info("Adding new script to list: " + fileName);
                        scriptFiles.add(fileName);
                    }
                }
            } catch (IOException e) {
                Log.get().error("Failed to scan scripts directory", e);
            }
        }
    }

    public void createNewScript(String name) {
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        try {
            Path scriptsDir = this.getScriptPathTo("").toPath();
            if (!Files.exists(scriptsDir)) {
                Files.createDirectories(scriptsDir);
            }

            final String scriptFile = name + JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_SCRIPT_FILE;
            Path newScriptPath = scriptsDir.resolve(scriptFile);
            if (Files.exists(newScriptPath)) {
                Log.get().warn("Script already exists: " + newScriptPath);
                return;
            }
            String defaultScript = APIScriptsListing.getApiScriptTemplate();
            Files.write(newScriptPath, defaultScript.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE_NEW);
            this.getScriptFiles().add(scriptFile);

            Log.get().info("Created new script " + scriptFile);
        } catch (IOException e) {
            Log.get().error("Failed to create script", e);
        }
    }

    public void writeScriptFile(String name, String text) {
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        try {
            Path scriptsDir = this.getScriptPathTo("").toPath();
            Path newScriptPath = scriptsDir.resolve(name);
            Files.write(newScriptPath, text.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
            Log.get().info("Wrote script " + name);
        } catch (IOException e) {
            Log.get().error("Failed to write script", e);
        }
    }

    public void deleteScript(int index) {
        List<String> scriptFiles = this.getScriptFiles();

        if (index < 0 || index >= scriptFiles.size()) {
            Log.get().warn("Invalid script index: " + index);
            return;
        }

        try {
            Path scriptPath = this.getScriptPathTo(scriptFiles.get(index)).toPath();

            if (Files.exists(scriptPath)) {
                Files.delete(scriptPath);
            }

            Log.get().info("Script deleted: " + scriptPath);
        } catch (IOException e) {
            Log.get().error("Failed to delete script", e);
        } finally {
            scriptFiles.remove(index);
        }
    }

    public void setMapDataFile(String mapDataFile) {
        this.mapDataFile = mapDataFile;
    }

    public void setCurrentProjectPath(JGemsPath currentProjectPath) {
        this.currentProjectPath = currentProjectPath;
    }

    public JGemsPath getCurrentProjectPath() {
        return this.currentProjectPath;
    }

    public String toString() {
        return this.getMapName() + " - " + this.getVersion();
    }
}