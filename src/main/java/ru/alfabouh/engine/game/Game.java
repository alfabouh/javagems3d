package ru.alfabouh.engine.game;

import org.lwjgl.glfw.GLFW;
import ru.alfabouh.engine.audio.SoundManager;
import ru.alfabouh.engine.game.exception.GameException;
import ru.alfabouh.engine.game.logger.GameLogging;
import ru.alfabouh.engine.game.map.loader.IMapLoader;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.game.settings.GameSettings;
import ru.alfabouh.engine.game.synchronizing.SyncManager;
import ru.alfabouh.engine.physics.entities.player.IPlayer;
import ru.alfabouh.engine.physics.world.World;
import ru.alfabouh.engine.physics.world.timer.PhysicThreadManager;
import ru.alfabouh.engine.render.scene.gui.base.GUI;
import ru.alfabouh.engine.render.scene.world.SceneWorld;
import ru.alfabouh.engine.render.screen.Screen;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Random;

public class Game {
    public static boolean DEBUG_MODE = false;
    public static final String GAME_NAME = "Reznya 3D";
    public static long rngSeed;
    public static Random random;
    private static Game startScreen;
    private final SoundManager soundManager;
    private final GameLogging logManager;
    private final Screen screen;
    private final PhysicThreadManager physicThreadManager;
    private final Proxy proxy;
    private GameSystem gameSystem;
    private final GameSettings gameSettings;
    private boolean shouldBeClosed;
    private final String[] startArgs;

    private Game(String[] startArgs) throws IOException {
        this.startArgs = startArgs;
        this.logManager = new GameLogging();
        Game.rngSeed = Game.systemTime();
        Game.random = new Random(Game.rngSeed);
        this.physicThreadManager = new PhysicThreadManager(PhysicThreadManager.TICKS_PER_SECOND);
        this.soundManager = new SoundManager();
        this.screen = new Screen();
        this.proxy = new Proxy(this.getPhysicThreadManager().getPhysicsTimer(), this.getScreen());
        this.shouldBeClosed = false;

        if (!Files.exists(Game.getGameFilesFolder())) {
            Files.createDirectories(Game.getGameFilesFolder());
            this.getLogManager().log("Created game folder");
        }

        this.gameSettings = new GameSettings();
    }

    public String[] getStartArgs() {
        return this.startArgs;
    }

    public static String date() {
        LocalDateTime date = LocalDateTime.now();
        return date.toString();
    }

    public synchronized GameSettings getGameSettings() {
        return this.gameSettings;
    }

    public static String getGamePath() {
        return new File(Game.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
    }

    public static boolean seekInJar(String path) {
        return Game.class.getResourceAsStream(path) != null;
    }

    public static InputStream loadFileJar(String path) {
        InputStream inputStream = Game.class.getResourceAsStream(path);
        if (inputStream == null) {
            throw new GameException("Couldn't find: " + path);
        }
        return inputStream;
    }

    public static InputStream loadFileJarSilently(String path) {
        return Game.class.getResourceAsStream(path);
    }

    public static InputStream loadFileJar(String folder, String path) {
        return Game.loadFileJar(folder + "/" + path);
    }

    public static long systemTime() {
        return System.currentTimeMillis();
    }

    public static double glfwTime() {
        return GLFW.glfwGetTime();
    }

    public static Game getGame() {
        return Game.startScreen;
    }

    public static void main(String[] args) throws IOException {
        Game.startScreen = new Game(args);
        Game.start();
    }

    private static void start() {
        try {
            Game.getGame().getLogManager().log("Loading settings from file...");
            Game.getGame().getGameSettings().loadOptions();
            Game.getGame().checkArgs(Game.getGame().getStartArgs());
            Game.getGame().getLogManager().log("Starting game! Date: " + Game.date());
            Game.getGame().gameSystem = new GameSystem();
            Game.getGame().getEngineSystem().startSystem();
        } catch (Exception e) {
            Game.getGame().getLogManager().exception(e);
            GameLogging.showExceptionDialog("An exception occurred inside the game. Open the logs folder for details.");
        }
    }

    public void checkArgs(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("debug")) {
                Game.DEBUG_MODE = true;
                Game.getGame().getLogManager().log("DEBUG: ON ");
            }
        }
    }

    public static Path getGameFilesFolder() {
        String appdataPath = System.getProperty("user.home");
        String folderPath = "." + GameSystem.ENG_NAME.toLowerCase() + "//" + Game.GAME_NAME.toLowerCase();
        return Paths.get(appdataPath, folderPath);
    }

    public String toString() {
        return GameSystem.ENG_NAME + ": " + GameSystem.ENG_VER + " - " + Game.GAME_NAME;
    }

    public synchronized SoundManager getSoundManager() {
        return this.soundManager;
    }

    public void pauseGameAndLockUnPausing(boolean pauseSounds) {
        this.pauseGame(pauseSounds);
        this.gameSystem.setLockedUnPausing(true);
    }

    public void unPauseGameAndUnLockUnPausing() {
        this.unPauseGame();
        this.gameSystem.setLockedUnPausing(false);
    }

    public void pauseGame(boolean pauseSounds) {
        this.gameSystem.pauseGame();
        if (pauseSounds) {
            this.getSoundManager().pauseAllSounds();
        }
    }

    public void unPauseGame() {
        this.gameSystem.unPauseGame();
        if (!this.gameSystem.isLockedUnPausing()) {
            this.getSoundManager().resumeAllSounds();
        }
    }

    public void showGui(GUI gui) {
        this.getScreen().getScene().setGui(gui);
    }

    public void removeGui() {
        this.getScreen().getScene().removeGui();
    }

    public GameSystem.EngineState getEngineState() {
        return this.getEngineSystem().getEngineState();
    }

    public synchronized boolean isCurrentMapIsValid() {
        return this.getEngineSystem().getMapLoader() != null;
    }

    public void loadMap(IMapLoader mapLoader) {
        this.getEngineSystem().loadMap(mapLoader);
    }

    public void showMainMenu() {
        Game.getGame().getScreen().getScene().getGui().showMainMenu();
    }

    public void destroyMap() {
        if (GLFW.glfwGetCurrentContext() == 0L) {
            Game.getGame().getScreen().getScene().requestDestroyMap();
            return;
        }
        Game.getGame().getScreen().getScene().setRenderCamera(null);
        Game.getGame().getScreen().getWindow().setInFocus(false);
        this.getEngineSystem().destroyMap();
        Game.getGame().showMainMenu();
    }

    public ResourceManager getResourceManager() {
        return this.getEngineSystem().getResourceManager();
    }

    public synchronized GameSystem getEngineSystem() {
        return this.gameSystem;
    }

    public synchronized void destroyGame() {
        Game.getGame().shouldBeClosed = true;
        SyncManager.freeAll();
        synchronized (PhysicThreadManager.locker) {
            PhysicThreadManager.locker.notifyAll();
        }
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

    public IPlayer getPlayerSP() {
        return this.getEngineSystem().getLocalPlayer().getEntityPlayerSP();
    }

    public GameLogging getLogManager() {
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
}