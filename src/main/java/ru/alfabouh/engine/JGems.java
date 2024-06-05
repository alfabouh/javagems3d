package ru.alfabouh.engine;

import org.lwjgl.glfw.GLFW;
import ru.alfabouh.engine.audio.SoundManager;
import ru.alfabouh.engine.physics.entities.player.IPlayer;
import ru.alfabouh.engine.physics.world.World;
import ru.alfabouh.engine.physics.world.timer.PhysicThreadManager;
import ru.alfabouh.engine.render.scene.immediate_gui.ImmediateUI;
import ru.alfabouh.engine.render.scene.immediate_gui.panels.MainMenuPanel;
import ru.alfabouh.engine.render.scene.immediate_gui.panels.base.PanelUI;
import ru.alfabouh.engine.render.scene.world.SceneWorld;
import ru.alfabouh.engine.render.screen.Screen;
import ru.alfabouh.engine.system.EngineSystem;
import ru.alfabouh.engine.system.exception.GameException;
import ru.alfabouh.engine.system.localisation.Localisation;
import ru.alfabouh.engine.system.logger.JGemsLogging;
import ru.alfabouh.engine.system.map.loader.IMapLoader;
import ru.alfabouh.engine.system.proxy.Proxy;
import ru.alfabouh.engine.system.resources.ResourceManager;
import ru.alfabouh.engine.system.settings.GameSettings;
import ru.alfabouh.engine.system.synchronizing.SyncManager;

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
    public static boolean FIRST_LAUNCH = false;
    public static boolean DEBUG_MODE = false;
    public static final String GAME_NAME = "Reznya HD";
    public static long rngSeed;
    public static Random random;
    private static JGems mainObject;

    private final String[] startArgs;
    private final SoundManager soundManager;
    private final JGemsLogging logManager;
    private final Screen screen;
    private final PhysicThreadManager physicThreadManager;
    private final Proxy proxy;
    private final GameSettings gameSettings;
    private final Localisation localisation;

    private boolean shouldBeClosed;
    private EngineSystem engineSystem;

    private JGems(String[] startArgs) throws IOException {
        JGems.rngSeed = JGems.systemTime();
        JGems.random = new Random(JGems.rngSeed);

        this.startArgs = startArgs;
        this.logManager = new JGemsLogging();
        this.shouldBeClosed = false;
        this.physicThreadManager = new PhysicThreadManager(PhysicThreadManager.TICKS_PER_SECOND);
        this.soundManager = new SoundManager();
        this.screen = new Screen();
        this.proxy = new Proxy(this.getPhysicThreadManager().getPhysicsTimer(), this.getScreen());

        if (!Files.exists(JGems.getGameFilesFolder())) {
            Files.createDirectories(JGems.getGameFilesFolder());
            this.getLogManager().log("Created system folder");
            JGems.FIRST_LAUNCH = true;
        }

        this.gameSettings = new GameSettings();
        this.localisation = new Localisation();
    }

    public static long systemTime() {
        return System.currentTimeMillis();
    }

    public static double glfwTime() {
        return GLFW.glfwGetTime();
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

    public static void main(String[] args) throws IOException {
        JGems.mainObject = new JGems(args);
        JGems.start();
    }

    public static JGems get() {
        return JGems.mainObject;
    }

    private static void start() {
        try {
            JGems.get().getLogManager().log("Engine-On");
            JGems.get().getLogManager().log("Starting system! Date: " + JGems.date());
            JGems.get().getLogManager().log(JGems.GAME_NAME + ": " + EngineSystem.ENG_NAME + " - " + EngineSystem.ENG_VER);
            JGems.get().getLogManager().log("===============================================================");
            JGems.get().getLogManager().log("Run args: " + Arrays.toString(JGems.get().getStartArgs()));
            JGems.get().getLogManager().log("Loading settings from file...");
            JGems.get().getGameSettings().loadOptions();
            JGems.get().checkArgs(JGems.get().getStartArgs());
            JGems.get().engineSystem = new EngineSystem();
            JGems.get().getEngineSystem().startSystem();
        } catch (Exception e) {
            JGems.get().getLogManager().exception(e);
            JGemsLogging.showExceptionDialog("An exception occurred inside the system. Open the logs folder for details.");
        }
    }

    public void checkArgs(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("debug")) {
                JGems.DEBUG_MODE = true;
                JGems.get().getLogManager().log("DEBUG: ON ");
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

    public synchronized GameSettings getGameSettings() {
        return this.gameSettings;
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
            this.getLogManager().warn("Tried to get localised name from NULL Localisation Manager");
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

    public ResourceManager getResourceManager() {
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

    public JGemsLogging getLogManager() {
        return this.logManager;
    }

    public Screen getScreen() {
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
            throw new GameException("Couldn't find: " + path);
        }
        return inputStream;
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


    public String toString() {
        return EngineSystem.ENG_NAME + ": " + EngineSystem.ENG_VER + " - " + JGems.GAME_NAME;
    }
}