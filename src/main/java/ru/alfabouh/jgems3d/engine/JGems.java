package ru.alfabouh.jgems3d.engine;

import org.lwjgl.glfw.GLFW;
import ru.alfabouh.jgems3d.engine.audio.SoundManager;
import ru.alfabouh.jgems3d.engine.physics.objects.entities.player.IPlayer;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.physics.world.timer.PhysicThreadManager;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.immediate_gui.ImmediateUI;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.immediate_gui.panels.MainMenuPanel;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.immediate_gui.panels.base.PanelUI;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.world.SceneWorld;
import ru.alfabouh.jgems3d.engine.graphics.opengl.screen.JGemsScreen;
import ru.alfabouh.jgems3d.engine.system.core.EngineSystem;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;
import ru.alfabouh.jgems3d.engine.system.resources.localisation.Localisation;
import ru.alfabouh.jgems3d.engine.system.map.legacy.loader.IMapLoader;
import ru.alfabouh.jgems3d.engine.system.proxy.Proxy;
import ru.alfabouh.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.alfabouh.jgems3d.engine.system.settings.JGemsSettings;
import ru.alfabouh.jgems3d.engine.system.synchronizing.SyncManager;
import ru.alfabouh.jgems3d.logger.SystemLogging;
import ru.alfabouh.jgems3d.logger.managers.JGemsLogging;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;

public class JGems {
    public static final String GAME_NAME = "Reznya HD";
    public static boolean DEBUG_MODE = false;
    public static long rngSeed;
    public static Random random;
    private static JGems mainObject;

    private final String[] startArgs;
    private final SoundManager soundManager;
    private final JGemsScreen screen;
    private final PhysicThreadManager physicThreadManager;
    private final Proxy proxy;
    private final JGemsSettings JGemsSettings;
    private final Localisation localisation;

    private boolean shouldBeClosed;
    private EngineSystem engineSystem;

    private JGems(String[] startArgs) throws IOException {
        SystemLogging.get().setCurrentLogging(SystemLogging.jGemsLogging);

        JGems.rngSeed = JGems.systemTime();
        JGems.random = new Random(JGems.rngSeed);

        this.startArgs = startArgs;
        this.shouldBeClosed = false;
        this.physicThreadManager = new PhysicThreadManager(PhysicThreadManager.TICKS_PER_SECOND);
        this.soundManager = new SoundManager();
        this.screen = new JGemsScreen();
        this.proxy = new Proxy(this.getPhysicThreadManager().getPhysicsTimer(), this.getScreen());

        JGems.checkFilesDirectory();

        this.JGemsSettings = new JGemsSettings(new File(JGems.getGameFilesFolder().toFile(), "settings.txt"));
        this.localisation = new Localisation();
    }

    public static long systemTime() {
        return System.currentTimeMillis();
    }

    public static double glfwTime() {
        return GLFW.glfwGetTime();
    }

    public static void main(String[] args) throws IOException {
        JGems.mainObject = new JGems(args);
        JGems.start();
    }

    public static JGems get() {
        return JGems.mainObject;
    }

    private static void start() {
        try {
            SystemLogging.get().getLogManager().log("Engine-On");
            SystemLogging.get().getLogManager().log("Starting system! Date: " + JGems.date());
            SystemLogging.get().getLogManager().log(JGems.GAME_NAME + ": " + EngineSystem.ENG_NAME + " - " + EngineSystem.ENG_VER);
            SystemLogging.get().getLogManager().log("===============================================================");
            SystemLogging.get().getLogManager().log("Run args: " + Arrays.toString(JGems.get().getStartArgs()));
            SystemLogging.get().getLogManager().log("Loading settings from file...");
            JGems.get().getGameSettings().loadOptions();
            JGems.get().checkArgs(JGems.get().getStartArgs());
            JGems.get().engineSystem = new EngineSystem();
            JGems.get().getEngineSystem().startSystem();
        } catch (Exception e) {
            SystemLogging.get().getLogManager().exception(e);
            JGemsLogging.showExceptionDialog("An exception occurred inside the system. Open the logs folder for details.");
        }
    }

    public static void checkFilesDirectory() throws IOException {
        if (!Files.exists(JGems.getGameFilesFolder())) {
            Files.createDirectories(JGems.getGameFilesFolder());
            SystemLogging.get().getLogManager().log("Created system folder");
        }
    }

    public static String date() {
        LocalDateTime date = LocalDateTime.now();
        return date.toString();
    }

