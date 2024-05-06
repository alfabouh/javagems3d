package ru.alfabouh.engine.game;

import org.joml.Vector3d;
import org.joml.Vector4d;
import ru.alfabouh.engine.game.controller.ControllerDispatcher;
import ru.alfabouh.engine.game.logger.GameLogging;
import ru.alfabouh.engine.game.map.loader.IMapLoader;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.game.synchronizing.SyncManger;
import ru.alfabouh.engine.physics.world.World;
import ru.alfabouh.engine.physics.world.object.WorldItem;
import ru.alfabouh.engine.physics.world.timer.PhysicThreadManager;
import ru.alfabouh.engine.render.environment.Environment;
import ru.alfabouh.engine.render.scene.gui.InGameGUI;

public class GameSystem implements IEngine {
    public static final String ENG_NAME = "JVertex3d";
    public static final String ENG_VER = "0.11a";

    private final ResourceManager resourceManager;
    private final EngineState engineState;
    private Thread thread;
    private IMapLoader mapLoader;
    private LocalPlayer localPlayer;
    private boolean lockedUnPausing;

    public GameSystem() {
        this.thread = null;
        this.engineState = new EngineState();
        this.resourceManager = new ResourceManager();
        this.mapLoader = null;
        this.lockedUnPausing = false;
    }

    public EngineState getEngineState() {
        return this.engineState;
    }

    public String currentMapName() {
        return this.getMapLoader().levelInfo().getLevelName();
    }

    public IMapLoader getMapLoader() {
        return this.mapLoader;
    }

    public boolean isLockedUnPausing() {
        return this.lockedUnPausing;
    }

    public void setLockedUnPausing(boolean lockedUnPausing) {
        this.lockedUnPausing = lockedUnPausing;
    }

    public void loadMap(IMapLoader mapLoader) {
        if (this.getMapLoader() != null) {
            Game.getGame().getLogManager().warn("Firstly, the current map should be destroyed!");
            return;
        }
        this.mapLoader = mapLoader;
        this.initMap();
    }

    public Thread getThread() {
        return this.thread;
    }

    private void initMap() {
        if (!this.engineState().isEngineIsReady()) {
            Game.getGame().getLogManager().warn("Engine thread is not ready to load map!");
            return;
        }
        this.startWorlds();
        Game.getGame().getLogManager().log("Loading map " + this.currentMapName());
        World world = Game.getGame().getPhysicThreadManager().getPhysicsTimer().getWorld();
        this.localPlayer = new LocalPlayer(world);
        this.getLocalPlayer().addPlayerInWorlds(this.getMapLoader().levelInfo().getPlayerStartPos());

        Game.getGame().getLogManager().log("Created player");
        Game.getGame().getLogManager().log(this.currentMapName() + ": Adding brushes!");
        this.getMapLoader().addBrushes(world);
        Game.getGame().getLogManager().log(this.currentMapName() + ": Adding liquids!");
        this.getMapLoader().addLiquids(world);
        Game.getGame().getLogManager().log(this.currentMapName() + ": Adding triggers!");
        this.getMapLoader().addTriggers(world);
        Game.getGame().getLogManager().log(this.currentMapName() + ": Adding entities!");
        this.getMapLoader().addEntities(world);
        Game.getGame().getLogManager().log(this.currentMapName() + ": Adding sounds!");
        this.getMapLoader().addSounds(world);
        Game.getGame().getLogManager().log(this.currentMapName() + ": Reading Nav Mesh!");
        this.getMapLoader().readNavMesh(world);

        Game.getGame().getScreen().getControllerDispatcher().attachControllerTo(ControllerDispatcher.mouseKeyboardController, this.getLocalPlayer().getEntityPlayerSP());
        Game.getGame().getScreen().getScene().enableAttachedCamera((WorldItem) this.getLocalPlayer().getEntityPlayerSP());
        Game.getGame().getScreen().getWindow().setInFocus(true);
        Game.getGame().showGui(new InGameGUI());

        Environment environment = Game.getGame().getSceneWorld().getEnvironment();
        Vector4d fog1 = this.mapLoader.levelInfo().getFog();
        if (this.mapLoader.levelInfo().getFog() != null) {
            environment.getWorldFog().setColor(new Vector3d(fog1.x, fog1.y, fog1.z));
            environment.getWorldFog().setDensity((float) fog1.w);
            environment.getSky().setCoveredByFog(this.mapLoader.levelInfo().isSkyCoveredByFog());
        }
        environment.getSky().setSunColors(this.mapLoader.levelInfo().getSunColor());
        environment.getSky().setSunBrightness(this.mapLoader.levelInfo().getSunBrightness());

        this.unPauseGame();
    }

