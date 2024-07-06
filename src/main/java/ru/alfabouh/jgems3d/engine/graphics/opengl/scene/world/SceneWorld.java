package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.world;

import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.physics.liquids.ILiquid;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;
import ru.alfabouh.jgems3d.engine.graphics.opengl.environment.Environment;
import ru.alfabouh.jgems3d.engine.graphics.opengl.environment.light.Light;
import ru.alfabouh.jgems3d.engine.graphics.opengl.frustum.FrustumCulling;
import ru.alfabouh.jgems3d.engine.graphics.opengl.frustum.ICullable;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.RenderLiquidData;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.RenderObjectData;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.IModeledSceneObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.items.LiquidObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.items.AbstractSceneItemObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.world.camera.ICamera;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;
import ru.alfabouh.jgems3d.logger.SystemLogging;

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
        ICamera camera = JGems.get().getScreen().getCamera();
        return renderObject.getModelRenderParams().getRenderDistance() >= 0 && camera.getCamPosition().distance(renderObject.getModel().getFormat().getPosition()) > renderObject.getModelRenderParams().getRenderDistance();
    }

    public Collection<? extends ICullable> getEntitiesFrustumCulledList(Collection<? extends ICullable> list) {
        if (this.getFrustumCulling() == null) {
            return list;
        }
        return list.stream().filter(e -> this.getFrustumCulling().isInFrustum(e.calcRenderSphere()) || !e.canBeCulled()).collect(Collectors.toList());
    }

    public List<IModeledSceneObject> getFilteredEntityList(boolean deferredPass) {
        List<IModeledSceneObject> physicsObjects = new ArrayList<>(this.getModeledSceneEntities());
        if (this.getFrustumCulling() == null) {
            return physicsObjects;
        }
        return this.getEntitiesFrustumCulledList(physicsObjects).stream().map(e -> (IModeledSceneObject) e).filter(e -> e.getModelRenderParams().getShaderManager().isUseForGBuffer() == deferredPass && e.isVisible() && !this.isItemReachedRenderDistance(e)).collect(Collectors.toList());
    }

    public void addRenderObjectInScene(IModeledSceneObject renderObject) {
        this.getModeledSceneEntities().add(renderObject);
    }

    public void removeRenderObjectFromScene(IModeledSceneObject renderObject) {
        if (!this.getModeledSceneEntities().remove(renderObject)) {
            SystemLogging.get().getLogManager().warn("Couldn't remove a render object from scene rendering!");
        }
    }

    public void addItem(WorldItem worldItem, RenderObjectData renderData) throws JGemsException {
        if (renderData == null) {
            throw new JGemsException("Wrong render parameters: " + worldItem.toString());
        }
        AbstractSceneItemObject abstractSceneItemObject = renderData.constructPhysicsObject(this, worldItem);
        this.addPhysEntity(abstractSceneItemObject);
        worldItem.setRelativeRenderObject(abstractSceneItemObject);
    }

    private void addWorldItemLight(WorldItem worldItem, Light light) {
        try {
            if (!worldItem.isSpawned()) {
                throw new JGemsException("Couldn't attach light. Entity hasn't been spawned!");
            }
            Light light1 = worldItem.attachLight(light);
            light1.start();
            this.getEnvironment().getLightManager().addLight(light);
        } catch (JGemsException e) {
            System.err.println(e.getMessage());
        }
    }

    public void addPhysEntity(AbstractSceneItemObject abstractSceneItemObject) {
        abstractSceneItemObject.onSpawn(this);
        this.addRenderObjectInScene(abstractSceneItemObject);
    }

    public void removeEntity(AbstractSceneItemObject abstractSceneItemObject) {
        abstractSceneItemObject.onDestroy(this);
        this.removeRenderObjectFromScene(abstractSceneItemObject);
    }

    public void addLiquid(ILiquid liquid, RenderLiquidData renderLiquidData) {
        this.getLiquids().add(new LiquidObject(liquid, renderLiquidData));
    }

    public void removeLight(WorldItem worldItem, int i) {
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
        //JGems.get().getScreen().zeroRenderTick();
        this.getEnvironment().init(this);
        this.ticks = 0;
    }

    @Override
    public void onWorldUpdate() {
        this.ticks += 1;
    }

    public void onWorldEntityUpdate(boolean refresh, float partialTicks) {
        Iterator<IModeledSceneObject> iterator = this.getModeledSceneEntities().iterator();
        while (iterator.hasNext()) {
            IModeledSceneObject sceneObject = iterator.next();
            if (sceneObject instanceof AbstractSceneItemObject) {
                AbstractSceneItemObject abstractSceneItemObject = (AbstractSceneItemObject) sceneObject;
                if (refresh) {
                    abstractSceneItemObject.refreshInterpolatingState();
                }
                abstractSceneItemObject.onUpdate(this);
                abstractSceneItemObject.updateRenderPos(partialTicks);
                abstractSceneItemObject.updateRenderTranslation();
                if (abstractSceneItemObject.isDead()) {
                    abstractSceneItemObject.onDestroy(this);
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public void onWorldEnd() {
        this.cleanAll();
    }

    private void cleanAll() {
        this.getEnvironment().getLightManager().removeAllLights();
        Iterator<IModeledSceneObject> iterator = this.getModeledSceneEntities().iterator();
        while (iterator.hasNext()) {
            IModeledSceneObject modeledSceneObject = iterator.next();
            if (modeledSceneObject instanceof AbstractSceneItemObject) {
                AbstractSceneItemObject abstractSceneItemObject = (AbstractSceneItemObject) modeledSceneObject;
                abstractSceneItemObject.onDestroy(this);
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

    public int getTicks() {
        return this.ticks;
    }

    public Set<LiquidObject> getLiquids() {
        return this.liquids;
    }

    public List<IModeledSceneObject> getModeledSceneEntities() {
        return this.toRenderList;
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
