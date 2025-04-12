package javagems3d.graphics.objects.rendering.data;

import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.entities.world.SceneWorldProp;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.constructors.ISceneEntityConstructor;
import javagems3d.graphics.objects.rendering.constructors.IScenePropConstructor;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.graphics.objects.rendering.constructors.IModelConstructor;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("all")
public class PropRenderData {
    public static IScenePropConstructor DEFAULT_OBJECT_CONSTRUCTOR = (name, sceneWorld, propRenderData) -> new SceneWorldProp(name, sceneWorld, propRenderData);

    private final IScenePropConstructor sceneObjectConstructor;
    private IModelConstructor<Void> propModelConstructor;
    private MeshStructure3D<?> meshStructure;
    private RenderAttributes renderAttributes;

    public PropRenderData(@NotNull IScenePropConstructor sceneObjectConstructor, @Nullable RenderAttributes renderAttributes) {
        this(sceneObjectConstructor, renderAttributes, null);
    }

    public PropRenderData(@Nullable RenderAttributes renderAttributes) {
        this(PropRenderData.defaultObjectConstructor(), renderAttributes, null);
    }

    public PropRenderData(@Nullable RenderAttributes renderAttributes, @Nullable MeshStructure3D<?> meshStructure) {
        this(PropRenderData.DEFAULT_OBJECT_CONSTRUCTOR, renderAttributes, meshStructure);
    }

    public PropRenderData(@NotNull IScenePropConstructor sceneObjectConstructor, @Nullable RenderAttributes renderAttributes, @Nullable MeshStructure3D<?> meshStructure) {
        this.sceneObjectConstructor = sceneObjectConstructor;
        this.propModelConstructor = null;
        this.meshStructure = meshStructure;
        this.renderAttributes = renderAttributes;
    }

    public PropRenderData(@NotNull PropRenderData entityRenderData, @Nullable MeshStructure3D<?> meshStructure) {
        this(entityRenderData.getSceneObjectConstructor(), entityRenderData.getObjectRenderAttributes(), meshStructure);
    }

    public static IScenePropConstructor defaultObjectConstructor() {
        return PropRenderData.DEFAULT_OBJECT_CONSTRUCTOR;
    }

    public SceneProp constructSceneObject(String name, SceneWorld sceneWorld, PropRenderData propRenderData) {
        return this.getSceneObjectConstructor().createSceneProp(name, sceneWorld, propRenderData);
    }

    protected void onObjectCreated(SceneEntity abstractSceneEntity) {
    }

    public PropRenderData setObjectRenderSettings(RenderAttributes objectRenderingConfiguration) {
        this.renderAttributes = objectRenderingConfiguration;
        return this;
    }

    public PropRenderData setMeshDataGroup(MeshStructure3D<?> meshStructure) {
        this.meshStructure = meshStructure;
        this.propModelConstructor = null;
        return this;
    }

    public PropRenderData setPropModelConstructor(IModelConstructor<Void> propModelConstructor) {
        if (propModelConstructor != null) {
            this.meshStructure = null;
        }
        this.propModelConstructor = propModelConstructor;
        return this;
    }

    public MeshStructure3D<?> getMeshDataGroup() {
        return this.meshStructure;
    }

    public IModelConstructor<Void> getPropModelConstructor() {
        return this.propModelConstructor;
    }

    public RenderAttributes getObjectRenderAttributes() {
        return this.renderAttributes;
    }

    protected IScenePropConstructor getSceneObjectConstructor() {
        return this.sceneObjectConstructor;
    }

    protected PropRenderData copyObject() {
        PropRenderData entityRenderData = new PropRenderData(this.getSceneObjectConstructor(), this.getObjectRenderAttributes() == null ? null : this.getObjectRenderAttributes().copy());
        entityRenderData.setMeshDataGroup(this.getMeshDataGroup());
        entityRenderData.setPropModelConstructor(this.getPropModelConstructor());
        return entityRenderData;
    }

}
