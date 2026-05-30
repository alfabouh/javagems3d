package javagems3d.graphics.world;

import api.events.EventBus;
import api.events.EventLauncher;
import api.scripting.coding.env.internal.game.init.events.camera.JSSceneCameraEvent;
import api.scripting.coding.env.internal.game.init.events.rendering.world.*;
import api.scripting.coding.env.internal.util.events.JSEventRun;
import api.scripting.coding.env.internal.util.events.JSEventState;
import api.scripting.coding.env.internal.util.misc.JSFrameTicking;
import api.scripting.coding.env.internal.util.world.physical.zones.instances.JSLiquid;
import api.scripting.coding.env.internal.util.world.render.data.JSEntityRenderData;
import api.scripting.coding.env.internal.util.world.render.data.JSLiquidRenderData;
import api.scripting.coding.env.internal.util.world.render.screen.camera.JSCamera;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import api.scripting.coding.env.internal.util.world.render.world.instances.JSSceneWorldLiquid;
import api.scripting.JavaToJsAPI;
import javagems3d.JGems3D;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.lights.ILightAttachable;
import javagems3d.graphics.objects.IObjectWithLights;
import javagems3d.graphics.screen.timer.TimerPool;
import javagems3d.help.JGemsHelper;
import javagems3d.system.core.transmitter.ThreadActionsTransmitter;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.camera.AttachedCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.JGemsEnvironment;
import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.graphics.objects.entities.world.SceneWorldLiquid;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.LiquidRenderData;
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

    private final Set<Pair<WorldItem, ILightAttachable>> lightAttachmentQueue;
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
    }

    //section WorldStart
    @Override
    public void onWorldStart() {
        EventLauncher.pushEvent(new EventBus.SceneWorldLifecycleEvent(this, EventBus.State.START), new Pair<>(new JSSceneWorldLifecycleEvent(new JSSceneWorld(this), JSEventState.START), JavaToJsAPI.Target.Game));
        JGemsConfig.DEBUG.reset();
        JGems3D.get().getScreen().zeroRenderTick();
        this.getEnvironment().getSkyBox().createSkyBox(this);
        this.ticks = 0;
    }

    //section WorldUpdate
    @Override
    public void onWorldUpdate() {
        ThreadActionsTransmitter.INSTANCE.getActions__PHYSICS_TO_RENDER().forEach(
                e -> e.action(this)
        );
        ThreadActionsTransmitter.INSTANCE.getActions__PHYSICS_TO_RENDER().clear();

        EventBus.SceneWorldUpdateEvent pre = new EventBus.SceneWorldUpdateEvent(this, EventBus.Run.PRE, this.ticks);
        EventLauncher.pushEvent(pre, new Pair<>(new JSSceneWorldUpdateEvent(new JSSceneWorld(this), JSEventRun.PRE, this.ticks), JavaToJsAPI.Target.Game));
        if (pre.isCancelled()) {
            return;
        }

        Iterator<Pair<WorldItem, ILightAttachable>> iterator = this.lightAttachmentQueue.iterator();
        while (iterator.hasNext()) {
            Pair<WorldItem, ILightAttachable> pair = iterator.next();
            this.addWorldItemLight(pair.first(), pair.second());
            iterator.remove();
        }

        this.ticks += 1;
        EventLauncher.pushEvent(new EventBus.SceneWorldUpdateEvent(this, EventBus.Run.POST, this.ticks), new Pair<>(new JSSceneWorldUpdateEvent(new JSSceneWorld(this), JSEventRun.POST, this.ticks), JavaToJsAPI.Target.Game));
    }

    //section WorldEnd
    @Override
    public void onWorldEnd() {
        EventLauncher.pushEvent(new EventBus.SceneWorldLifecycleEvent(this, EventBus.State.END), new Pair<>(new JSSceneWorldLifecycleEvent(new JSSceneWorld(this), JSEventState.END), JavaToJsAPI.Target.Game));
        this.getEnvironment().destroyEnvironment();
        this.getEnvironment().getSkyBox().destroySkyBox(this);
        this.getEnvironment().getParticlesScene().getParticlesManager().clear();
        this.getEnvironment().getDecalsScene().clear();
        ((JGemsEnvironment) this.getEnvironment()).clearLightsBuffer();
        ((JGemsEnvironment) this.getEnvironment()).clearLightsBuffer();
        this.clearAll();
    }

    public void updateWorldObjects(boolean refresh, FrameTicking frameTicking) {
        EventBus.SceneWorldObjectsUpdateEvent pre = new EventBus.SceneWorldObjectsUpdateEvent(this, refresh, frameTicking, EventBus.Run.PRE);
        EventLauncher.pushEvent(pre, new Pair<>(new JSSceneWorldObjectsUpdateEvent(new JSSceneWorld(this), refresh, new JSFrameTicking(frameTicking), JSEventRun.PRE), JavaToJsAPI.Target.Game));
        if (pre.isCancelled()) {
            return;
        }
        Iterator<SceneObject> iterator = this.getSceneObjects().iterator();
        while (iterator.hasNext()) {
            SceneObject sceneObject = iterator.next();
            if (sceneObject.isDead()) {
                EventBus.SceneObjectDestroyEvent destroyEvent = new EventBus.SceneObjectDestroyEvent(this, sceneObject);
                EventLauncher.pushEvent(destroyEvent, new Pair<>(new JSSceneObjectDestroyEvent(new JSSceneWorld(this), () -> sceneObject), JavaToJsAPI.Target.Game));
                if (destroyEvent.isCancelled()) {
                    continue;
                }
                if (sceneObject instanceof SceneEntity e) {
                    this.getObjectMap().remove(e.getWorldItem().getItemId());
                }
                sceneObject.onDestroy(this);
                iterator.remove();
                continue;
            }

            EventLauncher.pushEvent(new EventBus.SceneObjectUpdateEvent(this, sceneObject), new Pair<>(new JSSceneObjectUpdateEvent(new JSSceneWorld(this), () -> sceneObject), JavaToJsAPI.Target.Game));
            sceneObject.updateAnimation();
            if (sceneObject instanceof IWorldTicked worldTicked) {
                worldTicked.onUpdate(this);
            }
            if (sceneObject instanceof SceneEntity e) {
                if (refresh) {
                    e.refreshInterpolatingState();
                }
                e.updateRenderPos(frameTicking.physicsSyncTicks());
                e.updateModelTranslation();
            }
        }

        this.getLiquids().removeIf(liquid -> {
            if (liquid.isDead()) {
                EventLauncher.pushEvent(new EventBus.SceneLiquidDestroyEvent(this, liquid), new Pair<>(new JSSceneLiquidDestroyEvent(new JSSceneWorld(this), new JSSceneWorldLiquid(liquid)), JavaToJsAPI.Target.Game));
                liquid.onDestroy(this);
                return true;
            }
            return false;
        });

        EventLauncher.pushEvent(new EventBus.SceneWorldObjectsUpdateEvent(this, refresh, frameTicking, EventBus.Run.POST), new Pair<>(new JSSceneWorldObjectsUpdateEvent(new JSSceneWorld(this), refresh, new JSFrameTicking(frameTicking), JSEventRun.POST), JavaToJsAPI.Target.Game));
    }

    //section WorldClean
    public void clearAll() {
        Iterator<SceneObject> iterator = this.getSceneObjects().iterator();
        while (iterator.hasNext()) {
            SceneObject modeledSceneObject = iterator.next();
            iterator.remove();
            if (modeledSceneObject instanceof SceneEntity abstractSceneEntity) {
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

    public SceneObject addWorldObject(WorldItem worldItem, EntityRenderData renderData) throws JGemsException {
        final SceneObject sceneObject = renderData.constructSceneObject(this, worldItem);
        this.addObject(sceneObject);
        return sceneObject;
    }

    public void removeLight(Light light) {
        EventBus.SceneLightDestroyEvent event = new EventBus.SceneLightDestroyEvent(this, light);
        EventLauncher.pushEvent(event, new Pair<>(new JSSceneLightDestroyEvent(new JSSceneWorld(this), () -> light), JavaToJsAPI.Target.Game));
        if (event.isCancelled()) {
            return;
        }
        this.getEnvironment().getLightScene().removeLight(light);
        ILightAttachable l = (ILightAttachable) light;
        if (l.getAttachedTo() != null) {
            l.getAttachedTo().removeLightAttachment(l);
        }
    }

    public void addLight(Light light, @Nullable IObjectWithLights lighted) {
        EventBus.SceneLightSpawnEvent event = new EventBus.SceneLightSpawnEvent(this, light);
        EventLauncher.pushEvent(event, new Pair<>(new JSSceneLightSpawnEvent(new JSSceneWorld(this), () -> light), JavaToJsAPI.Target.Game));
        if (event.isCancelled()) {
            return;
        }

        this.getEnvironment().getLightScene().addLight(light);
        if (lighted != null) {
            lighted.addLightAttachment((ILightAttachable) light);
        }
    }

    public void addWorldItemLight(WorldItem worldItem, ILightAttachable light) {
        SceneEntity abstractSceneEntity = this.getObjectMap().get(worldItem.getItemId());
        if (abstractSceneEntity == null) {
            this.lightAttachmentQueue.add(new Pair<>(worldItem, light));
            return;
        }
        abstractSceneEntity.addLightAttachment(light);
        this.getEnvironment().getLightScene().addLight((Light) light);
    }

    public void addObject(SceneObject sceneObject, EntityRenderData renderData) {
        EventBus.SceneObjectSpawnEvent event = new EventBus.SceneObjectSpawnEvent(this, sceneObject, renderData);
        EventLauncher.pushEvent(event, new Pair<>(new JSSceneObjectSpawnEvent(new JSSceneWorld(this), () -> sceneObject, new JSEntityRenderData(renderData)), JavaToJsAPI.Target.Game));
        if (event.isCancelled()) {
            return;
        }

        this.getSceneObjects().add(sceneObject);
        sceneObject.onSpawn(this);

        if (sceneObject instanceof SceneEntity e) {
            this.getObjectMap().put(e.getWorldItem().getItemId(), e);
        }
    }

    public void removeObject(SceneObject sceneObject) {
        EventBus.SceneObjectDestroyEvent event = new EventBus.SceneObjectDestroyEvent(this, sceneObject);
        EventLauncher.pushEvent(event, new Pair<>(new JSSceneObjectDestroyEvent(new JSSceneWorld(this), () -> sceneObject), JavaToJsAPI.Target.Game));
        if (event.isCancelled()) {
            return;
        }

        this.getSceneObjects().remove(sceneObject);
        sceneObject.onDestroy(this);

        if (sceneObject instanceof SceneEntity e) {
            this.getObjectMap().remove(e.getWorldItem().getItemId());
        }
    }

    @Override
    public void addObject(SceneObject sceneObject) {
        this.getSceneObjects().add(sceneObject);
        sceneObject.onSpawn(this);

        if (sceneObject instanceof SceneEntity sceneEntity) {
            this.getObjectMap().put(sceneEntity.getWorldItem().getItemId(), sceneEntity);
        }
    }

    @Override
    public TimerPool getTimerPool() {
        return JGemsHelper.screen().getTimerPool();
    }

    public void addLiquid(Liquid liquid, LiquidRenderData liquidRenderData) {
        EventBus.SceneLiquidSpawnEvent event = new EventBus.SceneLiquidSpawnEvent(this, liquid, liquidRenderData);
        EventLauncher.pushEvent(event, new Pair<>(new JSSceneLiquidSpawnEvent(new JSSceneWorld(this), new JSLiquid(liquid), new JSLiquidRenderData(liquidRenderData)), JavaToJsAPI.Target.Game));
        if (event.isCancelled()) { return; }

        SceneWorldLiquid sceneWorldLiquid = new SceneWorldLiquid(liquid, liquidRenderData);
        sceneWorldLiquid.onSpawn(this);
        this.getLiquids().add(sceneWorldLiquid);
    }

    public void removeLiquid(SceneWorldLiquid liquid) {
        EventBus.SceneLiquidDestroyEvent event = new EventBus.SceneLiquidDestroyEvent(this, liquid);
        EventLauncher.pushEvent(event, new Pair<>(new JSSceneLiquidDestroyEvent(new JSSceneWorld(this), new JSSceneWorldLiquid(liquid)), JavaToJsAPI.Target.Game));
        if (event.isCancelled()) {
            return;
        }

        liquid.onDestroy(this);
        this.getLiquids().remove(liquid);
    }

    public void setCamera(ICamera camera) {
        this.camera = camera;
        EventLauncher.pushEvent(new EventBus.SceneCameraEvent(this, camera), new Pair<>(new JSSceneCameraEvent(new JSSceneWorld(this), new JSCamera(camera)), JavaToJsAPI.Target.Game));
    }

    public IEnvironment getEnvironment() {
        synchronized (this) {
            return this.environment;
        }
    }

    public boolean contains(SceneObject sceneObject) {
        return this.getSceneObjects().contains(sceneObject);
    }

    public ICamera getCamera() {
        return this.camera;
    }

    public SceneObject getSceneObject(WorldItem worldItem) {
        return this.getObjectMap().get(worldItem.getItemId());
    }

    public SceneObject getSceneObjectByWorldItemId(int id) {
        return this.getObjectMap().get(id);
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
