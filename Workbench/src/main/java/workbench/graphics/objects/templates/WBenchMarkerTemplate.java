package workbench.graphics.objects.templates;

import api.application.workbench.resources.data.wbench.properties.WBenchRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Redirections;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import workbench.graphics.fabrics.MarkerSimpleRenderFabric;
import workbench.graphics.fabrics.MarkerSimpleTransparentRenderFabric;
import workbench.graphics.objects.WBenchMarkerObject;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.scene.world.WBenchWorld;
import workbench.resources.WBenchResourceManager;

public class WBenchMarkerTemplate extends WBenchObjectTemplate {
    private final Vector3f color;
    private final boolean transparent;

    public WBenchMarkerTemplate(@NotNull WBenchObject.ID objectId, MeshGroup meshGroup, TagsContainer tagsContainer, TranslationConstraints translationConstraints, Vector3f color, boolean transparent) {
        super(objectId, meshGroup, null, tagsContainer, translationConstraints);
        this.color = color;
        this.transparent = transparent;
        this.renderAttributes = this.createRenderAttributes(transparent);
    }

    @Override
    public WBenchMarkerObject createObject(@NotNull WBenchWorld world, @Nullable TagsContainer overridedTagsContainer) {
        return new WBenchMarkerObject(world, this, overridedTagsContainer, this.getColor(), this.isTransparent());
    }

    protected RenderAttributes createRenderAttributes(boolean transparent) {
        RenderAttributes renderAttributes = new RenderAttributes(RenderTable.getDirect(), WBenchRenderProperties.getDefault());
        renderAttributes.getProperties().setValueBool(JGemsRenderProperties.KEY_SHADOW_CASTER, false);
        if (transparent) {
            renderAttributes.getRenderTable().setRedirection(Redirections.SCENE__IN__TRANSPARENCY);
            renderAttributes.getRenderTable().setMatch(Pipeline.TRANSPARENCY, new RenderTable.Data(WBenchResourceManager.localShaderAssets.weighted_oit_simple, new MarkerSimpleTransparentRenderFabric(Stage.FORWARD)));
        } else {
            renderAttributes.getRenderTable().setMatch(Pipeline.SCENE, new RenderTable.Data(WBenchResourceManager.localShaderAssets.simple, new MarkerSimpleRenderFabric(Stage.FORWARD)));
        }
        return renderAttributes;
    }

    public boolean isTransparent() {
        return this.transparent;
    }

    public Vector3f getColor() {
        return this.color;
    }
}