    public LocalPlayer getLocalPlayer() {
        return this.localPlayer;
    }

    public void destroyMap() {
        this.pauseGame();
        this.clean();
        this.mapLoader = null;
    }

    public void pauseGame() {
        this.engineState().paused = true;
    }

    public void unPauseGame() {
        if (!this.isLockedUnPausing()) {
            this.engineState().paused = false;
        }
    }

    public void clean() {
        if (this.mapLoader == null) {
            return;
        }
        if (!this.engineState().isEngineIsReady()) {
            Game.getGame().getLogManager().warn("Engine thread is not ready to be cleaned!");
            return;
        }
        Game.getGame().getSoundManager().stopAllSounds();
        Game.getGame().getLogManager().warn("Cleaning worlds!");
        this.endWorlds();
        this.localPlayer = null;
    }

    private void startWorlds() {
        Game.getGame().getPhysicThreadManager().getPhysicsTimer().getWorld().onWorldStart();
        Game.getGame().getScreen().getScene().getSceneWorld().onWorldStart();
    }

    private void endWorlds() {
        Game.getGame().getPhysicThreadManager().getPhysicsTimer().getWorld().onWorldEnd();
        Game.getGame().getScreen().getScene().getSceneWorld().onWorldEnd();
    }

    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    @SuppressWarnings("all")
    public void startSystem() {
        Game.getGame().getLogManager().log("Engine-On");
        if (this.engineState().isEngineIsReady()) {
            Game.getGame().getLogManager().warn("Engine thread is currently running!");
            return;
        }
        this.getResourceManager().init();
        this.thread = new Thread(() -> {
            boolean badExit = true;
            try {
                Game.getGame().getSoundManager().createSystem();
                Game.getGame().getPhysicThreadManager().initService();
                this.getEngineState().gameResourcesLoaded = true;
                this.createGraphics();
                this.engineState().engineIsReady = true;
                Game.getGame().getScreen().startScreen();
                badExit = false;
            } catch (Exception e) {
                Game.getGame().getLogManager().error(e);
                GameLogging.showExceptionDialog("An exception occurred inside the game. Open the logs folder for details.");
                badExit = false;
            } finally {
                try {
                    this.destroyMap();
                    Game.getGame().destroyGame();
                    Game.getGame().getSoundManager().stopAllSounds();
                    Game.getGame().getResourceManager().destroy();
                    Game.getGame().getSoundManager().destroy();
                    Game.getGame().getPhysicThreadManager().getPhysicsTimer().cleanResources();
                    Game.getGame().getLogManager().log("Engine-Off");
                    if (badExit) {
                        GameLogging.showExceptionDialog("The program closed in a strange way. Open the logs folder to find out the details.");
                    }
                } catch (Exception e) {
                    Game.getGame().getLogManager().error(e);
                    GameLogging.showExceptionDialog("An exception occurred on game closing. Open the logs folder for details.");
                }
            }
        });
        this.thread.setName("game");
        this.thread.start();
    }

    @Override
    public EngineState engineState() {
        return this.engineState;
    }

    private void createGraphics() {
        Game.getGame().getScreen().buildScreen();
        Game.getGame().getScreen().initScreen();
        Game.getGame().getScreen().showWindow();
        Game.getGame().getResourceManager().loadAllAssets();
    }

    public static class EngineState {
        private boolean gameResourcesLoaded;
        private boolean engineIsReady;
        private boolean paused;

        public EngineState() {
            this.gameResourcesLoaded = false;
            this.paused = true;
            this.engineIsReady = false;
        }

        public boolean isEngineIsReady() {
            return this.engineIsReady;
        }

        public boolean isGameResourcesLoaded() {
            return this.gameResourcesLoaded;
        }

        public boolean isPaused() {
            return this.paused;
        }
    }
}
