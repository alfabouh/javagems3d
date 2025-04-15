package api.application.workbench.resources.data.wbench;

import api.application.workbench.resources.data.DefaultMarker;
import javagems3d.mapping.tags.Tag;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.tags.base.AxisConstraints;
import javagems3d.mapping.tags.base.TranslationConstraints;
import javagems3d.mapping.tags.items.TagItem;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class WBenchMarkerData extends WBenchData {
    private final JGemsPath pathToModel;
    private final DefaultMarker defaultMarker;
    private final Vector3f color;
    private final boolean transparent;

    public WBenchMarkerData(@NotNull JGemsPath pathToModel, @NotNull TranslationConstraints translationConstraints, @Nullable Vector3f color, boolean transparent) {
        super(new TagsContainer(), translationConstraints);
        this.color = color;
        this.pathToModel = pathToModel;
        this.transparent = transparent;
        this.defaultMarker = null;
    }

    public WBenchMarkerData(@NotNull DefaultMarker defaultMarker, @Nullable Vector3f color, boolean transparent) {
        super(new TagsContainer(), defaultMarker.getTranslationConstraints());
        this.color = color;
        this.pathToModel = null;
        this.transparent = transparent;
        this.defaultMarker = defaultMarker;
    }

    @Override
    @SafeVarargs
    public final WBenchMarkerData addTags(Tag<? extends TagItem>... tags) {
        return (WBenchMarkerData) super.addTags(tags);
    }

    @Override
    public WBenchMarkerData addTag(Tag<? extends TagItem> tag) {
        return (WBenchMarkerData) super.addTag(tag);
    }

    public boolean isTransparent() {
        return this.transparent;
    }

    public Vector3f getColor() {
        return this.color;
    }

    public DefaultMarker getDefaultMarker() {
        return this.defaultMarker;
    }

    public JGemsPath getPathToModel() {
        return this.pathToModel;
    }
}
