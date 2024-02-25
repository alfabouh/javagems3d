package ru.BouH.engine.render.scene.world;

import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.GameEvents;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.math.Pair;
import ru.BouH.engine.physics.liquids.ILiquid;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.environment.Environment;
import ru.BouH.engine.render.environment.light.Light;
import ru.BouH.engine.render.frustum.FrustumCulling;
import ru.BouH.engine.render.frustum.ICullable;
import ru.BouH.engine.render.scene.fabric.render.data.RenderLiquidData;
import ru.BouH.engine.render.scene.fabric.render.data.RenderObjectData;
import ru.BouH.engine.render.scene.objects.IModeledSceneObject;
import ru.BouH.engine.render.scene.objects.items.LiquidObject;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.world.camera.ICamera;

import java.util.*;
import java.util.stream.Collectors;

public final class SceneWorld implements IWorld {
    public static final double RENDER_TICKS_UPD_RATE = 60.0d;
    private final List<IModeledSceneObject> toRenderList;
    private final Set<LiquidObject> liquids;
    private final Deque<Pair<WorldItem, RenderObjectData>> toAddRenderObjectQueue;
    private final Deque<Pair<WorldItem, Light>> toAddObjectLightQueue;
    private final Environment environment;
    private final World world;
    private int ticks;
    private float elapsedRenderTicks;
    private double lastRenderTicksUpdate = Game.glfwTime();
    private FrustumCulling frustumCulling;

    public SceneWorld(World world) {
        this.toAddRenderObjectQueue = new ArrayDeque<>();
        this.toAddObjectLightQueue = new ArrayDeque<>();
        this.liquids = new HashSet<>();
        this.world = world;
        this.toRenderList = new ArrayList<>();
        this.environment = Environment.createEnvironment();
        this.frustumCulling = null;
    }

    public boolean isItemReachedRenderDistance(IModeledSceneObject renderObject) {
        if (!renderObject.hasRender()) {
            return true;
        }
        ICamera camera = Game.getGame().getScreen().getCamera();
        return renderObject.getModelRenderParams().getRenderDistance() >= 0 && camera.getCamPosition().distance(renderObject.getModel3D().getFormat().getPosition()) > renderObject.getModelRenderParams().getRenderDistance();
    }

    public Collection<? extends ICullable> filterCulled(Collection<? extends ICullable> list) {
        if (this.getFrustumCulling() == null) {
            return list;
        }
        return list.stream().filter(e -> this.getFrustumCulling().isInFrustum(e.getRenderABB()) || !e.canBeCulled()).collect(Collectors.toList());
    }

    public List<IModeledSceneObject> getFilteredEntityList() {
        List<IModeledSceneObject> physicsObjects = new ArrayList<>(this.getToRenderList());
        if (this.getFrustumCulling() == null) {
            return physicsObjects;
        }
        return this.filterCulled(physicsObjects).stream().map(e -> (IModeledSceneObject) e).filter(e -> e.isVisible() && !this.isItemReachedRenderDistance(e)).collect(Collectors.toList());
    }

    public List<IModeledSceneObject> getToRenderList() {
        return this.toRenderList;
    }

    public void addRenderObjectInScene(IModeledSceneObject renderObject) {
        this.getToRenderList().add(renderObject);
    }

    public void removeRenderObjectFromScene(IModeledSceneObject renderObject) {
        if (!this.getToRenderList().remove(renderObject)) {
            Game.getGame().getLogManager().warn("Couldn't remove a render object from scene rendering!");
        }
    }

    public void addItemInQueue(WorldItem worldItem, RenderObjectData renderData) {
        this.toAddRenderObjectQueue.add(new Pair<>(worldItem, renderData));
    }

    public void addItem(WorldItem worldItem, RenderObjectData renderData) throws GameException {
        if (renderData == null) {
            throw new GameException("Wrong render parameters: " + worldItem.toString());
        }
        PhysicsObject physicsObject = renderData.constructPhysicsObject(this, worldItem);
        this.addPhysEntity(physicsObject);
        worldItem.setRelativeRenderObject(physicsObject);
    }

    public void addWorldItemLightInQueue(WorldItem worldItem, Light light) {
        this.toAddObjectLightQueue.add(new Pair<>(worldItem, light));
    }

