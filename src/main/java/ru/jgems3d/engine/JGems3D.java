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

package ru.jgems3d.engine;

import org.lwjgl.glfw.GLFW;
import ru.jgems3d.engine.api_bridge.APIContainer;
import ru.jgems3d.engine.api_bridge.APILauncher;
import ru.jgems3d.engine.api_bridge.events.APIEventsLauncher;
import ru.jgems3d.engine.audio.SoundManager;
import ru.jgems3d.engine.physics.entities.player.Player;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.physics.world.thread.PhysicsThread;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.ImmediateUI;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.base.PanelUI;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.graphics.opengl.screen.JGemsScreen;
import ru.jgems3d.engine.system.core.EngineSystem;
import ru.jgems3d.engine.system.service.exceptions.JGemsIOException;
import ru.jgems3d.engine.system.service.exceptions.JGemsNotFoundException;
import ru.jgems3d.engine.system.service.exceptions.JGemsRuntimeException;
import ru.jgems3d.engine.system.service.path.JGemsPath;
import ru.jgems3d.engine.system.resources.localisation.Localisation;
import ru.jgems3d.engine.system.map.loaders.IMapLoader;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.jgems3d.engine.system.settings.JGemsSettings;
import ru.jgems3d.engine.system.service.synchronizing.SyncManager;
import ru.jgems3d.engine_api.events.bus.Events;
import ru.jgems3d.logger.SystemLogging;
import ru.jgems3d.logger.managers.JGemsLogging;
import ru.jgems3d.toolbox.ToolBox;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;

public final class JGems3D {
    public static boolean DEBUG_MODE = false;
    public static long rngSeed;
    public static Random random;
    private static JGems3D mainObject;

    private final String[] startArgs;
    private final SoundManager soundManager;
    private final JGemsScreen screen;
    private final PhysicsThread physicsThread;
    private final JGemsSettings jGemsSettings;
    private final Localisation localisation;

    private boolean shouldBeClosed;
    private EngineSystem engineSystem;

    private JGems3D(String[] startArgs) {
        SystemLogging.get().setCurrentLogging(SystemLogging.jGemsLogging);

        APILauncher.get().launchGameAPI();
        APILauncher.get().launchToolBoxAPI();
        APIContainer.get().getApiTBoxInfo().getAppInstance().initEntitiesUserData(APIContainer.get().getTBoxEntitiesUserData());
        APILauncher.get().disposeReflection();

        JGems3D.rngSeed = JGems3D.systemTime();
        JGems3D.random = new Random(JGems3D.rngSeed);

        this.startArgs = startArgs;
        this.shouldBeClosed = false;
        this.physicsThread = new PhysicsThread(PhysicsThread.TICKS_PER_SECOND);
        this.soundManager = new SoundManager();
        this.screen = new JGemsScreen();

        try {
            JGems3D.checkFilesDirectory();
        } catch (IOException e) {
            throw new JGemsRuntimeException(e);
        }

        this.jGemsSettings = new JGemsSettings(new File(JGems3D.getGameFilesFolder().toFile(), "jgems_settings.txt"));
        this.localisation = new Localisation();
    }

    public static long systemTime() {
        return System.currentTimeMillis();
    }

