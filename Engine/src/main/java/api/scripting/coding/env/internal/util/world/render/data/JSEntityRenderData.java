package api.scripting.coding.env.internal.util.world.render.data;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.JSMeshStructure3D;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldItem;
import api.scripting.coding.env.internal.util.world.render.processing.JSRenderAttributes;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import api.scripting.coding.env.internal.util.world.render.world.instances.JSSceneEntity;
import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.graphics.objects.entities.world.SceneWorldEntity;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.constructors.IModelConstructor;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;

@JSCodingClass(binding = "JSEntityRenderData", description = "Wrapper for EntityRenderData, handling mesh, model constructor, and render attributes.")
public class JSEntityRenderData {
    @JSCodingField(description = "DEFAULT_OBJECT_CONSTRUCTOR")
    public static JSSceneEntityConstructor DEFAULT_OBJECT_CONSTRUCTOR = (sceneWorld, worldItem, entityRenderData) -> new JSSceneEntity(new SceneWorldEntity(sceneWorld.getJavaSceneWorld(), worldItem.getJavaWorldObject(), entityRenderData.getJavaEntityRenderData()));

    @JSHideFromDoc
    private final EntityRenderData entityRenderData;

    @JSCodingConstructor(description = "Wrap existing EntityRenderData", paramNames = {"entityRenderData"})
    public JSEntityRenderData(EntityRenderData entityRenderData) {
        this.entityRenderData = entityRenderData;
    }

    @JSCodingConstructor(description = "Create EntityRenderData with custom SceneEntity constructor and optional RenderAttributes", paramNames = {"constructor", "renderAttributes"})
    public JSEntityRenderData(JSSceneEntityConstructor constructor, JSRenderAttributes renderAttributes) {
        this.entityRenderData = new EntityRenderData(constructor != null ? constructor.toJavaConstructor() : EntityRenderData.defaultObjectConstructor(), renderAttributes != null ? renderAttributes.getJavaRenderAttributes() : null);
    }

    @JSCodingConstructor(description = "Create EntityRenderData with RenderAttributes only", paramNames = {"renderAttributes"})
    public JSEntityRenderData(JSRenderAttributes renderAttributes) {
        this.entityRenderData = new EntityRenderData(renderAttributes != null ? renderAttributes.getJavaRenderAttributes() : null);
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java EntityRenderData")
    public EntityRenderData getJavaEntityRenderData() {
        return this.entityRenderData;
    }

    @JSCodingFunctionOrMethod(description = "Set RenderAttributes for this entity", paramNames = {"renderAttributes"})
    public JSEntityRenderData setObjectRenderSettings(JSRenderAttributes renderAttributes) {
        this.entityRenderData.setObjectRenderSettings(renderAttributes != null ? renderAttributes.getJavaRenderAttributes() : null);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Set mesh structure for this entity", paramNames = {"meshStructure"})
    public JSEntityRenderData setMeshStructure(JSMeshStructure3D meshStructure) {
        this.entityRenderData.setMeshStructure(meshStructure != null ? meshStructure.getJavaMeshStructure3D() : null);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Set mesh structure constructor for this entity (JSMeshStructureConstructor<JSWorldItem>)", paramNames = {"modelConstructor"})
    public JSEntityRenderData setEntityModelConstructor(JSMeshStructureConstructor<JSWorldItem> modelConstructor) {
        if (modelConstructor != null) {
            this.entityRenderData.setEntityModelConstructor(worldItem -> modelConstructor.toJavaConstructor().constructMeshDataGroup(new JSWorldItem(worldItem)));
        }
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get mesh structure of this entity")
    public JSMeshStructure3D getMeshStructure() {
        MeshStructure3D<?> mesh = this.entityRenderData.getMeshStructure();
        if (mesh == null) {
            return null;
        }
        return () -> mesh;
    }

    @JSCodingFunctionOrMethod(description = "Get model constructor of this entity (JSMeshStructureConstructor<JSWorldItem>)")
    public JSMeshStructureConstructor<JSWorldItem> getEntityModelConstructor() {
        IModelConstructor<WorldItem, ? extends IMesh> constructor = this.entityRenderData.getEntityModelConstructor();
        if (constructor == null) return null;
        return t -> (JSMeshStructure3D) () -> constructor.constructMeshDataGroup(t.getJavaWorldObject());
    }

    @JSCodingFunctionOrMethod(description = "Get render attributes of this entity")
    public JSRenderAttributes getObjectRenderAttributes() {
        RenderAttributes attrs = this.entityRenderData.getObjectRenderAttributes();
        return attrs != null ? new JSRenderAttributes(attrs) : null;
    }

    @JSCodingFunctionOrMethod(description = "Construct a SceneEntity from this data and given world and worldItem", paramNames = {"world", "worldItem"})
    public JSSceneEntity constructSceneObject(JSSceneWorld world, JSWorldItem worldItem) {
        SceneEntity sceneEntity = this.entityRenderData.constructSceneObject(world.getJavaSceneWorld(), worldItem.getJavaWorldObject());
        return new JSSceneEntity(sceneEntity);
    }

    @JSHideFromDoc
    public JSEntityRenderData copyObject() {
        return new JSEntityRenderData(this.entityRenderData.copyObject());
    }
}