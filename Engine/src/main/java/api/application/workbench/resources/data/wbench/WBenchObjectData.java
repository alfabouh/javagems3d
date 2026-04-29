package api.application.workbench.resources.data.wbench;

import api.application.workbench.resources.data.wbench.properties.WBenchRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.base.RenderProperties;
import javagems3d.system.external.mapping.tags.Tag;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.AxisConstraints;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;
import javagems3d.system.external.mapping.tags.items.TagItem;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.JGemsPathSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WBenchObjectData extends WBenchData {
    private final JGemsPathSource pathToModel;
    private final RenderProperties renderProperties;

    public WBenchObjectData(@Nullable JGemsPathSource pathToModel, @NotNull RenderProperties renderProperties, @NotNull TranslationConstraints translationConstraints) {
        super(new TagsContainer(), translationConstraints);
        this.pathToModel = pathToModel;
        this.renderProperties = renderProperties;
    }

    public WBenchObjectData(@Nullable JGemsPathSource pathToModel, @NotNull RenderProperties renderProperties) {
        this(pathToModel, renderProperties, new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ));
    }

    public WBenchObjectData(@Nullable JGemsPathSource pathToModel) {
        this(pathToModel, WBenchRenderProperties.getDefault());
    }

    @Override
    @SafeVarargs
    public final WBenchObjectData addTags(Tag<? extends TagItem>... tags) {
        return (WBenchObjectData) super.addTags(tags);
    }

    @Override
    public WBenchObjectData addTag(Tag<? extends TagItem> tag) {
        return (WBenchObjectData) super.addTag(tag);
    }

    public JGemsPathSource getPathToModel() {
        return this.pathToModel;
    }

    public RenderProperties getRenderProperties() {
        return this.renderProperties;
    }

    @SuppressWarnings("all")
    public <T extends RenderProperties> T getRenderPropertiesUnsafeCast() {
        return (T) this.renderProperties;
    }
}
