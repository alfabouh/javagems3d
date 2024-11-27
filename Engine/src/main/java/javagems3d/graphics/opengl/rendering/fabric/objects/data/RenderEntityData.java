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

import javagems3d.graphics.opengl.rendering.items.settings.ObjectRenderSettings;
import javagems3d.system.resources.assets.material.Material;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javagems3d.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import javagems3d.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import javagems3d.graphics.opengl.world.SceneWorld;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.resources.assets.models.helper.constructor.IEntityModelConstructor;
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
    private MeshStructure<?> meshStructure;
    private ObjectRenderSettings objectRenderSettings;

    public RenderEntityData(@NotNull IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> abstractEntityClass, @NotNull JGemsShaderManager shaderManager, @Nullable MeshStructure<?> meshStructure) {
        this.abstractEntityClass = abstractEntityClass;
        this.renderFabric = renderFabric;
        this.entityModelConstructor = null;
        this.meshStructure = meshStructure;
        this.objectRenderSettings = new ObjectRenderSettings(shaderManager);
    }

    public RenderEntityData(@NotNull IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> abstractEntityClass, @NotNull ObjectRenderSettings objectRenderSettings, @Nullable MeshStructure<?> meshStructure) {
        this.abstractEntityClass = abstractEntityClass;
        this.renderFabric = renderFabric;
        this.entityModelConstructor = null;
        this.meshStructure = meshStructure;
        this.objectRenderSettings = objectRenderSettings;
    }

    public RenderEntityData(@NotNull IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> abstractEntityClass, @NotNull JGemsShaderManager shaderManager) {
        this(renderFabric, abstractEntityClass, shaderManager, null);
    }

    public RenderEntityData(@NotNull IRenderObjectFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> abstractEntityClass, @NotNull ObjectRenderSettings objectRenderSettings) {
        this.abstractEntityClass = abstractEntityClass;
        this.renderFabric = renderFabric;
        this.entityModelConstructor = null;
        this.meshStructure = null;
        this.objectRenderSettings = objectRenderSettings;
    }

    public RenderEntityData(@NotNull RenderEntityData renderEntityData, @Nullable MeshStructure<?> meshStructure) {
        this(renderEntityData.getRenderFabric(), renderEntityData.getSceneObjectClass(), renderEntityData.getObjectRenderSettings(), meshStructure);
        this.setEntityModelConstructor(renderEntityData.getEntityModelConstructor());
    }

    public RenderEntityData(@NotNull RenderEntityData renderEntityData, Material overObjectMaterial) {
        this(renderEntityData.getRenderFabric(), renderEntityData.getSceneObjectClass(), renderEntityData.getObjectRenderSettings(), renderEntityData.getMeshDataGroup());
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

    public RenderEntityData setObjectRenderSettings(ObjectRenderSettings objectRenderSettings) {
        this.objectRenderSettings = objectRenderSettings;
        return this;
    }

    public IRenderObjectFabric getRenderFabric() {
        return this.renderFabric;
    }

    public MeshStructure<?> getMeshDataGroup() {
        return this.meshStructure;
    }

    public RenderEntityData setMeshDataGroup(MeshStructure<?> meshStructure) {
        this.meshStructure = meshStructure;
        this.entityModelConstructor = null;
        return this;
    }

    public IEntityModelConstructor<WorldItem> getEntityModelConstructor() {
        return this.entityModelConstructor;
    }

    public RenderEntityData setEntityModelConstructor(IEntityModelConstructor<WorldItem> entityModelConstructor) {
        if (entityModelConstructor != null) {
            this.meshStructure = null;
        }
        this.entityModelConstructor = entityModelConstructor;
        return this;
    }

    public ObjectRenderSettings getObjectRenderSettings() {
        return this.objectRenderSettings;
    }

    public Class<? extends AbstractSceneEntity> getSceneObjectClass() {
        return this.abstractEntityClass;
    }

    protected RenderEntityData copyObject() {
        RenderEntityData renderEntityData = new RenderEntityData(this.getRenderFabric(), this.getSceneObjectClass(), this.getObjectRenderSettings().copy());
        renderEntityData.setMeshDataGroup(this.getMeshDataGroup());
        renderEntityData.setEntityModelConstructor(this.getEntityModelConstructor());
        return renderEntityData;
    }
}
