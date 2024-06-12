package ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.data;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.base.IRenderFabric;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.objects.items.PhysicsObject;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.world.SceneWorld;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.Material;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.basic.constructor.IEntityModelConstructor;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.alfabouh.jgems3d.proxy.exception.JGemsException;

import java.lang.reflect.InvocationTargetException;

public class RenderObjectData {
    private final IRenderFabric renderFabric;
    private final Class<? extends PhysicsObject> aClass;
    private IEntityModelConstructor<WorldItem> entityModelConstructor;
    private MeshDataGroup meshDataGroup;
    private Material overObjectMaterial;
    private ModelRenderParams modelRenderParams;

    public RenderObjectData(IRenderFabric renderFabric, @NotNull Class<? extends PhysicsObject> aClass, @NotNull JGemsShaderManager shaderManager, MeshDataGroup meshDataGroup) {
        this.aClass = aClass;
        this.renderFabric = renderFabric;
        this.overObjectMaterial = null;
        this.entityModelConstructor = null;
        this.meshDataGroup = meshDataGroup;
        this.modelRenderParams = ModelRenderParams.defaultModelRenderConstraints(shaderManager);
    }

    public RenderObjectData(IRenderFabric renderFabric, @NotNull Class<? extends PhysicsObject> aClass, @NotNull ModelRenderParams modelRenderParams, MeshDataGroup meshDataGroup) {
        this.aClass = aClass;
        this.renderFabric = renderFabric;
        this.overObjectMaterial = null;
        this.entityModelConstructor = null;
        this.meshDataGroup = meshDataGroup;
        this.modelRenderParams = modelRenderParams.copy();
    }

    public RenderObjectData(IRenderFabric renderFabric, @NotNull Class<? extends PhysicsObject> aClass, @NotNull JGemsShaderManager shaderManager) {
        this(renderFabric, aClass, shaderManager, null);
    }

    public RenderObjectData(IRenderFabric renderFabric, @NotNull Class<? extends PhysicsObject> aClass, @NotNull ModelRenderParams modelRenderParams) {
        this.aClass = aClass;
        this.renderFabric = renderFabric;
        this.overObjectMaterial = null;
        this.entityModelConstructor = null;
        this.meshDataGroup = null;
        this.modelRenderParams = modelRenderParams.copy();
    }

    public RenderObjectData(@NotNull RenderObjectData renderObjectData, MeshDataGroup meshDataGroup) {
        this(renderObjectData.getRenderFabric(), renderObjectData.getRenderClass(), renderObjectData.getModelRenderParams(), meshDataGroup);
    }

    public PhysicsObject constructPhysicsObject(SceneWorld sceneWorld, WorldItem worldItem) {
        final RenderObjectData renderObjectData = this.copyObject();
        try {
            PhysicsObject physicsObject = this.aClass.getDeclaredConstructor(SceneWorld.class, WorldItem.class, RenderObjectData.class).newInstance(sceneWorld, worldItem, renderObjectData);
            this.onPhysicsObjectCreated(physicsObject);
            return physicsObject;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new JGemsException(e);
        }
    }

    protected void onPhysicsObjectCreated(PhysicsObject physicsObject) {
    }

    public Vector2d getModelTextureScaling() {
        return this.getModelRenderParams().getTextureScaling();
    }

    public RenderObjectData setModelTextureScaling(Vector2d scale) {
        this.getModelRenderParams().setTextureScaling(scale);
        return this;
    }

    public IRenderFabric getRenderFabric() {
        return this.renderFabric;
    }

    public MeshDataGroup getMeshDataGroup() {
        return this.meshDataGroup;
    }

    public RenderObjectData setMeshDataGroup(MeshDataGroup meshDataGroup) {
        this.meshDataGroup = meshDataGroup;
        this.entityModelConstructor = null;
        return this;
    }

    public Material getOverObjectMaterial() {
        return this.overObjectMaterial;
    }

    public RenderObjectData setOverObjectMaterial(Material overObjectMaterial) {
        this.overObjectMaterial = overObjectMaterial;
        return this;
    }

    public IEntityModelConstructor<WorldItem> getEntityModelConstructor() {
        return this.entityModelConstructor;
    }

    public RenderObjectData setEntityModelConstructor(IEntityModelConstructor<WorldItem> entityModelConstructor) {
        this.entityModelConstructor = entityModelConstructor;
        return this;
    }

    public ModelRenderParams getModelRenderParams() {
        return this.modelRenderParams;
    }

    public RenderObjectData setModelRenderParams(ModelRenderParams modelRenderParams) {
        this.modelRenderParams = modelRenderParams;
        return this;
    }

    public Class<? extends PhysicsObject> getRenderClass() {
        return this.aClass;
    }

    protected RenderObjectData copyObject() {
        RenderObjectData renderObjectData = new RenderObjectData(this.getRenderFabric(), this.getRenderClass(), this.getModelRenderParams());
        renderObjectData.setMeshDataGroup(this.getMeshDataGroup());
        renderObjectData.setModelTextureScaling(this.getModelTextureScaling());
        renderObjectData.setOverObjectMaterial(this.getOverObjectMaterial());
        renderObjectData.setEntityModelConstructor(this.getEntityModelConstructor());
        return renderObjectData;
    }
}