    public static double glfwTime() {
        return GLFW.glfwGetTime();
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("toolbox")) {
            ToolBox.main(args);
            return;
        }
        JGems3D.mainObject = new JGems3D(args);
        JGems3D.start();
    }

    public static JGems3D get() {
        return JGems3D.mainObject;
    }

    private static void start() {
        try {
            JGemsHelper.getLogger().log("Engine-On");
            JGemsHelper.getLogger().log("Starting system! Date: " + JGems3D.date());
            JGemsHelper.getLogger().log(JGems3D.getGameString() + ": " + EngineSystem.ENG_NAME + " - " + EngineSystem.ENG_VER);
            JGemsHelper.getLogger().log("===============================================================");
            JGemsHelper.getLogger().log("Run args: " + Arrays.toString(JGems3D.get().getStartArgs()));
            JGemsHelper.getLogger().log("Loading settings from path...");
            JGems3D.get().getGameSettings().loadOptions();
            JGems3D.get().checkArgs(JGems3D.get().getStartArgs());
            JGems3D.get().engineSystem = new EngineSystem();
            JGems3D.get().getEngineSystem().startSystem();
        } catch (Exception e) {
            JGemsHelper.getLogger().exception(e);
            JGemsLogging.showExceptionDialog("An exception occurred inside the system. Open the logs folder for details.");
        }
    }

    public static void checkFilesDirectory() throws IOException {
        if (!Files.exists(JGems3D.getGameFilesFolder())) {
            JGems3D.getGameFilesFolder().toFile().mkdirs();
            JGemsHelper.getLogger().log("Created system folder");
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

    public static boolean checkIfSys64B() {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        return runtimeBean.getVmName().toLowerCase().contains("64");
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
        String folderPath = "." + EngineSystem.ENG_FILEPATH.toLowerCase();
        return java.nio.file.Paths.get(appdataPath, folderPath);
    }

    public static Path getGameFilesFolder() {
        String appdataPath = System.getProperty("user.home");
        String folderPath = "." + EngineSystem.ENG_FILEPATH.toLowerCase() + "//" + JGems3D.getGameTitle().toLowerCase();
        return java.nio.file.Paths.get(appdataPath, folderPath);
    }

    public static Path getFilesFolder() {
        String appdataPath = System.getProperty("user.home");
        String folderPath = "." + EngineSystem.ENG_FILEPATH.toLowerCase();
        return java.nio.file.Paths.get(appdataPath, folderPath);
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
        JGems3D.get().getScreen().reloadSceneAndShadowsFrameBufferObjects();
        JGems3D.get().getScreen().checkScreenMode();
        JGems3D.get().getScreen().checkVSync();
        JGems3D.get().getLocalisation().setLanguage(JGemsHelper.GAME.getGameSettings().language.getCurrentLanguage());
        JGems3D.get().getScreen().removeLoadingScreen();
    }

    public void showMainMenu() {
        this.openUIPanel(APIContainer.get().getApiGameInfo().getAppManager().gameMainMenuPanel());
    }

    public void openUIPanel(PanelUI panelUI) {
        this.getUI().setPanel(panelUI);
    }

    public void removeUIPanel() {
        this.getUI().removePanel();
    }

    public void checkArgs(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("debug")) {
                JGems3D.DEBUG_MODE = true;
                JGemsHelper.getLogger().log("DEBUG: ON ");
            }
        }
    }

    public void pauseGameAndLockUnPausing(boolean pauseSounds) {
        this.pauseGame(pauseSounds);
        this.getEngineSystem().setLockedUnPausing(true);
    }

    public void unPauseGameAndUnLockUnPausing() {
        this.unPauseGame();
        this.getEngineSystem().setLockedUnPausing(false);
    }

    public void pauseGame(boolean pauseSounds) {
        this.getEngineSystem().pauseGame();
        if (pauseSounds) {
            this.getSoundManager().pauseAllSounds();
        }
    }

    public void unPauseGame() {
        this.getEngineSystem().unPauseGame();
        if (!this.getEngineSystem().isLockedUnPausing()) {
            this.getSoundManager().resumeAllSounds();
        }
    }

    public void loadMap(IMapLoader mapLoader) {
        this.getEngineSystem().loadMap(mapLoader);
    }

    public void destroyMap() {
        this.getEngineSystem().destroyMap();
    }

    public void destroyGame() {
        SyncManager.freeAll();
        synchronized (PhysicsThread.locker) {
            JGems3D.get().shouldBeClosed = true;
            PhysicsThread.locker.notifyAll();
        }
    }

    public JGemsScreen getScreen() {
        return this.screen;
    }

    public PhysicsThread getPhysicThreadManager() {
        return this.physicsThread;
    }

    public JGemsSettings getGameSettings() {
        synchronized (this.jGemsSettings) {
            return this.jGemsSettings;
        }
    }

    public SoundManager getSoundManager() {
        synchronized (this.soundManager) {
            return this.soundManager;
        }
    }

    public EngineSystem getEngineSystem() {
        synchronized (this) {
            return this.engineSystem;
        }
    }

    public Localisation getLocalisation() {
        synchronized (this.localisation) {
            return this.localisation;
        }
    }

    public ImmediateUI getUI() {
        return this.getScreen().getScene().UI();
    }

    public boolean isCurrentMapIsValid() {
        return this.getEngineSystem().getMapLoader() != null;
    }

    public JGemsResourceManager getResourceManager() {
        return this.getEngineSystem().getResourceManager();
    }

    @SuppressWarnings("all")
    public boolean isShouldBeClosed() {
        return this.shouldBeClosed;
    }

    public PhysicsWorld getPhysicsWorld() {
        return this.getPhysicThreadManager().getPhysicsTimer().getWorld();
    }

    public SceneWorld getSceneWorld() {
        return this.getScreen().getSceneWorld();
    }

    public boolean isValidPlayer() {
        return this.getEngineSystem().getLocalPlayer() != null && this.getPlayer() != null;
    }

    public String[] getStartArgs() {
        return this.startArgs;
    }

    public Player getPlayer() {
        return this.getEngineSystem().getLocalPlayer().getEntityPlayer();
    }

    public boolean isPaused() {
        return this.getEngineSystem().engineState().isPaused();
    }

    public String toString() {
        return EngineSystem.ENG_NAME + ": " + EngineSystem.ENG_VER + " - " + JGems3D.getGameString();
    }

    public static class Paths {
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