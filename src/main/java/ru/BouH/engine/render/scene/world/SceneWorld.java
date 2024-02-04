package ru.BouH.engine.render.scene.world;

import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.GameEvents;
import ru.BouH.engine.game.exception.GameException;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.environment.Environment;
import ru.BouH.engine.render.environment.light.Light;
import ru.BouH.engine.render.frustum.FrustumCulling;
import ru.BouH.engine.render.scene.objects.IModeledSceneObject;
import ru.BouH.engine.render.scene.objects.items.PhysicsObjectModeled;
import ru.BouH.engine.render.scene.fabric.render_data.RenderObjectData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public final class SceneWorld implements IWorld {
    public static float elapsedRenderTicks;
    private final List<IModeledSceneObject> toRenderList;
    private final Environment environment;
    private final World world;
    private FrustumCulling frustumCulling;

    public SceneWorld(World world) {
        this.world = world;
        this.toRenderList = new ArrayList<>();
        this.environment = Environment.createEnvironment();
        this.frustumCulling = null;
    }

    public List<IModeledSceneObject> getFilteredEntityList() {
        List<IModeledSceneObject> physicsObjects = new ArrayList<>(this.getToRenderList());
        if (this.getFrustumCulling() == null) {
            return physicsObjects;
        }
        return physicsObjects.stream().filter(e -> e.isVisible() && (this.getFrustumCulling().isInFrustum(e.getRenderABB()) || !e.canBeCulled())).collect(Collectors.toList());
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
        PhysicsObjectModeled physicsObject = renderData.constructPhysicsObject(this, worldItem);
        this.addPhysEntity(physicsObject);
        worldItem.setRelativeRenderObject(physicsObject);
    }

    public void addPhysEntity(PhysicsObjectModeled physicsObject) {
        physicsObject.onSpawn(this);
        this.addRenderObjectInScene(physicsObject);
    }

    public void removeEntity(PhysicsObjectModeled physicsObject) {
        physicsObject.onDestroy(this);
        this.removeRenderObjectFromScene(physicsObject);
    }

    public List<PhysicsObjectModeled> getPhysicsObjects() {
        return this.getToRenderList().stream().filter(e -> e instanceof PhysicsObjectModeled).map(e -> (PhysicsObjectModeled) e).collect(Collectors.toList());
    }

    public void removeAllEntities() {
        Iterator<PhysicsObjectModeled> iterator = this.getPhysicsObjects().iterator();
        while (iterator.hasNext()) {
            PhysicsObjectModeled physicsObject = iterator.next();
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
        SceneWorld.elapsedRenderTicks += 0.01f;
    }

    @Override
    public void onWorldEnd() {
        this.removeAllEntities();
    }

    public void onWorldEntityUpdate(boolean refresh, double partialTicks) {
        Iterator<IModeledSceneObject> iterator = this.getToRenderList().iterator();
        while (iterator.hasNext()) {
            IModeledSceneObject sceneObject = iterator.next();
            if (sceneObject instanceof PhysicsObjectModeled) {
                PhysicsObjectModeled physicsObject = (PhysicsObjectModeled) sceneObject;
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
