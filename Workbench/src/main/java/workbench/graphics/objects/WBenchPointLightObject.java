package workbench.graphics.objects;

import api.application.workbench.resources.data.wbench.MapObjectsIdentifiers;
import api.application.workbench.resources.data.wbench.properties.WBenchRenderProperties;
import javagems3d.graphics.environment.lights.ILightAttached;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.rendering.scene.renderer.debug.DebugLinesDrawer;
import javagems3d.mapping.tags.Tag;
import javagems3d.mapping.tags.TagID;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.tags.base.AxisConstraints;
import javagems3d.mapping.tags.base.ColorMode;
import javagems3d.mapping.tags.base.TranslationConstraints;
import javagems3d.mapping.tags.base.VectorMode;
import javagems3d.mapping.tags.items.TagColor;
import javagems3d.mapping.tags.items.TagFloat;
import javagems3d.mapping.tags.items.TagObjectsList;
import javagems3d.mapping.tags.items.TagVector;
import javagems3d.physics.world.IWorld;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;
import workbench.graphics.fabrics.MarkerSimpleRenderFabric;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.graphics.scene.world.WBenchWorld;
import workbench.resources.WBenchResourceManager;

public class WBenchPointLightObject extends WBenchMarkerObject {
    private PointLight pointLight;

    public WBenchPointLightObject(@NotNull WBenchObject.ID objectId, @NotNull WBenchWorld wBenchWorld, @Nullable MeshStructure3D<?> meshStructure3D, @NotNull RenderAttributes renderAttributes, @NotNull TagsContainer tagsContainer, @NotNull TranslationConstraints translationConstraints, @NotNull Vector3f color, boolean transparent) {
        super(objectId, wBenchWorld, meshStructure3D, renderAttributes, tagsContainer, translationConstraints, color, transparent);
    }

    public static WBenchPointLightObject create(@NotNull String name, @NotNull WBenchWorld wBenchWorld, @NotNull TagsContainer tagsContainer) {
        final MeshStructure3D<?> meshStructure3D = WBenchResourceManager.localModelAssets.markerCube;
        final RenderAttributes renderAttributes = new RenderAttributes(RenderTable.getDirect(), WBenchRenderProperties.getDefault());
        renderAttributes.getProperties().setValueBool(JGemsRenderProperties.KEY_SHADOW_CASTER, false);
        renderAttributes.getRenderTable().setMatch(Pipeline.SCENE, new RenderTable.Data(WBenchResourceManager.localShaderAssets.simple, new MarkerSimpleRenderFabric(Stage.FORWARD)));
        final TranslationConstraints translationConstraints = new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.NULL, AxisConstraints.NULL);

        return new WBenchPointLightObject(new ID(name, null), wBenchWorld, meshStructure3D, renderAttributes, tagsContainer, translationConstraints, new Vector3f(1.0f), false);
    }

    public static WBenchPointLightObject create(@NotNull String name, @NotNull WBenchWorld wBenchWorld) {
        final Tag<TagColor> colorTag = Tag.create(TagID.DEFAULT.COLOR3, new TagColor(ColorMode.COLOR3, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f)));
        final Tag<TagFloat> brightnessTag = Tag.create(TagID.DEFAULT.BRIGHTNESS, new TagFloat(1.0f, 0.0f, 24.0f));
        final Tag<TagObjectsList> objectsListTag = Tag.create(new TagID(TagID.DEFAULT.OBJECT_LIST, "Attach To"), new TagObjectsList());
        final Tag<TagVector> offset = Tag.create(new TagID(TagID.DEFAULT.FLOAT3, "Offset"), new TagVector(VectorMode.VEC3F, new Vector4f(0.0f), -32.0f, 32.0f));

        final TagsContainer tagsContainer = new TagsContainer();
        tagsContainer.addTag(colorTag);
        tagsContainer.addTag(brightnessTag);
        tagsContainer.addTag(objectsListTag);
        tagsContainer.addTag(offset);

        return WBenchPointLightObject.create(MapObjectsIdentifiers.POINT_LIGHT + name, wBenchWorld, tagsContainer);
    }

    @Override
    public Vector3f getColor() {
        Vector4f tagColor = this.getTagsContainer().getTag(TagID.DEFAULT.COLOR3).<TagColor>getTagItemUnsafeCast().getColorVector();
        return new Vector3f(tagColor.x, tagColor.y, tagColor.z);
    }

    public int getAttachedTo() {
        if (!this.getTagsContainer().hasTag(TagID.DEFAULT.OBJECT_LIST)) {
            return -1;
        }
        return this.getTagsContainer().getTag(TagID.DEFAULT.OBJECT_LIST).<TagObjectsList>getTagItemUnsafeCast().getValue();
    }

    public float getBrightness() {
        return this.getTagsContainer().getTag(TagID.DEFAULT.BRIGHTNESS).<TagFloat>getTagItemUnsafeCast().getValue();
    }

    public Vector3f getLightOffset() {
        if (!this.getTagsContainer().hasTag(TagID.DEFAULT.FLOAT3)) {
            return new Vector3f(0.0f);
        }
        return this.getTagsContainer().getTag(TagID.DEFAULT.FLOAT3).<TagVector>getTagItemUnsafeCast().getValues().xyz(new Vector3f());
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
        if (this.pointLight != null) {
            this.pointLight.setLightColor(this.getColor());
            this.pointLight.setBrightness(this.getBrightness());
            this.pointLight.setOffset(this.getLightOffset());
        }
        int attachedTo = this.getAttachedTo();

        final WBenchWorld wBenchWorld = (WBenchWorld) iWorld;
        SceneObject sceneObject = wBenchWorld.getIdMap().get(attachedTo);
        boolean flag = !(sceneObject instanceof WBenchMarkerObject) && (sceneObject != null && attachedTo != this.getId());
        if (flag) {
            WBenchOpenGLRenderer.DebugLinesDrawer().addRequest(DebugLinesDrawer.LineRequest(this.getPosition(), sceneObject.getPositionToAttachLights(), new Vector3f(this.getColor()).mul(6.0f), DebugLinesDrawer.Depth(), DebugLinesDrawer.Depth()));
            if (this.pointLight.getAttachedTo() == null || !this.pointLight.getAttachedTo().equals(sceneObject)) {
                this.pointLight.getAttachedTo().removeLightAttachment(this.pointLight);
                sceneObject.addLightAttachment(this.pointLight);
            }
        } else {
            if (!this.isLightAttached(this.pointLight)) {
                this.addLightAttachment(this.pointLight);
            }
        }
    }

    @Override
    public String toString(boolean textPosition) {
        final int attached = this.getAttachedTo();
        return super.toString(textPosition) + (attached >= 0 ? ("->(" + attached + ")") : "");
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        this.pointLight = new PointLight(this.getPosition(), new Vector3f(1.0f)).on();
        this.pointLight.setActionOnDetach(ILightAttached.ActionOnDetach.KEEP_IN_WORLD);

        WBenchWorld wBenchWorld = (WBenchWorld) iWorld;
        wBenchWorld.addLight(this.pointLight, this);
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        super.onDestroy(iWorld);

        if (this.pointLight != null) {
            WBenchWorld wBenchWorld = (WBenchWorld) iWorld;
            wBenchWorld.removeLight(this.pointLight, this);
        }
    }

    @Override
    public Vector3f textInMenuColor() {
        return new Vector3f(1.0f, 0.0f, 1.0f);
    }

    @Override
    public boolean isLighted() {
        return false;
    }
}