    public static String getGamePath() {
        return new File(JGems.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
    }

    public static boolean seekInJar(String path) {
        return JGems.class.getResourceAsStream(path) != null;
    }

    public static InputStream loadFileJar(String path) {
        InputStream inputStream = JGems.class.getResourceAsStream(path);
        if (inputStream == null) {
            throw new JGemsException("Couldn't find: " + path);
        }
        return inputStream;
    }

    public static String normalizeURL(String url) {
        return url.replace("\\", "/");
    }

    public static InputStream loadFileJarSilently(String path) {
        return JGems.class.getResourceAsStream(path);
    }

    public static InputStream loadFileJar(String folder, String path) {
        return JGems.loadFileJar(folder + "/" + path);
    }

    public static Path getGameFilesFolder() {
        String appdataPath = System.getProperty("user.home");
        String folderPath = "." + EngineSystem.ENG_FILEPATH.toLowerCase() + "//" + JGems.GAME_NAME.toLowerCase();
        return Paths.get(appdataPath, folderPath);
    }

    public static Path getFilesFolder() {
        String appdataPath = System.getProperty("user.home");
        String folderPath = "." + EngineSystem.ENG_FILEPATH.toLowerCase();
        return Paths.get(appdataPath, folderPath);
    }

    public void showMainMenu() {
        this.setUIPanel(new MainMenuPanel(null));
    }

    public void setUIPanel(PanelUI panelUI) {
        this.getUI().setPanel(panelUI);
    }

    public void removeUIPanel() {
        this.getUI().removePanel();
    }

    public void checkArgs(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("debug")) {
                JGems.DEBUG_MODE = true;
                SystemLogging.get().getLogManager().log("DEBUG: ON ");
            }
        }
    }

    public void pauseGameAndLockUnPausing(boolean pauseSounds) {
        this.pauseGame(pauseSounds);
        this.engineSystem.setLockedUnPausing(true);
    }

    public void unPauseGameAndUnLockUnPausing() {
        this.unPauseGame();
        this.engineSystem.setLockedUnPausing(false);
    }

    public void pauseGame(boolean pauseSounds) {
        this.engineSystem.pauseGame();
        if (pauseSounds) {
            this.getSoundManager().pauseAllSounds();
        }
    }

    public void unPauseGame() {
        this.engineSystem.unPauseGame();
        if (!this.engineSystem.isLockedUnPausing()) {
            this.getSoundManager().resumeAllSounds();
        }
    }

    public void loadMap(IMapLoader mapLoader) {
        this.getEngineSystem().loadMap(mapLoader);
    }

    public void destroyMap() {
        if (GLFW.glfwGetCurrentContext() == 0L) {
            JGems.get().getScreen().getScene().requestDestroyMap();
            return;
        }
        JGems.get().getScreen().getScene().setRenderCamera(null);
        JGems.get().getScreen().getWindow().setInFocus(false);
        this.getEngineSystem().destroyMap();
        JGems.get().showMainMenu();
    }

    public synchronized JGemsSettings getGameSettings() {
        return this.JGemsSettings;
    }

    public synchronized void destroyGame() {
        JGems.get().shouldBeClosed = true;
        SyncManager.freeAll();
        synchronized (PhysicThreadManager.locker) {
            PhysicThreadManager.locker.notifyAll();
        }
    }

    public synchronized SoundManager getSoundManager() {
        return this.soundManager;
    }

    public synchronized EngineSystem getEngineSystem() {
        return this.engineSystem;
    }

    public synchronized boolean isCurrentMapIsValid() {
        return this.getEngineSystem().getMapLoader() != null;
    }

    public String I18n(String key, Object... objects) {
        if (this.getLocalisation() == null) {
            SystemLogging.get().getLogManager().warn("Tried to get localised name from NULL Localisation Manager");
            return key;
        }
        return String.format(this.getLocalisation().format(key), objects);
    }

    public ImmediateUI getUI() {
        return this.getScreen().getScene().UI();
    }

    public EngineSystem.EngineState getEngineState() {
        return this.getEngineSystem().getEngineState();
    }

    public JGemsResourceManager getResourceManager() {
        return this.getEngineSystem().getResourceManager();
    }

    @SuppressWarnings("all")
    public boolean isShouldBeClosed() {
        return this.shouldBeClosed;
    }

    public World getPhysicsWorld() {
        return this.getPhysicThreadManager().getPhysicsTimer().getWorld();
    }

    public SceneWorld getSceneWorld() {
        return this.getScreen().getRenderWorld();
    }

    public boolean isValidPlayer() {
        return this.getEngineSystem().getLocalPlayer() != null && this.getPlayerSP() != null;
    }

    public String[] getStartArgs() {
        return this.startArgs;
    }

    public Localisation getLocalisation() {
        return this.localisation;
    }

    public IPlayer getPlayerSP() {
        return this.getEngineSystem().getLocalPlayer().getEntityPlayerSP();
    }

    public JGemsScreen getScreen() {
        return this.screen;
    }

    public PhysicThreadManager getPhysicThreadManager() {
        return this.physicThreadManager;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public boolean isPaused() {
        return this.getEngineState().isPaused();
    }

    public String toString() {
        return EngineSystem.ENG_NAME + ": " + EngineSystem.ENG_VER + " - " + JGems.GAME_NAME;
    }
}