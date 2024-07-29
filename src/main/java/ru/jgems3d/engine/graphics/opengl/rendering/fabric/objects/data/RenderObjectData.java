package ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.system.resources.assets.models.mesh.data.render.MeshRenderData;
import ru.jgems3d.exceptions.JGemsException;
import ru.jgems3d.engine.system.resources.assets.materials.Material;
import ru.jgems3d.engine.system.resources.assets.models.basic.constructor.IEntityModelConstructor;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

import java.lang.reflect.InvocationTargetException;

public class RenderObjectData {
    private final IRenderObjectFabric renderFabric;
    private final Class<? extends AbstractSceneEntity> aClass;
    private IEntityModelConstructor<WorldItem> entityModelConstructor;
    private MeshDataGroup meshDataGroup;
    private Material overObjectMaterial;
    private MeshRenderData meshRenderData;

    public RenderObjectData(IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> aClass, @NotNull JGemsShaderManager shaderManager, MeshDataGroup meshDataGroup) {
        this.aClass = aClass;
        this.renderFabric = renderFabric;
        this.overObjectMaterial = null;
        this.entityModelConstructor = null;
        this.meshDataGroup = meshDataGroup;
        this.meshRenderData = MeshRenderData.defaultModelRenderConstraints(shaderManager);
    }

    public RenderObjectData(IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> aClass, @NotNull MeshRenderData meshRenderData, MeshDataGroup meshDataGroup) {
        this.aClass = aClass;
        this.renderFabric = renderFabric;
        this.overObjectMaterial = null;
        this.entityModelConstructor = null;
        this.meshDataGroup = meshDataGroup;
        this.meshRenderData = meshRenderData.copy();
    }

    public RenderObjectData(IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> aClass, @NotNull JGemsShaderManager shaderManager) {
        this(renderFabric, aClass, shaderManager, null);
    }

    public RenderObjectData(IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> aClass, @NotNull MeshRenderData meshRenderData) {
        this.aClass = aClass;
        this.renderFabric = renderFabric;
        this.overObjectMaterial = null;
        this.entityModelConstructor = null;
        this.meshDataGroup = null;
        this.meshRenderData = meshRenderData.copy();
    }

    public RenderObjectData(@NotNull RenderObjectData renderObjectData, MeshDataGroup meshDataGroup) {
        this(renderObjectData.getRenderFabric(), renderObjectData.getRenderClass(), renderObjectData.getMeshRenderData(), meshDataGroup);
        this.setOverObjectMaterial(renderObjectData.getOverObjectMaterial());
        this.setEntityModelConstructor(renderObjectData.getEntityModelConstructor());
    }

    public RenderObjectData(@NotNull RenderObjectData renderObjectData, Material overObjectMaterial) {
        this(renderObjectData.getRenderFabric(), renderObjectData.getRenderClass(), renderObjectData.getMeshRenderData(), renderObjectData.getMeshDataGroup());
        this.setOverObjectMaterial(overObjectMaterial);
        this.setEntityModelConstructor(renderObjectData.getEntityModelConstructor());
    }

    public AbstractSceneEntity constructPhysicsObject(SceneWorld sceneWorld, WorldItem worldItem) {
        final RenderObjectData renderObjectData = this.copyObject();
        try {
            AbstractSceneEntity abstractSceneEntity = this.aClass.getDeclaredConstructor(SceneWorld.class, WorldItem.class, RenderObjectData.class).newInstance(sceneWorld, worldItem, renderObjectData);
            this.onPhysicsObjectCreated(abstractSceneEntity);
            return abstractSceneEntity;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new JGemsException(e);
        }
    }

    protected void onPhysicsObjectCreated(AbstractSceneEntity abstractSceneEntity) {
    }

    public IRenderObjectFabric getRenderFabric() {
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

    public MeshRenderData getMeshRenderData() {
        return this.meshRenderData;
    }

    public RenderObjectData setModelRenderParams(MeshRenderData meshRenderData) {
        this.meshRenderData = meshRenderData;
        return this;
    }

    public Class<? extends AbstractSceneEntity> getRenderClass() {
        return this.aClass;
    }

    protected RenderObjectData copyObject() {
        RenderObjectData renderObjectData = new RenderObjectData(this.getRenderFabric(), this.getRenderClass(), this.getMeshRenderData());
        renderObjectData.setMeshDataGroup(this.getMeshDataGroup());
        renderObjectData.setOverObjectMaterial(this.getOverObjectMaterial());
        renderObjectData.setEntityModelConstructor(this.getEntityModelConstructor());
        return renderObjectData;
    }
}
