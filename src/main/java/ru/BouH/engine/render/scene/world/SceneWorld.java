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
import ru.BouH.engine.render.scene.fabric.models.base.IRenderSceneModel;
import ru.BouH.engine.render.scene.objects.items.PhysicsObject;
import ru.BouH.engine.render.scene.preforms.RenderObjectData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public final class SceneWorld implements IWorld {
    public static float elapsedRenderTicks;
    private final List<PhysicsObject> entityList;
    private final List<IRenderSceneModel> sceneModelsList;
    private final Environment environment;
    private final World world;
    private FrustumCulling frustumCulling;

    public SceneWorld(World world) {
        this.world = world;
        this.entityList = new ArrayList<>();
        this.sceneModelsList = new ArrayList<>();
        this.environment = Environment.createEnvironment();
        this.frustumCulling = null;
    }

    public List<PhysicsObject> getFilteredEntityList() {
        List<PhysicsObject> physicsObjects = new ArrayList<>(this.getEntityList());
        if (this.getFrustumCulling() == null) {
            return physicsObjects;
        }
        return physicsObjects.stream().filter(PhysicsObject::isVisible).collect(Collectors.toList());
    }

    public List<PhysicsObject> getEntityList() {
        return this.entityList;
    }

    public List<IRenderSceneModel> getSceneModelsList() {
        return this.sceneModelsList;
    }

    public void addModelToRender(IRenderSceneModel renderSceneModel) {
        this.getSceneModelsList().add(renderSceneModel);
    }

    public void removeModelFromRender(IRenderSceneModel renderSceneModel) {
        if (!this.getSceneModelsList().remove(renderSceneModel)) {
            Game.getGame().getLogManager().warn("Couldn't remove a model from scene rendering!");
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

    public void addPhysEntity(PhysicsObject physicsObject) {
        physicsObject.onSpawn(this);
        this.getEntityList().add(physicsObject);
    }

    public void removeEntity(PhysicsObject physicsObject) {
        physicsObject.onDestroy(this);
        this.getEntityList().remove(physicsObject);
    }

    public void removeAllEntities() {
        Iterator<PhysicsObject> iterator = this.getEntityList().iterator();
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
        SceneWorld.elapsedRenderTicks += 0.01f;
    }

    @Override
    public void onWorldEnd() {
        this.removeAllEntities();
    }

    public void onWorldEntityUpdate(boolean refresh, double partialTicks) {
        Iterator<PhysicsObject> iterator = this.getEntityList().iterator();
        while (iterator.hasNext()) {
            PhysicsObject physicsObject = iterator.next();
            if (refresh) {
                physicsObject.refreshInterpolatingState();
                physicsObject.setPrevPos(physicsObject.getWorldItem().getPosition());
                physicsObject.setPrevRot(physicsObject.getWorldItem().getRotation());
            }
            physicsObject.onUpdate(this);
            physicsObject.updateRenderPos(partialTicks);
            physicsObject.updateRenderTranslation();
            physicsObject.checkCulling(this.getFrustumCulling().isEnabled() ? this.getFrustumCulling() : null);
            if (physicsObject.isDead()) {
                physicsObject.onDestroy(this);
                iterator.remove();
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
