/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d;

import javagems3d.graphics.rendering.scene.ISceneRenderer;
import javagems3d.system.os.OS;
import javagems3d.system.os.SysOSValidation;
import logger.managers.LoggingManager;
import org.lwjgl.glfw.GLFW;
import api.bridge.APIContainer;
import api.bridge.APILauncher;
import api.bridge.events.APIEventsLauncher;
import javagems3d.audio.JGemsSoundManager;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.screen.JGemsScreen;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.world.thread.JGemsPhysics;
import javagems3d.system.core.JGemsCore;
import javagems3d.system.map.loaders.IMapLoader;
import javagems3d.system.resources.localisation.JGemsLocalisation;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.exceptions.JGemsNotFoundException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.path.JGemsPath;
import javagems3d.system.service.synchronizing.SyncManager;
import javagems3d.system.settings.JGemsSettings;
import api.app.events.bus.Events;
import logger.SystemLogging;
import logger.managers.JGemsLogging;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Random;

public final class JGems3D {
    private final OS os;
    public static int MAP_MAX_SIZE = 128;

    public static boolean DEBUG_MODE = false;
    public static boolean FIRST_LAUNCH = false;
    public static long rngSeed;
    public static Random random;
    private static JGems3D mainObject;

    private JGemsCore core;
    private final JGemsSettings jGemsSettings;
    private final JGemsLocalisation jGemsLocalisation;

    private boolean shouldBeClosed;

    private JGems3D() throws JGemsRuntimeException {
        try {
            SystemLogging.get().setCurrentLogging(SystemLogging.jGemsLogging);
            this.api();
            JGems3D.checkFilesDirectory();
        } catch (IOException e) {
            throw new JGemsRuntimeException(e);
        }
        this.os = SysOSValidation.getCurrentOS();

        JGems3D.rngSeed = JGems3D.systemTime();
        JGems3D.random = new Random(JGems3D.rngSeed);

        this.jGemsSettings = new JGemsSettings(new File(JGems3D.getGameFilesFolder().toFile(), "jgems_settings.txt"));
        this.jGemsLocalisation = new JGemsLocalisation();
        this.shouldBeClosed = false;
    }

    public OS getOS() {
        return this.os;
    }

    private void api() {
        APILauncher.get().launchGameAPI();
        APILauncher.get().launchToolBoxAPI();
        APILauncher.get().disposeReflection();
    }

    public static long systemTime() {
        return System.currentTimeMillis();
    }

    public static double glfwTime() {
        return GLFW.glfwGetTime();
    }

    @SuppressWarnings("all")
    public static void launch() {
        if (JGems3D.mainObject != null) {
            throw new JGemsRuntimeException("Couldn't launch JavaGems more than 1 times");
        }
        try {
            JGems3D.mainObject = new JGems3D();
        } catch (JGemsRuntimeException e) {
            LoggingManager.showExceptionDialog(e.getMessage());
            e.printStackTrace(System.err);
            return;
        }
        JGems3D.start();
    }

    public static JGems3D get() {
        return JGems3D.mainObject;
    }

    private static void start() {
        try {
            JGemsHelper.getLogger().debug("BEGIN");
            JGemsHelper.getLogger().info("Starting system! Date: " + JGems3D.date());
            JGemsHelper.getLogger().info(JGems3D.getGameString() + ": " + JGemsCore.ENG_NAME + " - " + JGemsCore.ENG_VER);
            JGemsHelper.getLogger().info("===============================================================");
            JGemsHelper.getLogger().info("Loading settings from path...");
            if (JGems3D.get().getGameSettings().makeSettingDirs()) {
                JGems3D.FIRST_LAUNCH = true;
            } else {
                JGems3D.get().getGameSettings().loadOptions();
            }
            JGems3D.get().core = new JGemsCore();
            JGems3D.get().getCore().startSystem();
        } catch (Exception e) {
            JGemsHelper.getLogger().exception(e);
            JGemsLogging.showExceptionDialog("An exception occurred inside the system. Open the logs folder for details.");
        }
    }

    public static void checkFilesDirectory() throws IOException {
        if (!Files.exists(JGems3D.getGameFilesFolder())) {
            JGems3D.getGameFilesFolder().toFile().mkdirs();
            JGemsHelper.getLogger().info("Created system folder");
        }
    }

    public static String getGameTitle() {
        return APIContainer.get().getApiGameInfo().getGemsEntry().gameTitle();
    }

    public static String getGameVersion() {
        return APIContainer.get().getApiGameInfo().getGemsEntry().gameVersion();
    }

    public static String getGameDev() {
        return APIContainer.get().getApiGameInfo().getGemsEntry().devStage().name().toLowerCase();
    }

    public static String getGameString() {
        String s1 = JGems3D.getGameTitle();
        String s2 = JGems3D.getGameVersion();
        String s3 = JGems3D.getGameDev();
        return s1 + " " + s2 + " " + s3;
    }

    public static String date() {
        LocalDateTime date = LocalDateTime.now();
        return date.toString();
    }

