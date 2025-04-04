package workbench.graphics.objects;

import api.application.workbench.resources.data.wbench.properties.WBenchRenderProperties;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.mapping.tags.Tag;
import javagems3d.mapping.tags.TagID;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.tags.base.AxisConstraints;
import javagems3d.mapping.tags.base.Colors;
import javagems3d.mapping.tags.base.TranslationConstraints;
import javagems3d.mapping.tags.items.TagColor;
import javagems3d.mapping.tags.items.TagFloat;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.basic.IWorldTicked;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;
import workbench.graphics.fabrics.MarkerSimpleRenderFabric;
import workbench.graphics.scene.world.WBenchWorld;
import workbench.resources.WBenchResourceManager;

public class WBenchPointLightObject extends WBenchMarkerObject {
    private PointLight pointLight;

    protected WBenchPointLightObject(@NotNull String name, @NotNull WBenchWorld wBenchWorld, @Nullable MeshStructure3D<?> meshStructure3D, @NotNull RenderAttributes renderAttributes, @NotNull TagsContainer tagsContainer, @NotNull TranslationConstraints translationConstraints, @NotNull Vector3f color, boolean transparent) {
        super(name, wBenchWorld, meshStructure3D, renderAttributes, tagsContainer, translationConstraints, color, transparent);
    }

    public static WBenchPointLightObject create(@NotNull String name, @NotNull WBenchWorld wBenchWorld) {
        final MeshStructure3D<?> meshStructure3D = WBenchResourceManager.localModelAssets.markerCube;
        final RenderAttributes renderAttributes = new RenderAttributes(RenderTable.getDirect(), WBenchRenderProperties.getDefault());
        renderAttributes.getProperties().setValueBool(JGemsRenderProperties.KEY_SHADOW_CASTER, false);
        renderAttributes.getRenderTable().setMatch(Pipeline.SCENE, new RenderTable.Data(WBenchResourceManager.localShaderAssets.simple, new MarkerSimpleRenderFabric(Stage.FORWARD)));
        final TranslationConstraints translationConstraints = new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.NULL, AxisConstraints.NULL);

        final Tag<TagColor> colorTag = Tag.create(TagID.DEFAULT.COLOR3, new TagColor(Colors.COLOR3, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f)));
        final Tag<TagFloat> brightnessTag = Tag.create(TagID.DEFAULT.BRIGHTNESS, new TagFloat(1.0f, 0.0f, 24.0f));

        final TagsContainer tagsContainer = new TagsContainer();
        tagsContainer.addTag(colorTag);
        tagsContainer.addTag(brightnessTag);

        return new WBenchPointLightObject(name, wBenchWorld, meshStructure3D, renderAttributes, tagsContainer, translationConstraints, new Vector3f(1.0f), false);
    }

    @Override
    public Vector3f getColor() {
        Vector4f tagColor = this.getTagsContainer().getTag(TagID.DEFAULT.COLOR3).<TagColor>getTagItemUnsafeCast().getColorVector();
        return new Vector3f(tagColor.x, tagColor.y, tagColor.z);
    }

    public float getBrightness() {
        return this.getTagsContainer().getTag(TagID.DEFAULT.BRIGHTNESS).<TagFloat>getTagItemUnsafeCast().getValue();
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
        if (this.pointLight != null) {
            this.pointLight.setLightColor(this.getColor());
            this.pointLight.setBrightness(this.getBrightness());
        }
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        this.pointLight = new PointLight(this.getPosition(), new Vector3f(1.0f)).on();
        WBenchWorld wBenchWorld = (WBenchWorld) iWorld;
        wBenchWorld.addItemLight(this, this.pointLight);
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        super.onDestroy(iWorld);

        if (this.pointLight != null) {
            WBenchWorld wBenchWorld = (WBenchWorld) iWorld;
            wBenchWorld.removeItemLight(this, this.pointLight);
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
