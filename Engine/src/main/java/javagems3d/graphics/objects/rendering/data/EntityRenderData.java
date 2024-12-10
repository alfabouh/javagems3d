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

package javagems3d.graphics.objects.rendering.data;

import javagems3d.graphics.objects.rendering.configuration.ObjectRenderConfiguration;
import javagems3d.graphics.objects.rendering.fabric.IRenderFabric;
import javagems3d.system.resources.assets.material.Material;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javagems3d.graphics.objects.entities.AbstractSceneEntity;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.resources.assets.models.helper.constructor.IEntityModelConstructor;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.service.exceptions.JGemsRuntimeException;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("all")
public class EntityRenderData {
    private final IRenderFabric renderFabric;
    private final Class<? extends AbstractSceneEntity> abstractEntityClass;
    private IEntityModelConstructor<WorldItem> entityModelConstructor;
    private MeshStructure<?> meshStructure;
    private ObjectRenderConfiguration objectRenderingConfiguration;

    public EntityRenderData(@Nullable IRenderFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> abstractEntityClass, @NotNull JGemsShaderManager shaderManager, @Nullable MeshStructure<?> meshStructure) {
        this.abstractEntityClass = abstractEntityClass;
        this.renderFabric = renderFabric;
        this.entityModelConstructor = null;
        this.meshStructure = meshStructure;
        this.objectRenderingConfiguration = new ObjectRenderConfiguration(shaderManager);
    }

    public EntityRenderData(@Nullable IRenderFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> abstractEntityClass, @NotNull ObjectRenderConfiguration objectRenderingConfiguration, @Nullable MeshStructure<?> meshStructure) {
        this.abstractEntityClass = abstractEntityClass;
        this.renderFabric = renderFabric;
        this.entityModelConstructor = null;
        this.meshStructure = meshStructure;
        this.objectRenderingConfiguration = objectRenderingConfiguration;
    }

    public EntityRenderData(@Nullable IRenderFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> abstractEntityClass, @NotNull JGemsShaderManager shaderManager) {
        this(renderFabric, abstractEntityClass, shaderManager, null);
    }

    public EntityRenderData(@Nullable IRenderFabric renderFabric, @NotNull Class<? extends AbstractSceneEntity> abstractEntityClass, @NotNull ObjectRenderConfiguration objectRenderingConfiguration) {
        this.abstractEntityClass = abstractEntityClass;
        this.renderFabric = renderFabric;
        this.entityModelConstructor = null;
        this.meshStructure = null;
        this.objectRenderingConfiguration = objectRenderingConfiguration;
    }

    public EntityRenderData(@NotNull EntityRenderData entityRenderData, @Nullable MeshStructure<?> meshStructure) {
        this(entityRenderData.getRenderFabric(), entityRenderData.getSceneObjectClass(), entityRenderData.getObjectRenderSettings(), meshStructure);
        this.setEntityModelConstructor(entityRenderData.getEntityModelConstructor());
    }

    public EntityRenderData(@NotNull EntityRenderData entityRenderData, Material overObjectMaterial) {
        this(entityRenderData.getRenderFabric(), entityRenderData.getSceneObjectClass(), entityRenderData.getObjectRenderSettings(), entityRenderData.getMeshDataGroup());
        this.setEntityModelConstructor(entityRenderData.getEntityModelConstructor());
    }

    public AbstractSceneEntity constructPhysicsObject(SceneWorld sceneWorld, WorldItem worldItem) {
        final EntityRenderData entityRenderData = this.copyObject();
        try {
            AbstractSceneEntity abstractSceneEntity = this.abstractEntityClass.getDeclaredConstructor(SceneWorld.class, WorldItem.class, EntityRenderData.class).newInstance(sceneWorld, worldItem, entityRenderData);
            this.onPhysicsObjectCreated(abstractSceneEntity);
            return abstractSceneEntity;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new JGemsRuntimeException(e);
        }
    }

    protected void onPhysicsObjectCreated(AbstractSceneEntity abstractSceneEntity) {
    }

    public EntityRenderData setObjectRenderSettings(ObjectRenderConfiguration objectRenderingConfiguration) {
        this.objectRenderingConfiguration = objectRenderingConfiguration;
        return this;
    }

    public IRenderFabric getRenderFabric() {
        return this.renderFabric;
    }

    public MeshStructure<?> getMeshDataGroup() {
        return this.meshStructure;
    }

    public EntityRenderData setMeshDataGroup(MeshStructure<?> meshStructure) {
        this.meshStructure = meshStructure;
        this.entityModelConstructor = null;
        return this;
    }

    public IEntityModelConstructor<WorldItem> getEntityModelConstructor() {
        return this.entityModelConstructor;
    }

    public EntityRenderData setEntityModelConstructor(IEntityModelConstructor<WorldItem> entityModelConstructor) {
        if (entityModelConstructor != null) {
            this.meshStructure = null;
        }
        this.entityModelConstructor = entityModelConstructor;
        return this;
    }

    public ObjectRenderConfiguration getObjectRenderSettings() {
        return this.objectRenderingConfiguration;
    }

    public Class<? extends AbstractSceneEntity> getSceneObjectClass() {
        return this.abstractEntityClass;
    }

    protected EntityRenderData copyObject() {
        EntityRenderData entityRenderData = new EntityRenderData(this.getRenderFabric(), this.getSceneObjectClass(), this.getObjectRenderSettings().copy());
        entityRenderData.setMeshDataGroup(this.getMeshDataGroup());
        entityRenderData.setEntityModelConstructor(this.getEntityModelConstructor());
        return entityRenderData;
    }
}
