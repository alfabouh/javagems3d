package javagems3d;

import api.system.JGemsAPIData;
import javagems3d.graphics.rendering.scene.ISceneRenderer;
import javagems3d.graphics.rendering.ui.jgems_imgui.IJGemsUIImp;
import javagems3d.help.JGemsCoreHelper;
import javagems3d.system.service.os.OS;
import javagems3d.system.service.os.SysOSValidation;
import logger.Log;
import logger.managers.LoggingManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import api.system.JGemsAPI;
import api.events.EventLauncher;
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
import api.events.EventBus;
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
            SystemLogging.get().setCurrentLogging(new JGemsLogging("JGemsLogger"));
            JGemsAPI.get().launchAPI();
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

    public static long systemTime() {
        return System.currentTimeMillis();
    }

    public static double glfwTime() {
        return GLFW.glfwGetTime();
    }

    public static JGemsAPIData getAPIAppData() {
        return JGemsAPI.APIAppData();
    }

    @SuppressWarnings("all")
    public static void launch() {
        if (JGems3D.mainObject != null) {
            throw new JGemsRuntimeException("Couldn't launch JavaGems more than 1 times");
        }
        try {
            JGems3D.mainObject = new JGems3D();
        } catch (JGemsRuntimeException e) {
            LoggingManager.showExceptionDialog("Where was an error, while creating an application instance!\n\n" + e.getMessage());
            Log.get().exception(e);
            return;
        }
        JGems3D.start();
    }

    public static JGems3D get() {
        return JGems3D.mainObject;
    }

    private static void start() {
        try {
            Log.get().debug("BEGIN");
            Log.get().info("Starting system! Date: " + JGems3D.date());
            Log.get().info(JGems3D.get().toString());
            Log.get().info("===============================================================");
            Log.get().info("Loading settings from path...");
            if (JGems3D.get().getGameSettings().makeSettingDirs()) {
                JGems3D.FIRST_LAUNCH = true;
            } else {
                JGems3D.get().getGameSettings().loadOptions();
            }
            JGems3D.get().core = new JGemsCore();
            JGems3D.get().getCore().startSystem();
        } catch (Exception e) {
            Log.get().exception(e);
            JGemsLogging.showExceptionDialog("An exception occurred inside the system. Open the logs folder for details.");
        }
    }

    public static void checkFilesDirectory() throws IOException {
        if (!Files.exists(JGems3D.getGameFilesFolder())) {
            JGems3D.getGameFilesFolder().toFile().mkdirs();
            Log.get().debug("Created system folder");
        }
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
        String folderPath = "." + JGemsCore.ENG_FILEPATH.toLowerCase() + File.separator + JGems3D.getAPIAppData().getId();
        return Paths.get(appdataPath, folderPath);
    }

    public static Path getFilesFolder() {
        String appdataPath = System.getProperty("user.home");
        String folderPath = "." + JGemsCore.ENG_FILEPATH.toLowerCase();
        return Paths.get(appdataPath, folderPath);
    }

    public String I18n(String key, Object... objects) {
        if (this.getLocalisation() == null) {
            Log.get().warn("Tried to get localised name from NULL Localisation Manager");
            return key;
        }
        return String.format(this.getLocalisation().format(key), objects);
    }

    public void reloadResources() {
        EventLauncher.pushEvent(new EventBus.ReloadResourcesEvent());
        JGems3D.get().getScreen().showGameLoadingScreen("System01");
        JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Performing settings...");
        JGems3D.get().getResourceManager().recreateTexturesInAllCaches();
        JGems3D.get().getScreen().refreshSceneResources();
        JGems3D.get().getLocalisation().setLanguage(JGemsCoreHelper.getGameSettings().language.getCurrentLanguage());
        this.getResourceManager().loadBindlessHandlersInSSBO(JGemsResourceManager.globalShaderAssets.BindlessTexturesData);
        JGems3D.get().getScreen().removeLoadingScreen();
    }

    public void changeIcon(@Nullable JGemsPath icon) {
        this.getScreen().setIcon(icon);
    }

    public void changeTitle(@NotNull String title) {
        this.getScreen().setTitle(title);
    }

    public void showMainMenu() {
        this.openUIPanel(JGems3D.getAPIAppData().getMainMenuPanel());
    }

    public void openUIPanel(PanelUI panelUI) {
        ((IJGemsUIImp) this.getSceneRenderer()).openUIPanel(panelUI);
    }

    public void closeUIPanel() {
        ((IJGemsUIImp) this.getSceneRenderer()).openUIPanel(null);
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

    public void entryMap(IMapLoader mapLoader) {
        this.getCore().entryMap(mapLoader);
    }

    public void exitMap() {
        this.getCore().exitMap();
    }

    public static void close(@Nullable Exception exception) {
        Log.get().warn("Exit...");
        synchronized (JGems3D.get()) {
            JGems3D.get().shouldBeClosed = true;
            JGems3D.get().getCore().addExceptionInTrace(exception);
        }
    }

    public static void freeSync() {
        SyncManager.freeAll();
        synchronized (JGemsPhysics.locker) {
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
        return this.getCore().getLocalPlayer() == null ? null : this.getCore().getLocalPlayer().getEntityPlayer();
    }

    public boolean isPaused() {
        return this.getCore().engineState().isPaused();
    }

    public String toString() {
        return JGemsCore.ENG_NAME + ": " + JGemsCore.ENG_VER + " | appId = " + JGems3D.getAPIAppData().getId();
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