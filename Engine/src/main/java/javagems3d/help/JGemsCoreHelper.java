package javagems3d.help;

import javagems3d.JGems3D;
import javagems3d.audio.JGemsSoundManager;
import javagems3d.graphics.screen.JGemsScreen;
import javagems3d.graphics.screen.timer.JGemsTimer;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.mapping.IGameMap;
import javagems3d.mapping.processing.base.IMapProcessor;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.settings.JGemsSettings;

public abstract class JGemsCoreHelper {
    public static JGems3D getMainObject() {
        return JGems3D.get();
    }

    public static JGemsSettings getGameSettings() {
        return JGems3D.get().getGameSettings();
    }

    public static JGemsTimer createTimer() {
        return getScreen().getTimerPool().createTimer();
    }

    public static IPlayer getCurrentPlayer() {
        return getMainObject().getCurrentGameMapPlayer();
    }

    public static JGemsScreen getScreen() {
        return getMainObject().getScreen();
    }

    public static SceneWorld getSceneWorld() {
        return (SceneWorld) getMainObject().getCore().getScreen().getSceneWorld();
    }

    public static PhysicsWorld getPhysicsWorld() {
        return getMainObject().getPhysics().getPhysicsProcessor().getPhysicsWorld();
    }

    public static JGemsSoundManager getSoundManager() {
        return getMainObject().getSoundManager();
    }

    public static void killItems() {
        getPhysicsWorld().killItems();
    }

    public static void zeroRenderTick() {
        JGems3D.get().getScreen().zeroRenderTick();
    }

    public static void lockController() {
        JGems3D.get().lockController();
    }

    public static void unLockController() {
        JGems3D.get().unLockController();
    }

    public static void pauseGameAndLockUnPausing(boolean pauseSounds) {
        JGems3D.get().pauseGameAndLockUnPausing(pauseSounds);
    }

    public static IGameMap getCurrentMap() {
        return JGems3D.get().getCore().getCurrentGameMap();
    }

    public static void unPauseGameAndUnLockUnPausing() {
        JGems3D.get().unPauseGameAndUnLockUnPausing();
    }

    public static void pauseGame(boolean pauseSounds) {
        JGems3D.get().pauseGame(pauseSounds);
    }

    public static void unPauseGame() {
        JGems3D.get().unPauseGame();
    }

    public static void loadMap(IMapProcessor mapLoader) {
        JGems3D.get().loadMap(mapLoader);
    }

    public static void destroyMap() {
        JGems3D.get().exitMap();
    }
}
