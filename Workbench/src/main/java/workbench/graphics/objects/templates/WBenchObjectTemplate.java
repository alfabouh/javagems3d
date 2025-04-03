package workbench.graphics.objects.templates;

import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.tags.base.TranslationConstraints;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import org.jetbrains.annotations.NotNull;
import workbench.graphics.objects.WBenchCommonObject;
import workbench.graphics.objects.WBenchObject;
import workbench.graphics.scene.world.WBenchWorld;

public class WBenchObjectTemplate {
    protected final String id;
    protected MeshGroup meshGroup;
    protected RenderAttributes renderAttributes;
    protected TagsContainer tagsContainer;
    protected TranslationConstraints translationConstraints;

    public WBenchObjectTemplate(String id, MeshGroup meshGroup, RenderAttributes renderAttributes, TagsContainer tagsContainer, TranslationConstraints translationConstraints) {
        this.id = id;
        this.meshGroup = meshGroup;
        this.renderAttributes = renderAttributes;
        this.tagsContainer = tagsContainer;
        this.translationConstraints = translationConstraints;
    }

    public WBenchObject createObject(@NotNull WBenchWorld world) {
        return new WBenchCommonObject(world, this);
    }

    public TranslationConstraints getTranslationConstraints() {
        return this.translationConstraints;
    }

    public TagsContainer getTagsContainer() {
        return this.tagsContainer;
    }

    public String getId() {
        return this.id;
    }

    public MeshGroup getMeshGroup() {
        return this.meshGroup;
    }

    public RenderAttributes getRenderAttributes() {
        return this.renderAttributes;
    }
}