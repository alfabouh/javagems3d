package ru.alfabouh.jgems3d.engine.graphics.opengl.world;

import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.graphics.opengl.camera.AttachedCamera;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.IKeepLights;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import ru.alfabouh.jgems3d.engine.math.Pair;
import ru.alfabouh.jgems3d.engine.physics.world.basic.IWorldTicked;
import ru.alfabouh.jgems3d.engine.physics.world.triggers.liquids.base.Liquid;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.physics.world.basic.WorldItem;
import ru.alfabouh.jgems3d.engine.graphics.opengl.environment.Environment;
import ru.alfabouh.jgems3d.engine.graphics.opengl.environment.light.Light;
import ru.alfabouh.jgems3d.engine.graphics.opengl.frustum.FrustumCulling;
import ru.alfabouh.jgems3d.engine.graphics.opengl.frustum.ICulled;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderLiquidData;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderObjectData;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.IModeledSceneObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.objects.LiquidObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.camera.ICamera;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;
import ru.alfabouh.jgems3d.logger.SystemLogging;

import java.util.*;
import java.util.stream.Collectors;

public final class SceneWorld implements IWorld {
    private Set<Pair<WorldItem, Light>> lightAttachmentQueue;

    private final Map<Integer, AbstractSceneEntity> objectMap;
    private final Set<IModeledSceneObject> toRenderSet;
    private final Set<LiquidObject> liquids;
    private final Environment environment;
    private final World world;
    private FrustumCulling frustumCulling;
    private int ticks;

    public SceneWorld(World world) {
        this.lightAttachmentQueue = new HashSet<>();

        this.objectMap = new HashMap<>();
        this.liquids = new HashSet<>();
        this.toRenderSet = new HashSet<>();

        this.world = world;
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

    public Collection<? extends ICulled> getEntitiesFrustumCulledList(Collection<? extends ICulled> list) {
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

    public AttachedCamera createAttachedCamera(WorldItem worldItem) {
        AbstractSceneEntity abstractSceneEntity = this.getObjectMap().get(worldItem.getItemId());
        if (abstractSceneEntity == null) {
            SystemLogging.get().getLogManager().warn("Couldn't attach camera on " + worldItem + ". SceneEntity doesn't exist");
            return null;
        }
        return new AttachedCamera(abstractSceneEntity);
    }

    public boolean attachCameraOn(WorldItem worldItem, AttachedCamera attachedCamera) {
        AbstractSceneEntity abstractSceneEntity = this.getObjectMap().get(worldItem.getItemId());
        if (abstractSceneEntity == null) {
            SystemLogging.get().getLogManager().warn("Couldn't attach camera on " + worldItem + ". SceneEntity doesn't exist");
            return false;
        }
        attachedCamera.attachCameraOnItem(abstractSceneEntity);
        return true;
    }

    public void addItem(WorldItem worldItem, RenderObjectData renderData) throws JGemsException {
        if (renderData == null) {
            throw new JGemsException("Wrong render parameters: " + worldItem.toString());
        }
        AbstractSceneEntity abstractSceneEntity = renderData.constructPhysicsObject(this, worldItem);
        this.addEntityInWorld(abstractSceneEntity);
    }

    public void removeLight(Light light) {
        this.getEnvironment().getLightManager().removeLight(light);
    }

    public void addLight(Light light) {
        this.getEnvironment().getLightManager().addLight(light);
    }

    public void addItemLight(IKeepLights keepLights, Light light) {
        keepLights.addLight(light);
        this.getEnvironment().getLightManager().addLight(light);
    }

    public void addWorldItemLight(WorldItem worldItem, Light light) {
        if (!worldItem.isSpawned()) {
            SystemLogging.get().getLogManager().error("Couldn't attach light. Entity hasn't been spawned!");
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

    public void removeLightFromById(IKeepLights keepLights, int i) {
        if (keepLights == null) {
            SystemLogging.get().getLogManager().error("Couldn't attach light. Invalid entity!");
            return;
        }
        keepLights.removeLightById(i);
    }

    public void removeLightFrom(IKeepLights keepLights, Light light) {
        if (keepLights == null) {
            SystemLogging.get().getLogManager().error("Couldn't attach light. Invalid entity!");
            return;
        }
        keepLights.removeLight(light);
    }

    public void removeLightFrom(WorldItem worldItem, Light light) {
        AbstractSceneEntity abstractSceneEntity = this.getObjectMap().get(worldItem.getItemId());
        if (abstractSceneEntity == null) {
            SystemLogging.get().getLogManager().error("Couldn't attach light. Invalid entity!");
            return;
        }
        abstractSceneEntity.removeLight(light);
    }

    public void removeLightFromById(WorldItem worldItem, int i) {
        AbstractSceneEntity abstractSceneEntity = this.getObjectMap().get(worldItem.getItemId());
        if (abstractSceneEntity == null) {
            SystemLogging.get().getLogManager().error("Couldn't attach light. Invalid entity!");
            return;
        }
        abstractSceneEntity.removeLightById(i);
    }

    public void addObjectInWorld(IModeledSceneObject renderObject) {
        this.getModeledSceneEntities().add(renderObject);
    }

    public void removeObjectFromWorld(IModeledSceneObject renderObject) {
        if (!this.getModeledSceneEntities().remove(renderObject)) {
            SystemLogging.get().getLogManager().warn("Couldn't remove a render object from scene rendering!");
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

    @Override
    public void onWorldStart() {
        //JGems.get().getScreen().zeroRenderTick();
        this.getEnvironment().init(this);
        this.ticks = 0;
    }

    @Override
    public void onWorldUpdate() {
        Iterator<Pair<WorldItem, Light>> iterator = this.lightAttachmentQueue.iterator();
        while (iterator.hasNext()) {
            Pair<WorldItem, Light> pair = iterator.next();
            this.addWorldItemLight(pair.getFirst(), pair.getSecond());
            iterator.remove();
        }
        this.ticks += 1;
    }

    @Override
    public void onWorldEnd() {
        this.cleanAll();
    }

    public void updateWorldObjects(boolean refresh, float partialTicks) {
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
                abstractSceneEntity.updateRenderPos(partialTicks);
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

    private void cleanAll() {
        this.getEnvironment().getLightManager().removeAllLights();

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

    public void setFrustumCulling(FrustumCulling frustumCulling) {
        this.frustumCulling = frustumCulling;
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

    public Environment getEnvironment() {
        return this.environment;
    }

    public World getWorld() {
        return this.world;
    }
}
