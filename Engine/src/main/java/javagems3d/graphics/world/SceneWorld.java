package javagems3d.graphics.world;

import api.events.EventBus;
import api.events.EventLauncher;
import javagems3d.JGems3D;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.lights.ILightAttached;
import javagems3d.graphics.objects.ILighted;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.camera.AttachedCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.JGemsEnvironment;
import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.objects.IAnimated;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.graphics.objects.entities.world.SceneWorldLiquid;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.LiquidRenderData;
import javagems3d.graphics.particles.ParticlesEmitter;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.physics.world.basic.IWorldTicked;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.triggers.liquids.base.Liquid;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsException;
import javagems3d.system.service.synchronizing.SyncManager;
import logger.Log;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class SceneWorld implements IRenderWorld {
    private ICamera camera;
    private final IEnvironment environment;

    private final ParticlesEmitter particlesEmitter;
    private final Set<Pair<WorldItem, ILightAttached>> lightAttachmentQueue;
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
        JGemsConfig.DEBUG.reset();
        JGems3D.get().getScreen().zeroRenderTick();
        this.getParticlesEmitter().create(this);
        this.ticks = 0;
        EventLauncher.pushEvent(new EventBus.RenderWorldStart(EventBus.Run.POST, this));
    }

    //section WorldUpdate
    @Override
    public void onWorldUpdate() {
        if (!EventLauncher.pushEvent(new EventBus.RenderWorldTickPre(this)).isCancelled()) {
            Iterator<Pair<WorldItem, ILightAttached>> iterator = this.lightAttachmentQueue.iterator();
            while (iterator.hasNext()) {
                Pair<WorldItem, ILightAttached> pair = iterator.next();
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
        ((JGemsEnvironment) this.getEnvironment()).clearPointLightsBuffer();
        this.clearAll();
        EventLauncher.pushEvent(new EventBus.RenderWorldEnd(EventBus.Run.POST, this));
    }

    public void updateWorldObjects(boolean refresh, FrameTicking frameTicking) {
        this.getParticlesEmitter().onUpdateParticles(frameTicking.getFrameDeltaTime(), this);

        Iterator<SceneObject> iterator = this.getSceneObjects().iterator();
        while (iterator.hasNext()) {
            SceneObject sceneObject = iterator.next();

            if (sceneObject.isDead()) {
                if (sceneObject instanceof SceneEntity) {
                    SceneEntity abstractSceneEntity = (SceneEntity) sceneObject;
                    this.getObjectMap().remove(abstractSceneEntity.getWorldItem().getItemId());
                }
                sceneObject.onDestroy(this);
                iterator.remove();
                continue;
            }

            sceneObject.updateAnimation();

            if (sceneObject instanceof IWorldTicked) {
                IWorldTicked worldTicked = (IWorldTicked) sceneObject;
                worldTicked.onUpdate(this);
            }

            if (sceneObject instanceof SceneEntity) {
                SceneEntity abstractSceneEntity = (SceneEntity) sceneObject;
                if (refresh) {
                    abstractSceneEntity.refreshInterpolatingState();
                }
                abstractSceneEntity.updateRenderPos(frameTicking.getPhysicsSyncTicks());
                abstractSceneEntity.updateModelTranslation();
            }
        }

        this.getLiquids().removeIf(liquid -> {
            if (liquid.getLiquid().isDead()) {
                liquid.getModel().clear();
                return true;
            }
            return false;
        });
    }

    //section WorldClean
    private void clearAll() {
        Iterator<SceneObject> iterator = this.getSceneObjects().iterator();
        while (iterator.hasNext()) {
            SceneObject modeledSceneObject = iterator.next();
            iterator.remove();
            if (modeledSceneObject instanceof SceneEntity) {
                SceneEntity abstractSceneEntity = (SceneEntity) modeledSceneObject;
                abstractSceneEntity.onDestroy(this);
            }
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
        this.addObject(renderData.constructSceneObject(this, worldItem));
    }

    public void removeLight(Light light, @Nullable ILighted lighted) {
        this.getEnvironment().getLightScene().removeLight(light);
        if (lighted != null) {
            lighted.removeLightAttachment((ILightAttached) light);
        }
    }

    public void addLight(Light light, @Nullable ILighted lighted) {
        this.getEnvironment().getLightScene().addLight(light);
        if (lighted != null) {
            lighted.addLightAttachment((ILightAttached) light);
        }
    }

    public void addWorldItemLight(WorldItem worldItem, ILightAttached light) {
        SceneEntity abstractSceneEntity = this.getObjectMap().get(worldItem.getItemId());
        if (abstractSceneEntity == null) {
            this.lightAttachmentQueue.add(new Pair<>(worldItem, light));
            return;
        }
        abstractSceneEntity.addLightAttachment(light);
        this.getEnvironment().getLightScene().addLight((Light) light);
    }

    public void addObject(SceneObject sceneObject) {
        this.getSceneObjects().add(sceneObject);
        sceneObject.onSpawn(this);

        if (sceneObject instanceof SceneEntity) {
            SceneEntity sceneEntity = (SceneEntity) sceneObject;
            this.getObjectMap().put(sceneEntity.getWorldItem().getItemId(), sceneEntity);
        }
    }

    public void removeObject(SceneObject sceneObject) {
        this.getSceneObjects().remove(sceneObject);
        sceneObject.onDestroy(this);

        if (sceneObject instanceof SceneEntity) {
            SceneEntity sceneEntity = (SceneEntity) sceneObject;
            this.getObjectMap().remove(sceneEntity.getWorldItem().getItemId());
        }
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

    public IEnvironment getEnvironment() {
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
