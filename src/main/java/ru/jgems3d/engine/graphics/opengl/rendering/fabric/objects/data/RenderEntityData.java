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

package ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.system.resources.assets.material.Material;
import ru.jgems3d.engine.system.resources.assets.models.helper.constructor.IEntityModelConstructor;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.assets.models.mesh.data.render.MeshRenderData;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.jgems3d.engine.system.service.exceptions.JGemsRuntimeException;

import java.lang.reflect.InvocationTargetException;

/**
 * This class contains information for rendering an object from a scene.
 */

@SuppressWarnings("all")
public class RenderEntityData {
    private final IRenderObjectFabric renderFabric;
    private final Class<? extends AbstractSceneEntity> abstractEntityClass;
    private IEntityModelConstructor<WorldItem> entityModelConstructor;
    private MeshDataGroup meshDataGroup;
    private MeshRenderData meshRenderData;

    public RenderEntityData(@NotNull IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> abstractEntityClass, @NotNull JGemsShaderManager shaderManager, @Nullable MeshDataGroup meshDataGroup) {
        this.abstractEntityClass = abstractEntityClass;
        this.renderFabric = renderFabric;
        this.entityModelConstructor = null;
        this.meshDataGroup = meshDataGroup;
        this.meshRenderData = MeshRenderData.defaultMeshRenderData(shaderManager);
    }

    public RenderEntityData(@NotNull IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> abstractEntityClass, @NotNull MeshRenderData meshRenderData, @Nullable MeshDataGroup meshDataGroup) {
        this.abstractEntityClass = abstractEntityClass;
        this.renderFabric = renderFabric;
        this.entityModelConstructor = null;
        this.meshDataGroup = meshDataGroup;
        this.meshRenderData = meshRenderData.copy();
    }

    public RenderEntityData(@NotNull IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> abstractEntityClass, @NotNull JGemsShaderManager shaderManager) {
        this(renderFabric, abstractEntityClass, shaderManager, null);
    }

    public RenderEntityData(@NotNull IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> abstractEntityClass, @NotNull MeshRenderData meshRenderData) {
        this.abstractEntityClass = abstractEntityClass;
        this.renderFabric = renderFabric;
        this.entityModelConstructor = null;
        this.meshDataGroup = null;
        this.meshRenderData = meshRenderData.copy();
    }

    public RenderEntityData(@NotNull RenderEntityData renderEntityData, @Nullable MeshDataGroup meshDataGroup) {
        this(renderEntityData.getRenderFabric(), renderEntityData.getSceneObjectClass(), renderEntityData.getMeshRenderData(), meshDataGroup);
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

    public RenderEntityData setModelRenderData(MeshRenderData meshRenderData) {
        this.meshRenderData = meshRenderData;
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
    public MeshDataGroup getMeshDataGroup() {
        return this.meshDataGroup;
    }

    public RenderEntityData setMeshDataGroup(MeshDataGroup meshDataGroup) {
        this.meshDataGroup = meshDataGroup;
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
            this.meshDataGroup = null;
        }
        this.entityModelConstructor = entityModelConstructor;
        return this;
    }

    /**
     * This is the information for the model renderer.
     */
    public MeshRenderData getMeshRenderData() {
        return this.meshRenderData;
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
