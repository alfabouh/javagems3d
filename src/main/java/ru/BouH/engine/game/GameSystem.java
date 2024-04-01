package ru.BouH.engine.game;

import ru.BouH.engine.game.controller.ControllerDispatcher;
import ru.BouH.engine.game.map.loader.IMapLoader;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.physics.world.timer.PhysicThreadManager;

public class GameSystem implements IEngine {
    private final ResourceManager resourceManager;
    private final EngineState engineState;
    private Thread thread;
    private IMapLoader mapLoader;
    private LocalPlayer localPlayer;

    public GameSystem() {
        this.thread = null;
        this.engineState = new EngineState();
        this.resourceManager = new ResourceManager();
        this.mapLoader = null;
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

    public void loadMap(IMapLoader mapLoader) {
        if (this.getMapLoader() != null) {
            Game.getGame().getLogManager().warn("Firstly, the current map should be destroyed!");
            return;
        }
        this.mapLoader = mapLoader;
        this.initMap();
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
        this.engineState().paused = false;
    }

    public void clean() {
        if (this.mapLoader == null) {
            return;
        }
        if (!this.engineState().isEngineIsReady()) {
            Game.getGame().getLogManager().warn("Engine thread is not ready to be cleaned!");
            return;
        }
        Game.getGame().getSoundManager().cleanAllSounds();
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
            try {
                Game.getGame().getSoundManager().createSystem();
                Game.getGame().getPhysicThreadManager().initService();
                this.getEngineState().gameResourcesLoaded = true;
                this.createGraphics();
                this.engineState().engineIsReady = true;
                Game.getGame().getScreen().startScreen();
            } finally {
                this.destroyMap();
                Game.getGame().destroyGame();
                synchronized (PhysicThreadManager.locker) {
                    PhysicThreadManager.locker.notifyAll();
                }
                Game.getGame().getPhysicThreadManager().destroy();
                Game.getGame().getSoundManager().destroy();
                Game.getGame().getLogManager().log("Engine-Off");
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
        Game.getGame().getLogManager().log("Loading rendering resources...");
        Game.getGame().getResourceManager().loadAllAssets();
        Game.getGame().getLogManager().log("Rendering resources loaded!");
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
