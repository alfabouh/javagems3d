package ru.alfabouh.jgems3d.engine.system.proxy;

import org.jetbrains.annotations.Contract;
import ru.alfabouh.jgems3d.engine.math.Pair;
import ru.alfabouh.jgems3d.engine.physics.world.triggers.liquids.base.Liquid;
import ru.alfabouh.jgems3d.engine.physics.world.triggers.zones.base.ITriggerZone;
import ru.alfabouh.jgems3d.engine.physics.world.basic.WorldItem;
import ru.alfabouh.jgems3d.engine.physics.world.thread.timer.PhysicsTimer;
import ru.alfabouh.jgems3d.engine.graphics.opengl.environment.light.Light;
import ru.alfabouh.jgems3d.engine.graphics.opengl.environment.light.PointLight;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderLiquidData;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderObjectData;
import ru.alfabouh.jgems3d.engine.graphics.opengl.screen.JGemsScreen;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;

import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

public final class Proxy {
    private final PhysicsTimer physicsTimer;
    private final JGemsScreen screen;

    public Proxy(PhysicsTimer gameWorldTimer, JGemsScreen screen) {
        this.physicsTimer = gameWorldTimer;
        this.screen = screen;
    }

    public void update() {
    }

    public void addItemInWorlds(WorldItem worldItem, RenderObjectData renderData) {
        try {
            this.physicsTimer.getWorld().addItem(worldItem);
            this.screen.getRenderWorld().addItem(worldItem, renderData);
        } catch (JGemsException e) {
            throw new JGemsException(e);
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

    public void addLiquidInWorlds(Liquid liquid, RenderLiquidData renderLiquidData) {
        this.physicsTimer.getWorld().addItem(liquid);
        this.screen.getRenderWorld().addLiquid(liquid, renderLiquidData);
    }

    public void addTriggerZone(ITriggerZone triggerZone) {
        this.physicsTimer.getWorld().addItem(triggerZone);
    }

    public void addLight(WorldItem worldItem, Light light) {
        this.screen.getRenderWorld().addWorldItemLight(worldItem, light);
    }

    public void addLight(Light light) {
        light.start();
        this.screen.getRenderWorld().getEnvironment().getLightManager().addLight(light);
    }
}
