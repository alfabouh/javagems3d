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

package javagems3d.graphics.opengl.world;

import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import api.bridge.events.APIEventsLauncher;
import javagems3d.graphics.opengl.camera.AttachedCamera;
import javagems3d.graphics.opengl.camera.ICamera;
import javagems3d.graphics.opengl.environment.Environment;
import javagems3d.graphics.opengl.environment.light.Light;
import javagems3d.graphics.opengl.frustum.ICulled;
import javagems3d.graphics.opengl.particles.ParticlesEmitter;
import javagems3d.graphics.opengl.rendering.JGemsDebugGlobalConstants;
import javagems3d.graphics.opengl.rendering.fabric.objects.data.RenderEntityData;
import javagems3d.graphics.opengl.rendering.fabric.objects.data.RenderLiquidData;
import javagems3d.graphics.opengl.rendering.items.IAnimated;
import javagems3d.graphics.opengl.rendering.items.ILightsKeeper;
import javagems3d.graphics.opengl.rendering.items.AbstractSceneObject;
import javagems3d.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import javagems3d.graphics.opengl.rendering.items.objects.LiquidObject;
import javagems3d.graphics.opengl.rendering.scene.tick.FrameTicking;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.basic.IWorldTicked;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.triggers.liquids.base.Liquid;
import javagems3d.system.resources.assets.shaders.RenderPass;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsException;
import javagems3d.system.service.synchronizing.SyncManager;
import api.app.events.bus.Events;

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
    private final Set<AbstractSceneObject> toRenderSet;
    private final Set<LiquidObject> liquids;
    private final Environment environment;
    private int ticks;

    public SceneWorld() {
        this.lightAttachmentQueue = SyncManager.createSyncronisedSet();
        this.objectMap = SyncManager.createSyncronisedMap();
        this.liquids = SyncManager.createSyncronisedSet();
        this.toRenderSet = SyncManager.createSyncronisedSet();

        this.environment = new Environment();

        this.particlesEmitter = new ParticlesEmitter();
    }

    //section WorldStart
    @Override
    public void onWorldStart() {
        APIEventsLauncher.pushEvent(new Events.RenderWorldStart(Events.Stage.PRE, this));
        JGemsDebugGlobalConstants.reset();
        JGems3D.get().getScreen().zeroRenderTick();
        this.getParticlesEmitter().create(this);
        this.getEnvironment().createEnvironment(this);
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
        this.getEnvironment().destroyEnvironment(this);
        this.clearAll();
        APIEventsLauncher.pushEvent(new Events.RenderWorldEnd(Events.Stage.POST, this));
    }

    //section WorldUpdObj
    public void updateWorldObjects(boolean refresh, FrameTicking frameTicking) {
        this.getParticlesEmitter().onUpdateParticles(frameTicking.getFrameDeltaTime(), this);

        Iterator<AbstractSceneObject> iterator = this.getModeledSceneEntities().iterator();
        while (iterator.hasNext()) {
            AbstractSceneObject sceneObject = iterator.next();
            sceneObject.updateAnimation();
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
                abstractSceneEntity.updateModelTranslation();
            }
        }

        Iterator<LiquidObject> iterator2 = this.getLiquids().iterator();
        while (iterator2.hasNext()) {
            LiquidObject liquidObject = iterator2.next();
            if (liquidObject.getLiquid().isDead()) {
                liquidObject.getModel().clear();
                iterator2.remove();
            }
        }
    }

    //section WorldClean
    private void clearAll() {
        Iterator<AbstractSceneObject> iterator = this.getModeledSceneEntities().iterator();
        while (iterator.hasNext()) {
            AbstractSceneObject modeledSceneObject = iterator.next();
            if (modeledSceneObject instanceof AbstractSceneEntity) {
                AbstractSceneEntity abstractSceneEntity = (AbstractSceneEntity) modeledSceneObject;
                abstractSceneEntity.onDestroy(this);
            }
            iterator.remove();
        }

        Iterator<LiquidObject> iterator1 = this.getLiquids().iterator();
        while (iterator1.hasNext()) {
            LiquidObject liquidObject = iterator1.next();
            liquidObject.getModel().clear();
            iterator1.remove();
        }
        this.getObjectMap().clear();
    }

    public boolean checkReachedRenderDistance(AbstractSceneObject renderObject) {
        if (!renderObject.hasRender()) {
            return true;
        }
        ICamera camera = JGems3D.get().getScreen().getCamera();
        return renderObject.getObjectRenderSettings().getRenderDistance() >= 0 && camera.getCamPosition().distance(renderObject.getModel().getFormat().getPosition()) > renderObject.getObjectRenderSettings().getRenderDistance();
    }

    public Set<AbstractSceneObject> getFilteredEntitySet(RenderPass renderPass) {
        return this.getModeledSceneEntities().stream().filter(e -> (renderPass == null || e.getObjectRenderSettings().getModelRenderShader().checkShaderRenderPass(renderPass)) && e.isVisible() && !this.checkReachedRenderDistance(e)).collect(Collectors.toSet());
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

    public void addObjectInWorld(AbstractSceneObject renderObject) {
        this.getModeledSceneEntities().add(renderObject);
    }

    public void removeObjectFromWorld(AbstractSceneObject renderObject) {
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
        liquid.getModel().clear();
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

    public boolean ifObjectHasAnimations(WorldItem worldItem) {
        return this.getObjectMap().get(worldItem.getItemId()).hasAnimations();
    }

    public IAnimated getAnimatedObject(WorldItem worldItem) {
        return this.getObjectMap().get(worldItem.getItemId());
    }

    private Map<Integer, AbstractSceneEntity> getObjectMap() {
        return this.objectMap;
    }

    public Set<LiquidObject> getLiquids() {
        return this.liquids;
    }

    public Set<AbstractSceneObject> getModeledSceneEntities() {
        return this.toRenderSet;
    }

    public int getTicks() {
        return this.ticks;
    }
}
