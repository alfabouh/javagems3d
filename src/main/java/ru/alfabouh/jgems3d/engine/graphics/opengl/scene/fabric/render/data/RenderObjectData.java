package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.base.IRenderFabric;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.items.AbstractSceneItemObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.world.SceneWorld;
import ru.alfabouh.jgems3d.engine.system.exception.JGemsException;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.Material;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.basic.constructor.IEntityModelConstructor;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

import java.lang.reflect.InvocationTargetException;

public class RenderObjectData {
    private final IRenderFabric renderFabric;
    private final Class<? extends AbstractSceneItemObject> aClass;
    private IEntityModelConstructor<WorldItem> entityModelConstructor;
    private MeshDataGroup meshDataGroup;
    private Material overObjectMaterial;
    private ModelRenderParams modelRenderParams;

    public RenderObjectData(IRenderFabric renderFabric, @NotNull Class<? extends AbstractSceneItemObject> aClass, @NotNull JGemsShaderManager shaderManager, MeshDataGroup meshDataGroup) {
        this.aClass = aClass;
        this.renderFabric = renderFabric;
        this.overObjectMaterial = null;
        this.entityModelConstructor = null;
        this.meshDataGroup = meshDataGroup;
        this.modelRenderParams = ModelRenderParams.defaultModelRenderConstraints(shaderManager);
    }

    public RenderObjectData(IRenderFabric renderFabric, @NotNull Class<? extends AbstractSceneItemObject> aClass, @NotNull ModelRenderParams modelRenderParams, MeshDataGroup meshDataGroup) {
        this.aClass = aClass;
        this.renderFabric = renderFabric;
        this.overObjectMaterial = null;
        this.entityModelConstructor = null;
        this.meshDataGroup = meshDataGroup;
        this.modelRenderParams = modelRenderParams.copy();
    }

    public RenderObjectData(IRenderFabric renderFabric, @NotNull Class<? extends AbstractSceneItemObject> aClass, @NotNull JGemsShaderManager shaderManager) {
        this(renderFabric, aClass, shaderManager, null);
    }

    public RenderObjectData(IRenderFabric renderFabric, @NotNull Class<? extends AbstractSceneItemObject> aClass, @NotNull ModelRenderParams modelRenderParams) {
        this.aClass = aClass;
        this.renderFabric = renderFabric;
        this.overObjectMaterial = null;
        this.entityModelConstructor = null;
        this.meshDataGroup = null;
        this.modelRenderParams = modelRenderParams.copy();
    }

    public RenderObjectData(@NotNull RenderObjectData renderObjectData, MeshDataGroup meshDataGroup) {
        this(renderObjectData.getRenderFabric(), renderObjectData.getRenderClass(), renderObjectData.getModelRenderParams(), meshDataGroup);
        this.setOverObjectMaterial(renderObjectData.getOverObjectMaterial());
        this.setEntityModelConstructor(renderObjectData.getEntityModelConstructor());
    }

    public RenderObjectData(@NotNull RenderObjectData renderObjectData, Material overObjectMaterial) {
        this(renderObjectData.getRenderFabric(), renderObjectData.getRenderClass(), renderObjectData.getModelRenderParams(), renderObjectData.getMeshDataGroup());
        this.setOverObjectMaterial(overObjectMaterial);
        this.setEntityModelConstructor(renderObjectData.getEntityModelConstructor());
    }

    public AbstractSceneItemObject constructPhysicsObject(SceneWorld sceneWorld, WorldItem worldItem) {
        final RenderObjectData renderObjectData = this.copyObject();
        try {
            AbstractSceneItemObject abstractSceneItemObject = this.aClass.getDeclaredConstructor(SceneWorld.class, WorldItem.class, RenderObjectData.class).newInstance(sceneWorld, worldItem, renderObjectData);
            this.onPhysicsObjectCreated(abstractSceneItemObject);
            return abstractSceneItemObject;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new JGemsException(e);
        }
    }

    protected void onPhysicsObjectCreated(AbstractSceneItemObject abstractSceneItemObject) {
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
        if (entityModelConstructor != null) {
            this.meshDataGroup = null;
        }
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

    public Class<? extends AbstractSceneItemObject> getRenderClass() {
        return this.aClass;
    }

    protected RenderObjectData copyObject() {
        RenderObjectData renderObjectData = new RenderObjectData(this.getRenderFabric(), this.getRenderClass(), this.getModelRenderParams());
        renderObjectData.setMeshDataGroup(this.getMeshDataGroup());
        renderObjectData.setOverObjectMaterial(this.getOverObjectMaterial());
        renderObjectData.setEntityModelConstructor(this.getEntityModelConstructor());
        return renderObjectData;
    }
}
