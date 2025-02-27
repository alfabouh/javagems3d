package javagems3d.graphics.objects.rendering.data;

import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.graphics.objects.entities.world.SceneWorldEntity;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.resources.assets.models.helper.constructor.IEntityModelConstructor;

@SuppressWarnings("all")
public class EntityRenderData {
    public static ISceneObjectConstructor DEFAULT_OBJECT_CONSTRUCTOR = (sceneWorld, worldItem, entityRenderData) -> new SceneWorldEntity(sceneWorld, worldItem, entityRenderData);

    private final ISceneObjectConstructor sceneObjectConstructor;
    private IEntityModelConstructor<WorldItem> entityModelConstructor;
    private MeshStructure3D<?> meshStructure;
    private RenderAttributes renderAttributes;

    public EntityRenderData(@NotNull ISceneObjectConstructor sceneObjectConstructor, @Nullable RenderAttributes renderAttributes) {
        this(sceneObjectConstructor, renderAttributes, null);
    }

    public EntityRenderData(@NotNull ISceneObjectConstructor sceneObjectConstructor, @Nullable RenderAttributes renderAttributes, @Nullable MeshStructure3D<?> meshStructure) {
        this.sceneObjectConstructor = sceneObjectConstructor;
        this.entityModelConstructor = null;
        this.meshStructure = meshStructure;
        this.renderAttributes = renderAttributes;
    }

    public EntityRenderData(@NotNull EntityRenderData entityRenderData, @Nullable MeshStructure3D<?> meshStructure) {
        this(entityRenderData.getSceneObjectConstructor(), entityRenderData.getObjectRenderAttributes(), meshStructure);
    }

    public static ISceneObjectConstructor defaultObjectConstructor() {
        return EntityRenderData.DEFAULT_OBJECT_CONSTRUCTOR;
    }

    public SceneEntity constructSceneObject(SceneWorld sceneWorld, WorldItem worldItem) {
        return this.getSceneObjectConstructor().createSceneEntity(sceneWorld, worldItem, this.copyObject());
    }

    protected void onObjectCreated(SceneEntity abstractSceneEntity) {
    }

    public EntityRenderData setObjectRenderSettings(RenderAttributes objectRenderingConfiguration) {
        this.renderAttributes = objectRenderingConfiguration;
        return this;
    }

    public EntityRenderData setMeshDataGroup(MeshStructure3D<?> meshStructure) {
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

    public MeshStructure3D<?> getMeshDataGroup() {
        return this.meshStructure;
    }

    public IEntityModelConstructor<WorldItem> getEntityModelConstructor() {
        return this.entityModelConstructor;
    }

    public RenderAttributes getObjectRenderAttributes() {
        return this.renderAttributes;
    }

    protected ISceneObjectConstructor getSceneObjectConstructor() {
        return this.sceneObjectConstructor;
    }

    protected EntityRenderData copyObject() {
        EntityRenderData entityRenderData = new EntityRenderData(this.getSceneObjectConstructor(), this.getObjectRenderAttributes() == null ? null : this.getObjectRenderAttributes().copy());
        entityRenderData.setMeshDataGroup(this.getMeshDataGroup());
        entityRenderData.setEntityModelConstructor(this.getEntityModelConstructor());
        return entityRenderData;
    }

    @FunctionalInterface
    public interface ISceneObjectConstructor {
        @NotNull SceneEntity createSceneEntity(SceneWorld sceneWorld, WorldItem worldItem, EntityRenderData entityRenderData);
    }
}
