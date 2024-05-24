package ru.alfabouh.engine.render.scene.world;

import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.exception.GameException;
import ru.alfabouh.engine.physics.liquids.ILiquid;
import ru.alfabouh.engine.physics.world.IWorld;
import ru.alfabouh.engine.physics.world.World;
import ru.alfabouh.engine.physics.world.object.WorldItem;
import ru.alfabouh.engine.render.environment.Environment;
import ru.alfabouh.engine.render.environment.light.Light;
import ru.alfabouh.engine.render.frustum.FrustumCulling;
import ru.alfabouh.engine.render.frustum.ICullable;
import ru.alfabouh.engine.render.scene.SceneRender;
import ru.alfabouh.engine.render.scene.fabric.render.data.RenderLiquidData;
import ru.alfabouh.engine.render.scene.fabric.render.data.RenderObjectData;
import ru.alfabouh.engine.render.scene.objects.IModeledSceneObject;
import ru.alfabouh.engine.render.scene.objects.items.LiquidObject;
import ru.alfabouh.engine.render.scene.objects.items.PhysicsObject;
import ru.alfabouh.engine.render.scene.world.camera.ICamera;

import java.util.*;
import java.util.stream.Collectors;

public final class SceneWorld implements IWorld {
    private final List<IModeledSceneObject> toRenderList;
    private final Set<LiquidObject> liquids;
    private final Environment environment;
    private final World world;
    private int ticks;
    private FrustumCulling frustumCulling;

    public SceneWorld(World world) {
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

    public List<IModeledSceneObject> getFilteredEntityList(SceneRender.RenderPass renderPassFiltering) {
        List<IModeledSceneObject> physicsObjects = new ArrayList<>(this.getToRenderList());
        if (this.getFrustumCulling() == null) {
            return physicsObjects;
        }
        return this.filterCulled(physicsObjects).stream().map(e -> (IModeledSceneObject) e).filter(e -> (renderPassFiltering == null || e.getModelRenderParams().getRenderPassToRenderIn() == renderPassFiltering) && e.isVisible() && !this.isItemReachedRenderDistance(e)).collect(Collectors.toList());
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

    public void addItem(WorldItem worldItem, RenderObjectData renderData) throws GameException {
        if (renderData == null) {
            throw new GameException("Wrong render parameters: " + worldItem.toString());
        }
        PhysicsObject physicsObject = renderData.constructPhysicsObject(this, worldItem);
        this.addPhysEntity(physicsObject);
        worldItem.setRelativeRenderObject(physicsObject);
    }

    private void addWorldItemLight(WorldItem worldItem, Light light) {
        try {
            if (!worldItem.isSpawned()) {
                throw new GameException("Couldn't attach light. Entity hasn't been spawned!");
            }
            Light light1 = worldItem.attachLight(light);
            light1.start();
            this.getEnvironment().getLightManager().addLight(light);
        } catch (GameException e) {
            System.err.println(e.getMessage());
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

    private void cleanAll() {
        this.getEnvironment().getLightManager().removeAllLights();
        Iterator<IModeledSceneObject> iterator = this.getToRenderList().iterator();
        while (iterator.hasNext()) {
            IModeledSceneObject modeledSceneObject = iterator.next();
            if (modeledSceneObject instanceof PhysicsObject) {
                PhysicsObject physicsObject = (PhysicsObject) modeledSceneObject;
                physicsObject.onDestroy(this);
            }
            iterator.remove();
        }

        Iterator<LiquidObject> iterator1 = this.getLiquids().iterator();
        while (iterator1.hasNext()) {
            LiquidObject liquidObject = iterator1.next();
            liquidObject.getModel().clean();
            iterator1.remove();
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
        Game.getGame().getScreen().zeroRenderTick();
        this.getEnvironment().init(this);
        this.ticks = 0;
    }

    public void onWorldUpdate() {
        this.ticks += 1;
    }

    @Override
    public void onWorldEnd() {
        this.cleanAll();
    }

    public int getTicks() {
        return this.ticks;
    }

    public void onWorldEntityUpdate(boolean refresh, double partialTicks) {
        Iterator<IModeledSceneObject> iterator = this.getToRenderList().iterator();
        while (iterator.hasNext()) {
            IModeledSceneObject sceneObject = iterator.next();
            if (sceneObject instanceof PhysicsObject) {
                PhysicsObject physicsObject = (PhysicsObject) sceneObject;
                if (refresh) {
                    physicsObject.refreshInterpolatingState();
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
