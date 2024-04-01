package ru.BouH.engine.game;

import org.lwjgl.glfw.GLFW;
import ru.BouH.engine.audio.SoundManager;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.game.logger.GameLogging;
import ru.BouH.engine.game.map.loader.IMapLoader;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.physics.entities.player.IPlayer;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.timer.PhysicThreadManager;
import ru.BouH.engine.render.scene.gui.base.GUI;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.screen.Screen;

import java.io.File;
import java.io.InputStream;
import java.util.Random;

public class Game {
    public static final String build = "01.04.2024";
    public static long rngSeed;
    public static Random random;
    private static Game startScreen;
    private final SoundManager soundManager;
    private final GameLogging logManager;
    private final Screen screen;
    private final PhysicThreadManager physicThreadManager;
    private final Proxy proxy;
    private GameSystem gameSystem;
    private boolean shouldBeClosed;

    private Game() {
        this.logManager = new GameLogging();
        Game.rngSeed = Game.systemTime();
        Game.random = new Random(Game.rngSeed);
        this.physicThreadManager = new PhysicThreadManager(PhysicThreadManager.TICKS_PER_SECOND);
        this.soundManager = new SoundManager();
        this.screen = new Screen();
        this.proxy = new Proxy(this.getPhysicThreadManager().getPhysicsTimer(), this.getScreen());
        this.shouldBeClosed = false;
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
        return System.nanoTime();
    }

    public static double glfwTime() {
        return GLFW.glfwGetTime();
    }

    public static Game getGame() {
        return Game.startScreen;
    }

    public static void main(String[] args) throws InterruptedException {
        Game.startScreen = new Game();
        Game.getGame().getLogManager().log("Starting game!");
        Game.getGame().gameSystem = new GameSystem();
        Game.getGame().getEngineSystem().startSystem();
    }

    public synchronized SoundManager getSoundManager() {
        return this.soundManager;
    }

    public void pauseGame() {
        this.gameSystem.pauseGame();
        this.getSoundManager().pauseAllSounds();
    }

    public void unPauseGame() {
        this.gameSystem.unPauseGame();
        this.getSoundManager().resumeAllSounds();
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

    public void destroyMap() {
        this.getEngineSystem().destroyMap();
        Game.getGame().getScreen().getScene().setRenderCamera(null);
    }

    public ResourceManager getResourceManager() {
        return this.getEngineSystem().getResourceManager();
    }

    public GameSystem getEngineSystem() {
        return this.gameSystem;
    }

    public void destroyGame() {
        Game.getGame().shouldBeClosed = true;
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