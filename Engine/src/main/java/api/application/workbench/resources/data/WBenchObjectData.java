package api.application.workbench.resources.data;

import api.application.workbench.resources.data.properties.WBenchRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.base.RenderProperties;
import javagems3d.mapping.tags.Tag;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.tags.base.AxisConstraints;
import javagems3d.mapping.tags.base.TranslationConstraints;
import javagems3d.mapping.tags.items.TagItem;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;

public final class WBenchObjectData {
    private final JGemsPath pathToModel;
    private final RenderProperties renderProperties;
    private final TagsContainer tagsContainer;
    private final TranslationConstraints translationConstraints;

    public WBenchObjectData(@NotNull JGemsPath pathToModel, @NotNull RenderProperties renderProperties, @NotNull TranslationConstraints translationConstraints) {
        this.pathToModel = pathToModel;
        this.renderProperties = renderProperties;
        this.tagsContainer = new TagsContainer();

        this.translationConstraints = translationConstraints;
    }

    public WBenchObjectData(@NotNull JGemsPath pathToModel, @NotNull RenderProperties renderProperties) {
        this(pathToModel, renderProperties, new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ));
    }

    public WBenchObjectData(@NotNull JGemsPath pathToModel) {
        this(pathToModel, WBenchRenderProperties.getDefault());
    }

    @SafeVarargs
    public final WBenchObjectData addTags(Tag<? extends TagItem>... tags) {
        for (Tag<? extends TagItem> tag : tags) {
            this.addTag(tag);
        }
        return this;
    }

    public WBenchObjectData addTag(Tag<? extends TagItem> tag) {
        this.getTagsContainer().addTag(tag);
        return this;
    }

    public TranslationConstraints getTranslationConstraints() {
        return this.translationConstraints;
    }

    public TagsContainer getTagsContainer() {
        return this.tagsContainer;
    }

    public JGemsPath getPathToModel() {
        return this.pathToModel;
    }

    public RenderProperties getRenderProperties() {
        return this.renderProperties;
    }
}
