/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.engine.graphics.opengl.world;

import javagems3d.engine.JGems3D;
import javagems3d.engine.JGemsHelper;
import javagems3d.engine.api_bridge.events.APIEventsLauncher;
import javagems3d.engine.graphics.opengl.camera.AttachedCamera;
import javagems3d.engine.graphics.opengl.camera.ICamera;
import javagems3d.engine.graphics.opengl.environment.Environment;
import javagems3d.engine.graphics.opengl.environment.light.Light;
import javagems3d.engine.graphics.opengl.frustum.FrustumCulling;
import javagems3d.engine.graphics.opengl.frustum.ICulled;
import javagems3d.engine.graphics.opengl.particles.ParticlesEmitter;
import javagems3d.engine.graphics.opengl.rendering.JGemsDebugGlobalConstants;
import javagems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderEntityData;
import javagems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderLiquidData;
import javagems3d.engine.graphics.opengl.rendering.items.ILightsKeeper;
import javagems3d.engine.graphics.opengl.rendering.items.IModeledSceneObject;
import javagems3d.engine.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import javagems3d.engine.graphics.opengl.rendering.items.objects.LiquidObject;
import javagems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;
import javagems3d.engine.physics.world.IWorld;
import javagems3d.engine.physics.world.basic.IWorldTicked;
import javagems3d.engine.physics.world.basic.WorldItem;
import javagems3d.engine.physics.world.triggers.liquids.base.Liquid;
import javagems3d.engine.system.resources.assets.shaders.RenderPass;
import javagems3d.engine.system.service.collections.Pair;
import javagems3d.engine.system.service.exceptions.JGemsException;
import javagems3d.engine.system.service.synchronizing.SyncManager;
import engine_api.events.bus.Events;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * In the world of the scene, logic is being updated for the functioning of the render based on information from the physical world.
 */
public final class SceneWorld implements IWorld {
    private final ParticlesEmitter particlesEmitter;

    private final Set<Pair<WorldItem, Light>> lightAttachmentQueue;

    private final Map<Integer, AbstractSceneEntity> objectMap;
    private final Set<IModeledSceneObject> toRenderSet;
    private final Set<LiquidObject> liquids;
    private final Environment environment;
    private FrustumCulling frustumCulling;
    private int ticks;

    public SceneWorld() {
        this.lightAttachmentQueue = SyncManager.createSyncronisedSet();
        this.objectMap = SyncManager.createSyncronisedMap();
        this.liquids = SyncManager.createSyncronisedSet();
        this.toRenderSet = SyncManager.createSyncronisedSet();

        this.environment = new Environment(this);
        this.frustumCulling = null;

        this.particlesEmitter = new ParticlesEmitter();
    }

    //section WorldStart
    @Override
    public void onWorldStart() {
        APIEventsLauncher.pushEvent(new Events.RenderWorldStart(Events.Stage.PRE, this));
        JGemsDebugGlobalConstants.reset();
        JGems3D.get().getScreen().zeroRenderTick();
        this.getParticlesEmitter().create(this);
        this.getEnvironment().init(this);
        this.ticks = 0;
        APIEventsLauncher.pushEvent(new Events.RenderWorldStart(Events.Stage.POST, this));
    }

    //section WorldUpdate
    @Override
    public void onWorldUpdate() {
        if (!APIEventsLauncher.pushEvent(new Events.RenderWorldTickPre(this)).isCancelled()) {
            Iterator<Pair<WorldItem, Light>> iterator = this.lightAttachmentQueue.iterator();
            while (iterator.hasNext()) {
                Pair<WorldItem, Light> pair = iterator.next();
                this.addWorldItemLight(pair.getFirst(), pair.getSecond());
                iterator.remove();
            }
            this.ticks += 1;
        }
        APIEventsLauncher.pushEvent(new Events.RenderWorldTickPost(this));
    }

    //section WorldEnd
    @Override
    public void onWorldEnd() {
        APIEventsLauncher.pushEvent(new Events.RenderWorldEnd(Events.Stage.PRE, this));
        this.getParticlesEmitter().destroy(this);
        this.getEnvironment().destroy(this);
        this.cleanAll();
        APIEventsLauncher.pushEvent(new Events.RenderWorldEnd(Events.Stage.POST, this));
    }

