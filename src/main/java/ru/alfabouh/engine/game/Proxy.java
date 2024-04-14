package ru.alfabouh.engine.game;

import ru.alfabouh.engine.game.exception.GameException;
import ru.alfabouh.engine.math.Pair;
import ru.alfabouh.engine.physics.liquids.ILiquid;
import ru.alfabouh.engine.physics.triggers.ITriggerZone;
import ru.alfabouh.engine.physics.world.object.WorldItem;
import ru.alfabouh.engine.physics.world.timer.PhysicsTimer;
import ru.alfabouh.engine.render.environment.light.Light;
import ru.alfabouh.engine.render.environment.light.PointLight;
import ru.alfabouh.engine.render.scene.fabric.render.data.RenderLiquidData;
import ru.alfabouh.engine.render.scene.fabric.render.data.RenderObjectData;
import ru.alfabouh.engine.render.screen.Screen;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class Proxy {
    private final PhysicsTimer physicsTimer;
    private final Screen screen;
    private final Deque<Pair<WorldItem, RenderObjectData>> deque1;
    private final Deque<Pair<WorldItem, Light>> deque2;
    private final Deque<Pair<ILiquid, RenderLiquidData>> deque3;

    public Proxy(PhysicsTimer gameWorldTimer, Screen screen) {
        this.physicsTimer = gameWorldTimer;
        this.screen = screen;

        this.deque1 = new ArrayDeque<>();
        this.deque2 = new ArrayDeque<>();
        this.deque3 = new ArrayDeque<>();
    }

    public synchronized Deque<Pair<WorldItem, RenderObjectData>> getDeque1() {
        return this.deque1;
    }

    public synchronized Deque<Pair<WorldItem, Light>> getDeque2() {
        return this.deque2;
    }

    public synchronized Deque<Pair<ILiquid, RenderLiquidData>> getDeque3() {
        return this.deque3;
    }

    public void update() {
        Iterator<Pair<WorldItem, RenderObjectData>> it1 = this.getDeque1().iterator();
        while (it1.hasNext()) {
            Pair<WorldItem, RenderObjectData> pair = it1.next();
            this.screen.getRenderWorld().addItem(pair.getKey(), pair.getValue());
            it1.remove();
        }

        Iterator<Pair<WorldItem, Light>> it2 = this.getDeque2().iterator();
        while (it2.hasNext()) {
            Pair<WorldItem, Light> pair = it2.next();
            pair.getKey().attachLight(pair.getValue());
            this.addLight(pair.getValue());
            it2.remove();
        }

        Iterator<Pair<ILiquid, RenderLiquidData>> it3 = this.getDeque3().iterator();
        while (it3.hasNext()) {
            Pair<ILiquid, RenderLiquidData> pair = it3.next();
            this.screen.getRenderWorld().addLiquid(pair.getKey(), pair.getValue());
            it3.remove();
        }
    }

    public void addItemInWorlds(WorldItem worldItem, RenderObjectData renderData) {
        try {
            this.physicsTimer.getWorld().addItem(worldItem);
            this.getDeque1().add(new Pair<>(worldItem, renderData));
        } catch (GameException e) {
            throw new GameException(e);
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
        this.getDeque3().add(new Pair<>(liquid, renderLiquidData));
    }

    public void addTriggerZone(ITriggerZone triggerZone) {
        this.physicsTimer.getWorld().addTriggerZone(triggerZone);
    }

    public void addLight(WorldItem worldItem, Light light) {
        this.getDeque2().add(new Pair<>(worldItem, light));
    }

    public void addLight(Light light) {
        light.start();
        this.screen.getRenderWorld().getEnvironment().getLightManager().addLight(light);
    }
}
