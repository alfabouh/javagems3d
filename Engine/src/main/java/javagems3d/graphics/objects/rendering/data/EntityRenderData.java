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

import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.resources.assets.models.helper.constructor.IEntityModelConstructor;
import javagems3d.system.service.exceptions.JGemsRuntimeException;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("all")
public class EntityRenderData {
    private final Class<? extends SceneEntity> abstractEntityClass;
    private IEntityModelConstructor<WorldItem> entityModelConstructor;
    private MeshStructure<?> meshStructure;
    private RenderAttributes renderAttributes;

    public EntityRenderData(@NotNull Class<? extends SceneEntity> abstractEntityClass, @Nullable RenderTable renderTable, @Nullable MeshStructure<?> meshStructure) {
        this(abstractEntityClass, renderTable != null ? new RenderAttributes(renderTable) : null, meshStructure);
    }

    public EntityRenderData(@NotNull Class<? extends SceneEntity> abstractEntityClass, @Nullable RenderTable shadingTable) {
        this(abstractEntityClass, shadingTable, null);
    }

    public EntityRenderData(@NotNull Class<? extends SceneEntity> abstractEntityClass, @Nullable RenderAttributes renderAttributes) {
        this(abstractEntityClass, renderAttributes, null);
    }

    public EntityRenderData(@NotNull Class<? extends SceneEntity> abstractEntityClass, @Nullable RenderAttributes renderAttributes, @Nullable MeshStructure<?> meshStructure) {
        this.abstractEntityClass = abstractEntityClass;
        this.entityModelConstructor = null;
        this.meshStructure = meshStructure;
        this.renderAttributes = renderAttributes;
    }

    public EntityRenderData(@NotNull EntityRenderData entityRenderData, @Nullable MeshStructure<?> meshStructure) {
        this(entityRenderData.getSceneObjectClass(), entityRenderData.getObjectRenderAttributes(), meshStructure);
    }

    public SceneEntity constructSceneObject(SceneWorld sceneWorld, WorldItem worldItem) {
        final EntityRenderData entityRenderData = this.copyObject();
        try {
            SceneEntity abstractSceneEntity = this.abstractEntityClass.getDeclaredConstructor(SceneWorld.class, WorldItem.class, EntityRenderData.class).newInstance(sceneWorld, worldItem, entityRenderData);
            this.onObjectCreated(abstractSceneEntity);
            return abstractSceneEntity;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new JGemsRuntimeException(e);
        }
    }

    protected void onObjectCreated(SceneEntity abstractSceneEntity) {
    }

    public EntityRenderData setObjectRenderSettings(RenderAttributes objectRenderingConfiguration) {
        this.renderAttributes = objectRenderingConfiguration;
        return this;
    }

    public EntityRenderData setMeshDataGroup(MeshStructure<?> meshStructure) {
        this.meshStructure = meshStructure;
        this.entityModelConstructor = null;
        return this;
    }

    public EntityRenderData setEntityModelConstructor(IEntityModelConstructor<WorldItem> entityModelConstructor) {
        if (entityModelConstructor != null) {
            this.meshStructure = null;
        }
        this.entityModelConstructor = entityModelConstructor;
        return this;
    }

    public @Nullable MeshStructure<?> getMeshDataGroup() {
        return this.meshStructure;
    }

    public @Nullable IEntityModelConstructor<WorldItem> getEntityModelConstructor() {
        return this.entityModelConstructor;
    }

    public @Nullable RenderAttributes getObjectRenderAttributes() {
        return this.renderAttributes;
    }

    public @NotNull Class<? extends SceneEntity> getSceneObjectClass() {
        return this.abstractEntityClass;
    }

    protected EntityRenderData copyObject() {
        EntityRenderData entityRenderData = new EntityRenderData(this.getSceneObjectClass(), this.getObjectRenderAttributes() == null ? null : this.getObjectRenderAttributes().copy());
        entityRenderData.setMeshDataGroup(this.getMeshDataGroup());
        entityRenderData.setEntityModelConstructor(this.getEntityModelConstructor());
        return entityRenderData;
    }
}
