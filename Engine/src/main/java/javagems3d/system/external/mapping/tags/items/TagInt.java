package javagems3d.system.external.mapping.tags.items;

import imgui.ImGui;
import javagems3d.graphics.rendering.ui.snapshots.helper.UITrackingHelper;
import javagems3d.help.JGemsHelper;
import javagems3d.system.external.mapping.tags.TagID;
import org.jetbrains.annotations.Nullable;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.service.collections.Pair;
import java.util.Set;
import java.util.function.Supplier;

public class TagInt extends TagItem {
    public static final String TYPE_STRING = "TagInt";

    private int value;
    private final int min;
    private final int max;

    public TagInt(int value, int min, int max) {
        super(TagInt.TYPE_STRING);
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public TagInt setValue(int value) {
        this.value = value;
        return this;
    }

    public int getValue() {
        return this.value;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    @Override
    public TagItem copy() {
        return new TagInt(this.getValue(), this.getMin(), this.getMax());
    }

    @Override
    public void ImGuiRendering(TagsContainer tagsContainer, @Nullable SceneObject currentSelected, TagItem tagItem, TagID tagID, Set<Pair<Integer, SceneObject>> sceneObjectsIDSet, @Nullable Supplier<UITrackingHelper> trackingHelper) {
        TagInt tagInt = (TagInt) tagItem;
        if (trackingHelper != null && currentSelected != null) {
            try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TagIntTAG_" + currentSelected, trackingHelper)) {
                int[] value = new int[]{tagInt.getValue()};
                if (ImGui.dragInt("##" + tagID.getDescription(), value, 1, tagInt.getMin(), tagInt.getMax())) {
                    uiTrackingHelper.saveSnapshot();
                    tagInt.setValue(JGemsHelper.math().clamp(value[0], tagInt.getMin(), tagInt.getMax()));
                }
            }
        }
    }

    @Override
    public String toString() {
        return "TagInt{" +
                "value=" + value +
                ", min=" + min +
                ", max=" + max +
                '}';
    }
}
