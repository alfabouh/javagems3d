package workbench.project.game;

import com.google.gson.JsonSyntaxException;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.json.JSONFileManaging;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;
import logger.managers.LoggingManager;
import org.jetbrains.annotations.Nullable;
import workbench.WBench;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.project.map.WBenchMapProjectManager;

import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Objects;

public class WBenchGameProjectManager {
    public static final String MAPS_PATH = "maps";

    private WBenchGameProject currentGameProject;
    private final WBenchMapProjectManager wBenchMapProjectManager;

    public WBenchGameProjectManager() {
        this.currentGameProject = null;
        this.wBenchMapProjectManager = new WBenchMapProjectManager();
    }

    public boolean crateGameProject(JGemsPath absPath, JGemsPath path, String name) {
        try {
            WBenchGameProject wBenchGameProject = new WBenchGameProject(JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.GAME_DATA_VERSION, name);
            this.createGameSystemFiles(absPath, name);
            this.setCurrentProject(path, wBenchGameProject);
            this.saveGameProjectFile();
            Log.get().debug("Created WBenchGameProject: " + wBenchGameProject + ". Path: " + path + " (" + JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.GAME_PROJECT_FILE + ")");

            this.initWorkingSpace(WBenchOpenGLRenderer.getGameEditorInterface());

            return true;
        } catch (JGemsIOException e) {
            LoggingManager.showExceptionDialog("Internal error! Couldn't create object\n" + e.getMessage());
            Log.get().exception(e);
            this.setCurrentProject(null, null);
            return false;
        }
    }

    //TODO
    public void readGameProject() {
        this.refreshMapsFolderData();
    }

    //TODO
    public void saveGameProject(boolean wait) {
        this.saveGameProjectFile();
    }

    public void refreshMapsFolderData() {
        String absPath = this.getCurrentGameProject().getCurrentProjectPath().getFullPath() + "/maps";
        Iterator<String> stringIterator = this.getCurrentGameProject().getMaps().iterator();
        while (stringIterator.hasNext()) {
            String s = stringIterator.next();
            if (!(new File(absPath, s)).exists()) {
                Log.get().warn("Couldn't find: " + s);
                stringIterator.remove();
            }
        }
    }

    public void closeGameProject() {
        if (this.getCurrentGameProject() != null) {
            Log.get().info("Closing project " + this.getCurrentGameProject());
            this.closeWorkingSpace(WBenchOpenGLRenderer.getProjectInterface());
            this.currentGameProject = null;
            Log.get().info("Game successfully closed");
        }
    }

    private void closeWorkingSpace(DearUIInterface dearUIInterface) {
        this.saveGameProject(true);
        WBench.get().openInterface(dearUIInterface);
    }

    public boolean openGameProject(JGemsPath path) {
        try {
            File projectFolder = new File(path.getFullPath());
            if (!projectFolder.exists() || !projectFolder.isDirectory()) {
                throw new JGemsIOException("Invalid path: " + path);
            }

            File[] files = projectFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.GAME_PROJECT_FILE));
            if (files == null || files.length != 1) {
                throw new JGemsIOException("Couldn't find WBenchGameProject file: " + path + " (" + JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.GAME_PROJECT_FILE + ")");
            }

            File projectFile = files[0];
            WBenchGameProject wBenchGameProject = this.readMainFile(new JGemsPath(projectFile.getPath()));
            if (wBenchGameProject == null) {
                return false;
            }
            wBenchGameProject.checkVersion();

            this.createGameSystemFiles(path, wBenchGameProject.getGameTitle());
            Log.get().info("Opened WBenchGameProject: " + wBenchGameProject);
            Log.get().info(wBenchGameProject.getGameInfo());

            //LoadingInterfaceSwing.invoke();
            //LoadingInterfaceSwing.setResource("JSON Processing...");
            this.readGameProject();
            this.initWorkingSpace(WBenchOpenGLRenderer.getGameEditorInterface());

            return true;
        } catch (JGemsIOException e) {
            this.currentGameProject = null;
            LoggingManager.showExceptionDialog("Internal error! Couldn't open project!\n" + e.getMessage());
            Log.get().exception(e);
            return false;
        } finally {
            //LoadingInterfaceSwing.dispose();
        }
    }

    public WBenchGameProject readMainFile(JGemsPath path) {
        try {
            JSONFileManaging jsonFileManaging = JSONFileManaging.create();
            WBenchGameProject wBenchGameProject = jsonFileManaging.readFromFile(new File(path.toString()), WBenchGameProject.class, null);
            this.setCurrentProject(path, wBenchGameProject);
            return wBenchGameProject;
        } catch (JGemsIOException | JsonSyntaxException e) {
            LoggingManager.showExceptionDialog("Internal error! Couldn't open project!\n" + e.getMessage());
            Log.get().exception(e);
            return null;
        }
    }

    private void initWorkingSpace(DearUIInterface dearUIInterface) {
        WBench.get().openInterface(dearUIInterface);
    }

    @SuppressWarnings("all")
    private void createGameSystemFiles(JGemsPath path, String name) {
        new File(new JGemsPath(path, WBenchGameProjectManager.MAPS_PATH).getFullPath()).mkdirs();
    }

    private void saveGameProjectFile() {
        JSONFileManaging jsonFileManaging = JSONFileManaging.create();
        jsonFileManaging.writeToFile(this.getCurrentGameProject(), Objects.requireNonNull(this.getCurrentGameProject()).getCurrentProjectPath().toFile(), null);
    }

    private void setCurrentProject(JGemsPath path, WBenchGameProject currentGameProject) {
        this.currentGameProject = currentGameProject;
        if (currentGameProject != null) {
            currentGameProject.setCurrentProjectPath(path);
        }
    }

    public WBenchGameProject getCurrentGameProject() {
        return this.currentGameProject;
    }

    public WBenchMapProjectManager getMapProjectManager() {
        return this.wBenchMapProjectManager;
    }
}
