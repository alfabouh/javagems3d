package workbench.project.game;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.system.external.gaming.JGemsGaming;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.json.JSONFileManaging;
import javagems3d.system.service.files.JGemsPath;
import logger.Log;
import logger.managers.LoggingManager;
import org.jetbrains.annotations.NotNull;
import workbench.WBench;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.project.managing.WBenchGameResourcesManager;
import workbench.project.map.WBenchMapProjectManager;
import workbench.resources.WBenchResourceManager;
import workbench.resources.frame.LoadingInterfaceSwing;

import java.io.File;
import java.util.Objects;

public class WBenchGameProjectManager {
    public static final String MAPS_PATH = JGemsGaming.SYS_MAPS_FOLDER;

    private WBenchGameProject currentGameProject;
    private final WBenchMapProjectManager wBenchMapProjectManager;
    private WBenchGameResourcesManager wBenchGameResourcesManager;

    public WBenchGameProjectManager() {
        this.currentGameProject = null;
        this.wBenchMapProjectManager = new WBenchMapProjectManager();
    }

    public boolean createGameProject(JGemsPath absPath, JGemsPath path, String name) {
        try {
            WBenchGameProject wBenchGameProject = new WBenchGameProject(JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.GAME_DATA_VERSION, name);
            this.createGameSystemFiles(absPath, name);
            this.setCurrentProject(path, wBenchGameProject);
            this.initLocalGameResources();
            this.saveGameProjectFile();
            Log.get().debug("Created WBenchGameProject: " + wBenchGameProject + ". Path: " + path + " (" + JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.GAME_PROJECT_FILE + ")");
            this.initWorkingSpace(WBenchOpenGLRenderer.getGameEditorInterface());
            return true;
        } catch (JGemsIOException e) {
            LoggingManager.showExceptionDialog("Internal error! Couldn't create object", e);
            Log.get().exception(e);
            this.setCurrentProject(null, null);
            return false;
        } finally {
            LoadingInterfaceSwing.dispose();
        }
    }

    //TODO
    public void saveGameProject(boolean wait) {
        this.saveGameProjectFile();
    }

    public void closeGameProject() {
        if (this.getGameProject() != null) {
            Log.get().info("Closing project " + this.getGameProject());
            this.closeWorkingSpace(WBenchOpenGLRenderer.getProjectInterface());
            this.currentGameProject = null;
            Log.get().info("Game successfully closed");
        }
    }

    private void closeWorkingSpace(DearUIInterface dearUIInterface) {
        this.saveGameProject(true);
        this.destroyLocalGameResources();
        WBench.get().openInterface(dearUIInterface);
    }

    public void refreshScripts(boolean showLoadingScreen) {
        if (showLoadingScreen) {
            LoadingInterfaceSwing.invoke();
        }
        this.getGameResourcesManager().refreshScripts(this.getGameProject().getProjectAbsolutePath());
        if (showLoadingScreen) {
            LoadingInterfaceSwing.dispose();
        }
    }

    public void refreshMaps(boolean showLoadingScreen) {
        if (showLoadingScreen) {
            LoadingInterfaceSwing.invoke();
        }
        this.getGameResourcesManager().refreshMaps(this.getGameProject().getProjectAbsolutePath());
        if (showLoadingScreen) {
            LoadingInterfaceSwing.dispose();
        }
    }

    public void refreshModelFiles(boolean showLoadingScreen) {
        if (showLoadingScreen) {
            LoadingInterfaceSwing.invoke();
        }
        this.getGameResourcesManager().refreshModels(this.getGameProject().getProjectAbsolutePath());
        if (showLoadingScreen) {
            LoadingInterfaceSwing.dispose();
        }
    }

    public void refreshTextureFiles(boolean showLoadingScreen) {
        if (showLoadingScreen) {
            LoadingInterfaceSwing.invoke();
        }
        this.getGameResourcesManager().refreshTextures(this.getGameProject().getProjectAbsolutePath());
        if (showLoadingScreen) {
            LoadingInterfaceSwing.dispose();
        }
    }

    public void saveResourceObjectFiles(@NotNull WBenchGameResourcesManager.AssetsTarget assetsTarget) {
        this.getGameResourcesManager().saveCreatableResourceObjects(assetsTarget, this.getGameProject().getProjectAbsolutePath());
    }