    //section WorldUpdObj
    public void updateWorldObjects(boolean refresh, FrameTicking frameTicking) {
        this.getParticlesEmitter().onUpdateParticles(frameTicking.getFrameDeltaTime(), this);

        Iterator<IModeledSceneObject> iterator = this.getModeledSceneEntities().iterator();
        while (iterator.hasNext()) {
            IModeledSceneObject sceneObject = iterator.next();
            if (sceneObject instanceof IWorldTicked) {
                IWorldTicked worldTicked = (IWorldTicked) sceneObject;
                worldTicked.onUpdate(this);
            }
            if (sceneObject instanceof AbstractSceneEntity) {
                AbstractSceneEntity abstractSceneEntity = (AbstractSceneEntity) sceneObject;
                if (abstractSceneEntity.isDead()) {
                    this.getObjectMap().remove(abstractSceneEntity.getWorldItem().getItemId());
                    abstractSceneEntity.onDestroy(this);
                    iterator.remove();
                    continue;
                }
                if (refresh) {
                    abstractSceneEntity.refreshInterpolatingState();
                }
                abstractSceneEntity.updateRenderPos(frameTicking.getPhysicsSyncTicks());
                abstractSceneEntity.updateRenderTranslation();
            }
        }

        Iterator<LiquidObject> iterator2 = this.getLiquids().iterator();
        while (iterator2.hasNext()) {
            LiquidObject liquidObject = iterator2.next();
            if (liquidObject.getLiquid().isDead()) {
                liquidObject.getModel().clean();
                iterator2.remove();
            }
        }
    }

    //section WorldClean
    private void cleanAll() {
        Iterator<IModeledSceneObject> iterator = this.getModeledSceneEntities().iterator();
        while (iterator.hasNext()) {
            IModeledSceneObject modeledSceneObject = iterator.next();
            if (modeledSceneObject instanceof AbstractSceneEntity) {
                AbstractSceneEntity abstractSceneEntity = (AbstractSceneEntity) modeledSceneObject;
                abstractSceneEntity.onDestroy(this);
            }
            iterator.remove();
        }

        Iterator<LiquidObject> iterator1 = this.getLiquids().iterator();
        while (iterator1.hasNext()) {
            LiquidObject liquidObject = iterator1.next();
            liquidObject.getModel().clean();
            iterator1.remove();
        }
        this.getObjectMap().clear();
    }

    public boolean checkReachedRenderDistance(IModeledSceneObject renderObject) {
        if (!renderObject.hasRender()) {
            return true;
        }
        ICamera camera = JGems3D.get().getScreen().getCamera();
        return renderObject.getMeshRenderData().getRenderAttributes().getRenderDistance() >= 0 && camera.getCamPosition().distance(renderObject.getModel().getFormat().getPosition()) > renderObject.getMeshRenderData().getRenderAttributes().getRenderDistance();
    }

    public Collection<? extends ICulled> getCollectionFrustumCulledList(Collection<? extends ICulled> list) {
        if (this.getFrustumCulling() == null) {
            return list;
        }
        return list.stream().filter(e -> this.getFrustumCulling().isInFrustum(e.calcRenderSphere()) || !e.canBeCulled()).collect(Collectors.toList());
    }

    public Set<IModeledSceneObject> getFilteredEntitySet(RenderPass renderPass) {
        if (this.getFrustumCulling() == null) {
            return this.getModeledSceneEntities();
        }
        return this.getCollectionFrustumCulledList(this.getModeledSceneEntities()).stream().map(e -> (IModeledSceneObject) e).filter(e -> (renderPass == null || e.getMeshRenderData().getShaderManager().checkShaderRenderPass(renderPass)) && e.isVisible() && !this.checkReachedRenderDistance(e)).collect(Collectors.toSet());
    }

    public AttachedCamera createAttachedCamera(WorldItem worldItem) {
        AbstractSceneEntity abstractSceneEntity = this.getObjectMap().get(worldItem.getItemId());
        if (abstractSceneEntity == null) {
            JGemsHelper.getLogger().warn("Couldn't attach camera on " + worldItem + ". SceneEntity doesn't exist");
            return null;
        }
        return new AttachedCamera(abstractSceneEntity);
    }

    public boolean attachCameraOn(WorldItem worldItem, AttachedCamera attachedCamera) {
        AbstractSceneEntity abstractSceneEntity = this.getObjectMap().get(worldItem.getItemId());
        if (abstractSceneEntity == null) {
            JGemsHelper.getLogger().warn("Couldn't attach camera on " + worldItem + ". SceneEntity doesn't exist");
            return false;
        }
        attachedCamera.attachCameraOnItem(abstractSceneEntity);
        return true;
    }

