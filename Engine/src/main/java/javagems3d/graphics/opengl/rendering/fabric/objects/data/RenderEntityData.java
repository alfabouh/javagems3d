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

package javagems3d.graphics.opengl.rendering.fabric.objects.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javagems3d.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import javagems3d.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import javagems3d.graphics.opengl.world.SceneWorld;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.resources.assets.material.Material;
import javagems3d.system.resources.assets.models.helper.constructor.IEntityModelConstructor;
import javagems3d.system.resources.assets.models.mesh.MeshGroup;
import javagems3d.system.resources.assets.models.properties.ModelRenderData;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.service.exceptions.JGemsRuntimeException;

import java.lang.reflect.InvocationTargetException;

/**
 * This class contains information for rendering an object from a scene.
 */

@SuppressWarnings("all")
public class RenderEntityData {
    private final IRenderObjectFabric renderFabric;
    private final Class<? extends AbstractSceneEntity> abstractEntityClass;
    private IEntityModelConstructor<WorldItem> entityModelConstructor;
    private MeshGroup meshGroup;
    private ModelRenderData modelRenderData;

    public RenderEntityData(@NotNull IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> abstractEntityClass, @NotNull JGemsShaderManager shaderManager, @Nullable MeshGroup meshGroup) {
        this.abstractEntityClass = abstractEntityClass;
        this.renderFabric = renderFabric;
        this.entityModelConstructor = null;
        this.meshGroup = meshGroup;
        this.modelRenderData = ModelRenderData.defaultMeshRenderData(shaderManager);
    }

    public RenderEntityData(@NotNull IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> abstractEntityClass, @NotNull ModelRenderData modelRenderData, @Nullable MeshGroup meshGroup) {
        this.abstractEntityClass = abstractEntityClass;
        this.renderFabric = renderFabric;
        this.entityModelConstructor = null;
        this.meshGroup = meshGroup;
        this.modelRenderData = modelRenderData.copy();
    }

    public RenderEntityData(@NotNull IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> abstractEntityClass, @NotNull JGemsShaderManager shaderManager) {
        this(renderFabric, abstractEntityClass, shaderManager, null);
    }

    public RenderEntityData(@NotNull IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> abstractEntityClass, @NotNull ModelRenderData modelRenderData) {
        this.abstractEntityClass = abstractEntityClass;
        this.renderFabric = renderFabric;
        this.entityModelConstructor = null;
        this.meshGroup = null;
        this.modelRenderData = modelRenderData.copy();
    }

    public RenderEntityData(@NotNull RenderEntityData renderEntityData, @Nullable MeshGroup meshGroup) {
        this(renderEntityData.getRenderFabric(), renderEntityData.getSceneObjectClass(), renderEntityData.getMeshRenderData(), meshGroup);
        this.setEntityModelConstructor(renderEntityData.getEntityModelConstructor());
    }

    public RenderEntityData(@NotNull RenderEntityData renderEntityData, Material overObjectMaterial) {
        this(renderEntityData.getRenderFabric(), renderEntityData.getSceneObjectClass(), renderEntityData.getMeshRenderData(), renderEntityData.getMeshDataGroup());
        this.setEntityModelConstructor(renderEntityData.getEntityModelConstructor());
    }

    public AbstractSceneEntity constructPhysicsObject(SceneWorld sceneWorld, WorldItem worldItem) {
        final RenderEntityData renderEntityData = this.copyObject();
        try {
            AbstractSceneEntity abstractSceneEntity = this.abstractEntityClass.getDeclaredConstructor(SceneWorld.class, WorldItem.class, RenderEntityData.class).newInstance(sceneWorld, worldItem, renderEntityData);
            this.onPhysicsObjectCreated(abstractSceneEntity);
            return abstractSceneEntity;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new JGemsRuntimeException(e);
        }
    }

    protected void onPhysicsObjectCreated(AbstractSceneEntity abstractSceneEntity) {
    }

    public RenderEntityData setModelRenderData(ModelRenderData modelRenderData) {
        this.modelRenderData = modelRenderData;
        return this;
    }

    /**
     * The render factory is an object that will render an entity based on the available information
     */
    public IRenderObjectFabric getRenderFabric() {
        return this.renderFabric;
    }

    /**
     * Object's mesh.
     */
    public MeshGroup getMeshDataGroup() {
        return this.meshGroup;
    }

    public RenderEntityData setMeshDataGroup(MeshGroup meshGroup) {
        this.meshGroup = meshGroup;
        this.entityModelConstructor = null;
        return this;
    }

    /**
     * If you need to generate an object mesh manually, use this.
     */
    public IEntityModelConstructor<WorldItem> getEntityModelConstructor() {
        return this.entityModelConstructor;
    }

    public RenderEntityData setEntityModelConstructor(IEntityModelConstructor<WorldItem> entityModelConstructor) {
        if (entityModelConstructor != null) {
            this.meshGroup = null;
        }
        this.entityModelConstructor = entityModelConstructor;
        return this;
    }

    /**
     * This is the information for the model renderer.
     */
    public ModelRenderData getMeshRenderData() {
        return this.modelRenderData;
    }

    /**
     * The class that represents the object in the scene.
     */
    public Class<? extends AbstractSceneEntity> getSceneObjectClass() {
        return this.abstractEntityClass;
    }

    protected RenderEntityData copyObject() {
        RenderEntityData renderEntityData = new RenderEntityData(this.getRenderFabric(), this.getSceneObjectClass(), this.getMeshRenderData());
        renderEntityData.setMeshDataGroup(this.getMeshDataGroup());
        renderEntityData.setEntityModelConstructor(this.getEntityModelConstructor());
        return renderEntityData;
    }
}
