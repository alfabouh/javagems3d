package javagems3d.graphics.objects.rendering.data;

import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.graphics.objects.entities.world.SceneWorldEntity;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.constructors.ISceneEntityConstructor;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.graphics.objects.rendering.constructors.IModelConstructor;

@SuppressWarnings("all")
public class EntityRenderData {
    public static ISceneEntityConstructor DEFAULT_OBJECT_CONSTRUCTOR = (sceneWorld, worldItem, entityRenderData) -> new SceneWorldEntity(sceneWorld, worldItem, entityRenderData);

    private final ISceneEntityConstructor sceneObjectConstructor;
    private IModelConstructor<WorldItem> entityModelConstructor;
    private MeshStructure3D<?> meshStructure;
    private RenderAttributes renderAttributes;

    public EntityRenderData(@NotNull ISceneEntityConstructor sceneObjectConstructor, @Nullable RenderAttributes renderAttributes) {
        this(sceneObjectConstructor, renderAttributes, null);
    }

    public EntityRenderData(@Nullable RenderAttributes renderAttributes) {
        this(EntityRenderData.defaultObjectConstructor(), renderAttributes, null);
    }

    public EntityRenderData(@Nullable RenderAttributes renderAttributes, @Nullable MeshStructure3D<?> meshStructure) {
        this(EntityRenderData.DEFAULT_OBJECT_CONSTRUCTOR, renderAttributes, meshStructure);
    }

    public EntityRenderData(@NotNull ISceneEntityConstructor sceneObjectConstructor, @Nullable RenderAttributes renderAttributes, @Nullable MeshStructure3D<?> meshStructure) {
        this.sceneObjectConstructor = sceneObjectConstructor;
        this.entityModelConstructor = null;
        this.meshStructure = meshStructure;
        this.renderAttributes = renderAttributes;
    }

    public EntityRenderData(@NotNull EntityRenderData entityRenderData, @Nullable MeshStructure3D<?> meshStructure) {
        this(entityRenderData.getSceneObjectConstructor(), entityRenderData.getObjectRenderAttributes(), meshStructure);
    }

    public static ISceneEntityConstructor defaultObjectConstructor() {
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

    public EntityRenderData setEntityModelConstructor(IModelConstructor<WorldItem> entityModelConstructor) {
        if (entityModelConstructor != null) {
            this.meshStructure = null;
        }
        this.entityModelConstructor = entityModelConstructor;
        return this;
    }

    public MeshStructure3D<?> getMeshDataGroup() {
        return this.meshStructure;
    }

    public IModelConstructor<WorldItem> getEntityModelConstructor() {
        return this.entityModelConstructor;
    }

    public RenderAttributes getObjectRenderAttributes() {
        return this.renderAttributes;
    }

    protected ISceneEntityConstructor getSceneObjectConstructor() {
        return this.sceneObjectConstructor;
    }

    protected EntityRenderData copyObject() {
        EntityRenderData entityRenderData = new EntityRenderData(this.getSceneObjectConstructor(), this.getObjectRenderAttributes() == null ? null : this.getObjectRenderAttributes().copy());
        entityRenderData.setMeshDataGroup(this.getMeshDataGroup());
        entityRenderData.setEntityModelConstructor(this.getEntityModelConstructor());
        return entityRenderData;
    }

}