    public static String getGamePath() {
        return new File(JGems3D.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
    }

    public static boolean checkFileExistsInJar(JGemsPath path) {
        try (InputStream inputStream = JGems3D.class.getResourceAsStream(path.getFullPath())) {
            return inputStream != null;
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
    }

    public static InputStream loadFileFromJar(JGemsPath path) throws JGemsNotFoundException {
        InputStream inputStream = JGems3D.class.getResourceAsStream(path.getFullPath());
        if (inputStream == null) {
            throw new JGemsNotFoundException("Couldn't find: " + path);
        }
        return inputStream;
    }

    public static Path getEngineFilesFolder() {
        String appdataPath = System.getProperty("user.home");
        String folderPath = "." + JGemsCore.ENG_FILEPATH.toLowerCase();
        return Paths.get(appdataPath, folderPath);
    }

    public static Path getGameFilesFolder() {
        String appdataPath = System.getProperty("user.home");
        String folderPath = "." + JGemsCore.ENG_FILEPATH.toLowerCase() + File.separator + JGems3D.getGameTitle().toLowerCase();
        return Paths.get(appdataPath, folderPath);
    }

    public static Path getFilesFolder() {
        String appdataPath = System.getProperty("user.home");
        String folderPath = "." + JGemsCore.ENG_FILEPATH.toLowerCase();
        return Paths.get(appdataPath, folderPath);
    }

    public String I18n(String key, Object... objects) {
        if (this.getLocalisation() == null) {
            JGemsHelper.getLogger().warn("Tried to get localised name from NULL Localisation Manager");
            return key;
        }
        return String.format(this.getLocalisation().format(key), objects);
    }

    public void reloadResources() {
        APIEventsLauncher.pushEvent(new Events.ReloadResourcesEvent());
        JGems3D.get().getScreen().showGameLoadingScreen("System01");
        JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Performing settings...");
        JGems3D.get().getResourceManager().recreateTexturesInAllCaches();
        JGems3D.get().getScreen().refreshSceneResources();
        JGems3D.get().getLocalisation().setLanguage(JGemsHelper.GAME.getGameSettings().language.getCurrentLanguage());
        this.getResourceManager().loadBindlessHandlersInSSBO(JGemsResourceManager.globalShaderAssets.BindlessTextures);
        JGems3D.get().getScreen().removeLoadingScreen();
    }

    public void showMainMenu() {
        this.openUIPanel(APIContainer.get().getApiGameInfo().getAppManager().gameMainMenuPanel());
    }

    public void openUIPanel(PanelUI panelUI) {
        this.getSceneRenderer().UIPanelActionRequest(panelUI);
    }

    public void closeUIPanel() {
        this.getSceneRenderer().UIPanelActionRequest(null);
    }

    public void lockController() {
        this.getScreen().getControllerDispatcher().setLockController(true);
    }

    public void unLockController() {
        this.getScreen().getControllerDispatcher().setLockController(false);
    }

    public void pauseGameAndLockUnPausing(boolean pauseSounds) {
        this.pauseGame(pauseSounds);
        this.getCore().setLockedUnPausing(true);
    }

    public void unPauseGameAndUnLockUnPausing() {
        this.unPauseGame();
        this.getCore().setLockedUnPausing(false);
    }

    public void pauseGame(boolean pauseSounds) {
        this.getCore().pauseGame();
        if (pauseSounds) {
            this.getSoundManager().pauseAllSounds();
        }
    }

    public void unPauseGame() {
        this.getCore().unPauseGame();
        if (!this.getCore().isLockedUnPausing()) {
            this.getSoundManager().resumeAllSounds();
        }
    }

    public void loadMap(IMapLoader mapLoader) {
        this.getCore().loadMap(mapLoader);
    }

    public void destroyMap() {
        this.getCore().destroyMap();
    }

    public void destroyGame() {
        SyncManager.freeAll();
        synchronized (JGemsPhysics.locker) {
            JGems3D.get().shouldBeClosed = true;
            JGemsPhysics.locker.notifyAll();
        }
    }

    public ISceneRenderer getSceneRenderer() {
        return this.getScreen().getScene().getSceneRenderer();
    }

    public JGemsScreen getScreen() {
        return this.getCore().getScreen();
    }

    public JGemsPhysics getPhysics() {
        return this.getCore().getPhysics();
    }

    public JGemsSettings getGameSettings() {
        synchronized (this.jGemsSettings) {
            return this.jGemsSettings;
        }
    }

    public JGemsSoundManager getSoundManager() {
        synchronized (this.getCore().getSoundManager()) {
            return this.getCore().getSoundManager();
        }
    }

    public JGemsCore getCore() {
        synchronized (this) {
            return this.core;
        }
    }

    public JGemsLocalisation getLocalisation() {
        synchronized (this.jGemsLocalisation) {
            return this.jGemsLocalisation;
        }
    }

    public boolean isCurrentMapIsValid() {
        return this.getCore().getMapLoader() != null;
    }

    public JGemsResourceManager getResourceManager() {
        return this.getCore().getResourceManager();
    }

    @SuppressWarnings("all")
    public boolean isShouldBeClosed() {
        return this.shouldBeClosed;
    }

    public boolean isValidPlayer() {
        return this.getCore().getLocalPlayer() != null && this.getPlayer() != null;
    }

    public IPlayer getPlayer() {
        return this.getCore().getLocalPlayer().getEntityPlayer();
    }

    public boolean isPaused() {
        return this.getCore().engineState().isPaused();
    }

    public String toString() {
        return JGemsCore.ENG_NAME + ": " + JGemsCore.ENG_VER + " - " + JGems3D.getGameString();
    }

    public static abstract class DEF_PATHS {
        public static final String PARTICLES = "/assets/jgems/textures/particles/";
        public static final String CUBE_MAPS = "/assets/jgems/textures/cubemaps/";
        public static final String TEXTURES = "/assets/jgems/textures/";
        public static final String MODELS = "/assets/jgems/models/";
        public static final String SHADERS = "/assets/jgems/shaders/";
        public static final String SOUNDS = "/assets/jgems/sounds/";
        public static final String MAPS = "/assets/jgems/maps/";
        public static final String LANG = "/assets/jgems/lang/";
        public static final String ICONS = "/assets/jgems/icons/";
    }
}