    public void addItem(WorldItem worldItem, RenderEntityData renderData) throws JGemsException {
        AbstractSceneEntity abstractSceneEntity = renderData.constructPhysicsObject(this, worldItem);
        this.addEntityInWorld(abstractSceneEntity);
    }

    public void removeLight(Light light) {
        this.getEnvironment().getLightManager().removeLight(light);
    }

    public void addLight(Light light) {
        this.getEnvironment().getLightManager().addLight(light);
    }

    public void addItemLight(ILightsKeeper keepLights, Light light) {
        keepLights.addLight(light);
        this.getEnvironment().getLightManager().addLight(light);
    }

    public void addWorldItemLight(WorldItem worldItem, Light light) {
        if (!worldItem.isSpawned()) {
            JGemsHelper.getLogger().error("Couldn't attach light. Entity hasn't been spawned!");
            return;
        }
        AbstractSceneEntity abstractSceneEntity = this.getObjectMap().get(worldItem.getItemId());
        if (abstractSceneEntity == null) {
            this.lightAttachmentQueue.add(new Pair<>(worldItem, light));
            return;
        }
        abstractSceneEntity.addLight(light);
        this.getEnvironment().getLightManager().addLight(light);
    }

    public void removeLightFromById(ILightsKeeper keepLights, int i) {
        if (keepLights == null) {
            JGemsHelper.getLogger().error("Couldn't attach light. Invalid entity!");
            return;
        }
        keepLights.removeLightById(i);
    }

    public void removeLightFrom(ILightsKeeper keepLights, Light light) {
        if (keepLights == null) {
            JGemsHelper.getLogger().error("Couldn't attach light. Invalid entity!");
            return;
        }
        keepLights.removeLight(light);
    }

    public void removeLightFrom(WorldItem worldItem, Light light) {
        AbstractSceneEntity abstractSceneEntity = this.getObjectMap().get(worldItem.getItemId());
        if (abstractSceneEntity == null) {
            JGemsHelper.getLogger().error("Couldn't attach light. Invalid entity!");
            return;
        }
        abstractSceneEntity.removeLight(light);
    }

    public void removeLightFromById(WorldItem worldItem, int i) {
        AbstractSceneEntity abstractSceneEntity = this.getObjectMap().get(worldItem.getItemId());
        if (abstractSceneEntity == null) {
            JGemsHelper.getLogger().error("Couldn't attach light. Invalid entity!");
            return;
        }
        abstractSceneEntity.removeLightById(i);
    }

    public void addObjectInWorld(IModeledSceneObject renderObject) {
        this.getModeledSceneEntities().add(renderObject);
    }

    public void removeObjectFromWorld(IModeledSceneObject renderObject) {
        if (!this.getModeledSceneEntities().remove(renderObject)) {
            JGemsHelper.getLogger().warn("Couldn't remove a render object from scene rendering!");
        }
    }

    public void addEntityInWorld(AbstractSceneEntity abstractSceneEntity) {
        this.getObjectMap().put(abstractSceneEntity.getWorldItem().getItemId(), abstractSceneEntity);
        abstractSceneEntity.onSpawn(this);
        this.addObjectInWorld(abstractSceneEntity);
    }

    public void removeEntityFromWorld(AbstractSceneEntity abstractSceneEntity) {
        this.getObjectMap().remove(abstractSceneEntity.getWorldItem().getItemId());
        abstractSceneEntity.onDestroy(this);
        this.removeObjectFromWorld(abstractSceneEntity);
    }

    public void addLiquid(Liquid liquid, RenderLiquidData renderLiquidData) {
        this.getLiquids().add(new LiquidObject(liquid, renderLiquidData));
    }

    public void removeLiquid(LiquidObject liquid) {
        liquid.getModel().clean();
        this.getLiquids().remove(liquid);
    }

    public Environment getEnvironment() {
        synchronized (this) {
            return this.environment;
        }
    }

    public ParticlesEmitter getParticlesEmitter() {
        return this.particlesEmitter;
    }

    private Map<Integer, AbstractSceneEntity> getObjectMap() {
        return this.objectMap;
    }

    public Set<LiquidObject> getLiquids() {
        return this.liquids;
    }

    public Set<IModeledSceneObject> getModeledSceneEntities() {
        return this.toRenderSet;
    }

    public int getTicks() {
        return this.ticks;
    }

    public FrustumCulling getFrustumCulling() {
        return this.frustumCulling;
    }

    public void setFrustumCulling(FrustumCulling frustumCulling) {
        this.frustumCulling = frustumCulling;
    }
}
