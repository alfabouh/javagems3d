package ru.BouH.engine.proxy;

import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.physics.entities.PhysEntity;
import ru.BouH.engine.physics.entities.player.EntityPlayerSP;
import ru.BouH.engine.physics.liquids.ILiquid;
import ru.BouH.engine.physics.triggers.ITriggerZone;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.physics.world.timer.PhysicsTimer;
import ru.BouH.engine.render.environment.light.Light;
import ru.BouH.engine.render.environment.light.PointLight;
import ru.BouH.engine.render.scene.fabric.render.data.RenderLiquidData;
import ru.BouH.engine.render.scene.fabric.render.data.RenderObjectData;
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
            this.screen.getRenderWorld().addItemInQueue(worldItem, renderData);
        } catch (GameException e) {
            throw new RuntimeException(e);
        }
    }

    public void addPointLight(WorldItem worldItem, PointLight light, int attachShadowScene) {
        this.addLight(worldItem, light);
        this.screen.getScene().getSceneRender().getShadowScene().bindPointLightToShadowScene(attachShadowScene, light);
    }

    public void addPointLight(PointLight light, int attachShadowScene) {
        this.addLight(light);
        this.screen.getScene().getSceneRender().getShadowScene().bindPointLightToShadowScene(attachShadowScene, light);
    }

    public void addLiquidInWorlds(ILiquid liquid, RenderLiquidData renderLiquidData) {
        this.physicsTimer.getWorld().addLiquid(liquid);
        this.screen.getRenderWorld().addLiquid(liquid, renderLiquidData);
    }

    public void addTriggerZone(ITriggerZone triggerZone) {
        this.physicsTimer.getWorld().addTriggerZone(triggerZone);
    }

    public void addLight(WorldItem worldItem, Light light) {
        this.screen.getRenderWorld().addWorldItemLightInQueue(worldItem, light);
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
