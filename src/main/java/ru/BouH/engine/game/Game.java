package ru.BouH.engine.game;

import org.lwjgl.glfw.GLFW;
import ru.BouH.engine.game.jframe.ProgressBar;
import ru.BouH.engine.game.logger.GameLogging;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.physics.entities.player.EntityPlayerSP;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.timer.PhysicThreadManager;
import ru.BouH.engine.proxy.Proxy;
import ru.BouH.engine.render.scene.world.SceneWorld;
import ru.BouH.engine.render.screen.Screen;

import java.io.File;
import java.io.InputStream;
import java.util.Random;

public class Game {
    public static final String build = "07.02.2024";
    public static long rngSeed;
    public static Random random;
    private static Game startScreen;
    private final GameLogging logManager;
    private final Screen screen;
    private final PhysicThreadManager physicThreadManager;
    private final Proxy proxy;
    private EngineStarter engineStarter;
    private boolean shouldBeClosed = false;

    private Game() {
        Game.rngSeed = Game.systemTime();
        Game.random = new Random(Game.rngSeed);
        this.logManager = new GameLogging();
        this.physicThreadManager = new PhysicThreadManager(PhysicThreadManager.TICKS_PER_SECOND);
        this.screen = new Screen();
        this.proxy = new Proxy(this.getPhysicThreadManager().getPhysicsTimer(), this.getScreen());
    }

    public static String getGamePath() {
        return new File(Game.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
    }

    public static boolean seekInJar(String path) {
        return Game.class.getResourceAsStream(path) != null;
    }

    public static InputStream loadFileJar(String path) {
        return Game.class.getResourceAsStream(path);
    }

    public static InputStream loadFileJar(String folder, String path) {
        return Game.loadFileJar("/" + folder + "/" + path);
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
        Game.getGame().engineStarter = new EngineStarter();
        Game.getGame().getEngineSystem().startSystem();
    }

    public ResourceManager getResourceManager() {
        return this.getEngineSystem().getResourceManager();
    }

    public EngineStarter getEngineSystem() {
        return this.engineStarter;
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

    public EntityPlayerSP getPlayerSP() {
        return this.getProxy().getPlayerSP();
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

    public static class EngineStarter implements IEngine {
        public static final Object logicLocker = new Object();
        private final ResourceManager resourceManager;
        private Thread thread;
        private boolean threadHasStarted;

        public EngineStarter() {
            this.thread = null;
            this.threadHasStarted = false;
            this.resourceManager = new ResourceManager();
        }

        public ResourceManager getResourceManager() {
            return this.resourceManager;
        }

        @SuppressWarnings("all")
        public void startSystem() {
            if (this.threadHasStarted) {
                Game.getGame().getLogManager().warn("Engine thread is currently running!");
                return;
            }
            this.resourceManager.init();
            this.thread = new Thread(() -> {
                try {
                    Game.getGame().shouldBeClosed = false;
                    Game.getGame().getPhysicThreadManager().initService();
                    this.preLoading();
                    this.postLoading();
                    Game.getGame().getScreen().startScreen();
                } finally {
                    synchronized (PhysicThreadManager.locker) {
                        PhysicThreadManager.locker.notifyAll();
                    }
                    Game.getGame().getPhysicThreadManager().destroy();
                    synchronized (EngineStarter.logicLocker) {
                        EngineStarter.logicLocker.notifyAll();
                    }
                }
            });
            this.thread.setName("game");
            this.thread.start();
        }

        private void preLoading() {
            ProgressBar progressBar = new ProgressBar();
            progressBar.setProgress(0);
            progressBar.showBar();
            Game.getGame().getScreen().buildScreen();
            this.preLoadingResources();
            progressBar.setProgress(50);
            Game.getGame().getScreen().initScreen();
            this.populateEnvironment();
            progressBar.setProgress(100);
            Game.getGame().getScreen().showWindow();
            progressBar.hideBar();
        }

        private void postLoading() {
            synchronized (EngineStarter.logicLocker) {
                EngineStarter.logicLocker.notifyAll();
            }
            this.threadHasStarted = true;
        }

        private void populateEnvironment() {
            World world = Game.getGame().getPhysicsWorld();
            Game.getGame().getLogManager().log("Populating environment...");
            GameEvents.populate(world);
            Game.getGame().getProxy().getLocalPlayer().addPlayerInWorlds(Game.getGame().getProxy());
            Game.getGame().getLogManager().log("Environment populated!");
        }

        private void preLoadingResources() {
            Game.getGame().getLogManager().log("Loading rendering resources...");
            Game.getGame().getResourceManager().loadAllAssets();
            Game.getGame().getLogManager().log("Rendering resources loaded!");
        }
    }
}