    private void initLocalGameResources() {
        LoadingInterfaceSwing.invoke();
        this.wBenchGameResourcesManager = new WBenchGameResourcesManager(WBench.get().getResourceManager().getLocalGameEditorResources());
        ((WBenchOpenGLRenderer) (WBench.get().getScreen().getScene().getSceneRenderer())).getDebugLinesDrawer().setup();
        this.refreshModelFiles(false);
        this.refreshTextureFiles(false);
        this.refreshMaps(false);
        this.refreshScripts(false);
        this.getGameResourcesManager().readCreatableResourceObjects(WBenchGameResourcesManager.AssetsTarget.ALL, this.getGameProject().getProjectAbsolutePath());
        WBenchResourceManager.createLocalGameEditorShaders();
        WBenchResourceManager.setDefaultRenderTableValues();
        WBench.get().getResourceManager().initLocalGameEditorResources();
        WBench.get().getResourceManager().loadLocalGameEditorResources();
    }

    private void destroyLocalGameResources() {
        WBench.get().getResourceManager().destroyLocalGameEditorResources();
        ((WBenchOpenGLRenderer) (WBench.get().getScreen().getScene().getSceneRenderer())).getDebugLinesDrawer().clear();
    }

    public boolean openGameProject(JGemsPath path) {
        try {
            File projectFolder = new File(path.fullPath());
            if (!projectFolder.exists() || !projectFolder.isDirectory()) {
                throw new JGemsIOException("Invalid files: " + path);
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

            this.initLocalGameResources();
            LoadingInterfaceSwing.setResource("JSON Processing...");
            this.initWorkingSpace(WBenchOpenGLRenderer.getGameEditorInterface());

            return true;
        } catch (JGemsIOException e) {
            this.currentGameProject = null;
            LoggingManager.showExceptionDialog("Internal error! Couldn't open project!", e);
            Log.get().exception(e);
            return false;
        } finally {
            LoadingInterfaceSwing.dispose();
        }
    }

    public WBenchGameProject readMainFile(@NotNull JGemsPath path) {
        try {
            JSONFileManaging jsonFileManaging = JSONFileManaging.createSerializationRules();
            WBenchGameProject wBenchGameProject = jsonFileManaging.readFromFile(new File(path.toString()), new TypeToken<WBenchGameProject>(){}, null);
            this.setCurrentProject(path, wBenchGameProject);
            return wBenchGameProject;
        } catch (JGemsIOException | JsonSyntaxException e) {
            LoggingManager.showExceptionDialog("Internal error! Couldn't open project!", e);
            Log.get().exception(e);
            return null;
        }
    }

    public WBenchGameResourcesManager getGameResourcesManager() {
        return this.wBenchGameResourcesManager;
    }

    private void initWorkingSpace(DearUIInterface dearUIInterface) {
        WBench.get().openInterface(dearUIInterface);
    }

    @SuppressWarnings("all")
    private void createGameSystemFiles(JGemsPath path, String name) {
        new File(new JGemsPath(path, WBenchGameProjectManager.MAPS_PATH).fullPath()).mkdirs();
    }

    private void saveGameProjectFile() {
        JSONFileManaging jsonFileManaging = JSONFileManaging.createSerializationRules();
        jsonFileManaging.writeToFile(this.getGameProject(), Objects.requireNonNull(this.getGameProject()).getCurrentProjectFilePath().toFile(), null);
        this.getGameResourcesManager().saveCreatableResourceObjects(WBenchGameResourcesManager.AssetsTarget.ALL, this.getGameProject().getProjectAbsolutePath());
    }

    private void setCurrentProject(JGemsPath path, WBenchGameProject currentGameProject) {
        this.currentGameProject = currentGameProject;
        if (currentGameProject != null) {
            currentGameProject.setCurrentProjectPath(path);
        }
    }

    public JGemsPath getMapsPath() {
        return new JGemsPath(this.getGameProject().getProjectAbsolutePath(), WBenchGameProjectManager.MAPS_PATH);
    }

    public WBenchGameProject getGameProject() {
        return this.currentGameProject;
    }

    public WBenchMapProjectManager getMapProjectManager() {
        return this.wBenchMapProjectManager;
    }
}
