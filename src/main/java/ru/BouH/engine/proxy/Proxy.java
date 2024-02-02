package ru.BouH.engine.proxy;

import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.physics.entities.PhysEntity;
import ru.BouH.engine.physics.entities.player.EntityPlayerSP;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.physics.world.timer.PhysicsTimer;
import ru.BouH.engine.render.environment.light.Light;
import ru.BouH.engine.render.environment.light.PointLight;
import ru.BouH.engine.render.scene.preforms.RenderObjectData;
import ru.BouH.engine.render.screen.Screen;

public class Proxy {
    private final PhysicsTimer physicsTimer;
    private final Screen screen;
    private LocalPlayer localPlayer;

    public Proxy(PhysicsTimer gameWorldTimer, Screen screen) {
        this.physicsTimer = gameWorldTimer;
        this.screen = screen;
    }

    public void createLocalPlayer() {
        this.localPlayer = new LocalPlayer(this.physicsTimer.getWorld(), new Vector3d(0.0d, 1.0d, 0.0d));
    }

    public void addItemInWorlds(WorldItem worldItem, RenderObjectData renderData) {
        try {
            this.physicsTimer.getWorld().addItem(worldItem);
            this.screen.getRenderWorld().addItem(worldItem, renderData);
        } catch (GameException e) {
            throw new RuntimeException(e);
        }
    }

    public void addPointLight(WorldItem worldItem, PointLight light, int attachShadowScene) {
        this.addLight(worldItem, light);
        Game.getGame().getScreen().getScene().getSceneRender().getShadowScene().bindPointLightToShadowScene(attachShadowScene, light);
    }

    public void addLight(WorldItem worldItem, Light light) {
        try {
            if (!worldItem.isSpawned()) {
                throw new GameException("Couldn't attach light. Entity hasn't been spawned!");
            }
            Light light1 = worldItem.attachLight(light);
            light1.enable();
            this.screen.getRenderWorld().getEnvironment().getLightManager().addLight(light);
        } catch (GameException e) {
            Game.getGame().getLogManager().error(e.getMessage());
        }
    }

    public void addLight(Light light) {
        light.enable();
        this.screen.getRenderWorld().getEnvironment().getLightManager().addLight(light);
    }

    public LocalPlayer getLocalPlayer() {
        return this.localPlayer;
    }

    public EntityPlayerSP getPlayerSP() {
        return this.getLocalPlayer().getEntityPlayerSP();
    }

    public void removeEntityFromWorlds(PhysEntity physEntity) {
        this.physicsTimer.getWorld().removeItem(physEntity);
    }

    public void clearEntities() {
        this.physicsTimer.getWorld().clearAllItems();
    }
}
