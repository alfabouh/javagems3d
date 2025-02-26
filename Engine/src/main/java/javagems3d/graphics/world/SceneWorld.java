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

package javagems3d.graphics.world;

import api.events.EventBus;
import api.events.EventLauncher;
import javagems3d.JGems3D;
import javagems3d.system.global.JGemsConfiguration;
import javagems3d.graphics.camera.AttachedCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.JGemsEnvironment;
import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.objects.IAnimated;
import javagems3d.graphics.objects.ILighted;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.graphics.objects.entities.world.SceneWorldLiquid;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.LiquidRenderData;
import javagems3d.graphics.particles.ParticlesEmitter;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.basic.IWorldTicked;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.triggers.liquids.base.Liquid;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsException;
import javagems3d.system.service.synchronizing.SyncManager;
import logger.Log;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class SceneWorld implements IWorld {
    private ICamera camera;

    private final ParticlesEmitter particlesEmitter;
    private final JGemsEnvironment environment;

    private final Set<Pair<WorldItem, Light>> lightAttachmentQueue;
    private final Map<Integer, SceneEntity> objectMap;

    private final Set<SceneObject> toRenderSet;
    private final Set<SceneWorldLiquid> liquids;

    private int ticks;

    public SceneWorld() {
        this.camera = null;

        this.lightAttachmentQueue = SyncManager.createSyncronisedSet();
        this.objectMap = SyncManager.createSyncronisedMap();
        this.liquids = SyncManager.createSyncronisedSet();
        this.toRenderSet = SyncManager.createSyncronisedSet();

        this.environment = new JGemsEnvironment(this);

        this.particlesEmitter = new ParticlesEmitter();
    }

    //section WorldStart
    @Override
    public void onWorldStart() {
        EventLauncher.pushEvent(new EventBus.RenderWorldStart(EventBus.Run.PRE, this));
        JGemsConfiguration.DEBUG.reset();
        JGems3D.get().getScreen().zeroRenderTick();
        this.getParticlesEmitter().create(this);
        this.ticks = 0;
        EventLauncher.pushEvent(new EventBus.RenderWorldStart(EventBus.Run.POST, this));
    }

    //section WorldUpdate
    @Override
    public void onWorldUpdate() {
        if (!EventLauncher.pushEvent(new EventBus.RenderWorldTickPre(this)).isCancelled()) {
            Iterator<Pair<WorldItem, Light>> iterator = this.lightAttachmentQueue.iterator();
            while (iterator.hasNext()) {
                Pair<WorldItem, Light> pair = iterator.next();
                this.addWorldItemLight(pair.getFirst(), pair.getSecond());
                iterator.remove();
            }
            this.ticks += 1;
        }
        EventLauncher.pushEvent(new EventBus.RenderWorldTickPost(this));
    }

    //section WorldEnd
    @Override
    public void onWorldEnd() {
        EventLauncher.pushEvent(new EventBus.RenderWorldEnd(EventBus.Run.PRE, this));
        if (this.getParticlesEmitter() != null) {
            this.getParticlesEmitter().destroy(this);
        }
        this.clearAll();
        EventLauncher.pushEvent(new EventBus.RenderWorldEnd(EventBus.Run.POST, this));
    }

    //section WorldUpdObj
    public void updateWorldObjects(boolean refresh, FrameTicking frameTicking) {
        this.getParticlesEmitter().onUpdateParticles(frameTicking.getFrameDeltaTime(), this);

        Iterator<SceneObject> iterator = this.getSceneObjects().iterator();
        while (iterator.hasNext()) {
            SceneObject sceneObject = iterator.next();
            sceneObject.updateAnimation();
            if (sceneObject instanceof IWorldTicked) {
                IWorldTicked worldTicked = (IWorldTicked) sceneObject;
                worldTicked.onUpdate(this);
            }
            if (sceneObject instanceof SceneEntity) {
                SceneEntity abstractSceneEntity = (SceneEntity) sceneObject;
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

        Iterator<SceneWorldLiquid> iterator2 = this.getLiquids().iterator();
        while (iterator2.hasNext()) {
            SceneWorldLiquid sceneWorldLiquid = iterator2.next();
            if (sceneWorldLiquid.getLiquid().isDead()) {
                sceneWorldLiquid.getModel().clear();
                iterator2.remove();
            }
        }
    }

    //section WorldClean
    private void clearAll() {
        Iterator<SceneObject> iterator = this.getSceneObjects().iterator();
        while (iterator.hasNext()) {
            SceneObject modeledSceneObject = iterator.next();
            if (modeledSceneObject instanceof SceneEntity) {
                SceneEntity abstractSceneEntity = (SceneEntity) modeledSceneObject;
                abstractSceneEntity.onDestroy(this);
            }
            iterator.remove();
        }

        Iterator<SceneWorldLiquid> iterator1 = this.getLiquids().iterator();
        while (iterator1.hasNext()) {
            SceneWorldLiquid sceneWorldLiquid = iterator1.next();
            sceneWorldLiquid.getModel().clear();
            iterator1.remove();
        }
        this.getObjectMap().clear();
    }

    public AttachedCamera createAttachedCamera(SceneEntity abstractSceneEntity) {
        return new AttachedCamera(abstractSceneEntity);
    }

    public AttachedCamera createAttachedCamera(WorldItem worldItem) {
        SceneEntity abstractSceneEntity = this.getObjectMap().get(worldItem.getItemId());
        if (abstractSceneEntity == null) {
            Log.get().warn("Couldn't attach camera on " + worldItem + ". SceneEntity doesn't exist");
            return null;
        }
        return new AttachedCamera(abstractSceneEntity);
    }

    public boolean attachCameraOn(WorldItem worldItem, AttachedCamera attachedCamera) {
        SceneEntity abstractSceneEntity = this.getObjectMap().get(worldItem.getItemId());
        if (abstractSceneEntity == null) {
            Log.get().warn("Couldn't attach camera on " + worldItem + ". SceneEntity doesn't exist");
            return false;
        }
        attachedCamera.attachCameraOnItem(abstractSceneEntity);
        return true;
    }

    public void addItem(WorldItem worldItem, EntityRenderData renderData) throws JGemsException {
        SceneEntity abstractSceneEntity = renderData.constructSceneObject(this, worldItem);
        this.addEntityInWorld(abstractSceneEntity);
    }

    public void removeLight(Light light) {
        this.getEnvironment().getLightManager().removeLight(light);
    }

    public void addLight(Light light) {
        this.getEnvironment().getLightManager().addLight(light);
    }

    public void addItemLight(ILighted keepLights, Light light) {
        keepLights.addLight(light);
        this.getEnvironment().getLightManager().addLight(light);
    }

    public void addWorldItemLight(WorldItem worldItem, Light light) {
        if (!worldItem.isSpawned()) {
            Log.get().error("Couldn't attach light. Entity hasn't been spawned");
            return;
        }
        SceneEntity abstractSceneEntity = this.getObjectMap().get(worldItem.getItemId());
        if (abstractSceneEntity == null) {
            this.lightAttachmentQueue.add(new Pair<>(worldItem, light));
            return;
        }
        abstractSceneEntity.addLight(light);
        this.getEnvironment().getLightManager().addLight(light);
    }

    public void removeLightFromById(ILighted keepLights, int i) {
        if (keepLights == null) {
            Log.get().error("Couldn't attach light. Invalid entity");
            return;
        }
        keepLights.removeLightById(i);
    }

    public void removeLightFrom(ILighted keepLights, Light light) {
        if (keepLights == null) {
            Log.get().error("Couldn't attach light. Invalid entity");
            return;
        }
        keepLights.removeLight(light);
    }

    public void removeLightFrom(WorldItem worldItem, Light light) {
        SceneEntity abstractSceneEntity = this.getObjectMap().get(worldItem.getItemId());
        if (abstractSceneEntity == null) {
            Log.get().error("Couldn't attach light. Invalid entity");
            return;
        }
        abstractSceneEntity.removeLight(light);
    }

    public void removeLightFromById(WorldItem worldItem, int i) {
        SceneEntity abstractSceneEntity = this.getObjectMap().get(worldItem.getItemId());
        if (abstractSceneEntity == null) {
            Log.get().error("Couldn't attach light. Invalid entity");
            return;
        }
        abstractSceneEntity.removeLightById(i);
    }

    public void addObjectInWorld(SceneObject renderObject) {
        this.getSceneObjects().add(renderObject);
    }

    public void removeObjectFromWorld(SceneObject renderObject) {
        if (!this.getSceneObjects().remove(renderObject)) {
            Log.get().warn("Couldn't remove a render object from SceneWorld");
        }
    }

    public void addEntityInWorld(SceneEntity abstractSceneEntity) {
        this.getObjectMap().put(abstractSceneEntity.getWorldItem().getItemId(), abstractSceneEntity);
        abstractSceneEntity.onSpawn(this);
        this.addObjectInWorld(abstractSceneEntity);
    }

    public void removeEntityFromWorld(SceneEntity abstractSceneEntity) {
        this.getObjectMap().remove(abstractSceneEntity.getWorldItem().getItemId());
        abstractSceneEntity.onDestroy(this);
        this.removeObjectFromWorld(abstractSceneEntity);
    }

    public void addLiquid(Liquid liquid, LiquidRenderData liquidRenderData) {
        this.getLiquids().add(new SceneWorldLiquid(liquid, liquidRenderData));
    }

    public void removeLiquid(SceneWorldLiquid liquid) {
        liquid.getModel().clear();
        this.getLiquids().remove(liquid);
    }

    public void setCamera(ICamera camera) {
        this.camera = camera;
    }

    public JGemsEnvironment getEnvironment() {
        synchronized (this) {
            return this.environment;
        }
    }

    public ICamera getCamera() {
        return this.camera;
    }

    public ParticlesEmitter getParticlesEmitter() {
        return this.particlesEmitter;
    }

    public IAnimated getAnimatedObject(WorldItem worldItem) {
        return this.getObjectMap().get(worldItem.getItemId());
    }

    private Map<Integer, SceneEntity> getObjectMap() {
        return this.objectMap;
    }

    public Set<SceneWorldLiquid> getLiquids() {
        return this.liquids;
    }

    public Set<SceneObject> getSceneObjects() {
        return this.toRenderSet;
    }

    public int getTicks() {
        return this.ticks;
    }
}
