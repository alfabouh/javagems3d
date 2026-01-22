package workbench.graphics.objects.templates;

import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.mapping.processing.ExternalMapProcessor;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.tags.base.TranslationConstraints;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import workbench.graphics.objects.WBenchCommonObject;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.scene.world.WBenchWorld;

public class WBenchObjectTemplate extends WBenchTemplate {
    protected MeshGroup meshGroup;
    protected RenderAttributes renderAttributes;
    protected TagsContainer tagsContainer;
    protected TranslationConstraints translationConstraints;

    public WBenchObjectTemplate(@NotNull WBenchObject.ID objectId, MeshGroup meshGroup, RenderAttributes renderAttributes, TagsContainer tagsContainer, TranslationConstraints translationConstraints) {
        super(objectId);
        this.meshGroup = meshGroup;
        this.renderAttributes = renderAttributes;
        this.tagsContainer = tagsContainer;
        this.translationConstraints = translationConstraints;
    }

    public WBenchObject createObject(@NotNull WBenchWorld world, @Nullable TagsContainer tagsContainer) {
        return new WBenchCommonObject(world, this, tagsContainer);
    }

    public TranslationConstraints getTranslationConstraints() {
        return this.translationConstraints;
    }

    public TagsContainer getTagsContainer() {
        return this.tagsContainer;
    }

    public MeshGroup getMeshGroup() {
        return this.meshGroup;
    }

    public RenderAttributes getRenderAttributes() {
        return this.renderAttributes;
    }
}