    private void addWorldItemLight(WorldItem worldItem, Light light) {
        try {
            if (!worldItem.isSpawned()) {
                throw new GameException("Couldn't attach light. Entity hasn't been spawned!");
            }
            Light light1 = worldItem.attachLight(light);
            light1.enable();
            this.getEnvironment().getLightManager().addLight(light);
        } catch (GameException e) {
            Game.getGame().getLogManager().error(e.getMessage());
        }
    }

    public void addPhysEntity(PhysicsObject physicsObject) {
        physicsObject.onSpawn(this);
        this.addRenderObjectInScene(physicsObject);
    }

    public void removeEntity(PhysicsObject physicsObject) {
        physicsObject.onDestroy(this);
        this.removeRenderObjectFromScene(physicsObject);
    }

    public void addLiquid(ILiquid liquid, RenderLiquidData renderLiquidData) {
        this.getLiquids().add(new LiquidObject(liquid, renderLiquidData));
    }

    public Set<LiquidObject> getLiquids() {
        return this.liquids;
    }

    public List<PhysicsObject> getPhysicsObjects() {
        return this.getToRenderList().stream().filter(e -> e instanceof PhysicsObject).map(e -> (PhysicsObject) e).collect(Collectors.toList());
    }

    public void removeAllEntities() {
        Iterator<PhysicsObject> iterator = this.getPhysicsObjects().iterator();
        while (iterator.hasNext()) {
            PhysicsObject physicsObject = iterator.next();
            physicsObject.onDestroy(this);
            iterator.remove();
        }
    }

    public void disableLight(WorldItem worldItem, int i) {
        this.removeLight(worldItem.getAttachedLights().get(i));
    }

    public void removeLight(Light light) {
        light.setEnabled(false);
    }

    public void addLight(Light light) {
        this.getEnvironment().getLightManager().addLight(light);
    }

    @Override
    public void onWorldStart() {
        this.getEnvironment().init(this);
        GameEvents.addSceneModels(this);
    }

    public void onWorldUpdate() {
        double curr = Game.glfwTime();
        if (curr - this.lastRenderTicksUpdate > 1.0d / RENDER_TICKS_UPD_RATE) {
            this.elapsedRenderTicks += 0.01f;
            this.lastRenderTicksUpdate = curr;
        }
        this.ticks += 1;
    }

    @Override
    public void onWorldEnd() {
        this.removeAllEntities();
    }

    public int getTicks() {
        return this.ticks;
    }

    public float getElapsedRenderTicks() {
        return this.elapsedRenderTicks;
    }

    public void updateToAddQueue() {
        while (!this.toAddRenderObjectQueue.isEmpty()) {
            Pair<WorldItem, RenderObjectData> renderObjectDataPair = this.toAddRenderObjectQueue.pollFirst();
            this.addItem(renderObjectDataPair.getKey(), renderObjectDataPair.getValue());
        }
        while (!this.toAddObjectLightQueue.isEmpty()) {
            Pair<WorldItem, Light> renderObjectDataPair = this.toAddObjectLightQueue.pollFirst();
            this.addWorldItemLight(renderObjectDataPair.getKey(), renderObjectDataPair.getValue());
        }
    }

    public void onWorldEntityUpdate(boolean refresh, double partialTicks) {
        this.updateToAddQueue();
        Iterator<IModeledSceneObject> iterator = this.getToRenderList().iterator();
        while (iterator.hasNext()) {
            IModeledSceneObject sceneObject = iterator.next();
            if (sceneObject instanceof PhysicsObject) {
                PhysicsObject physicsObject = (PhysicsObject) sceneObject;
                if (refresh) {
                    physicsObject.refreshInterpolatingState();
                    physicsObject.setPrevPos(physicsObject.getWorldItem().getPosition());
                    physicsObject.setPrevRot(physicsObject.getWorldItem().getRotation());
                }
                physicsObject.onUpdate(this);
                physicsObject.updateRenderPos(partialTicks);
                physicsObject.updateRenderTranslation();
                if (physicsObject.isDead()) {
                    physicsObject.onDestroy(this);
                    iterator.remove();
                }
            }
        }
        this.onWorldUpdate();
    }

    public FrustumCulling getFrustumCulling() {
        return this.frustumCulling;
    }

    public void setFrustumCulling(FrustumCulling frustumCulling) {
        this.frustumCulling = frustumCulling;
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public World getWorld() {
        return this.world;
    }
}
