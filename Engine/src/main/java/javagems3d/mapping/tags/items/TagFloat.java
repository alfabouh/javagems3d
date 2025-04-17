package javagems3d.mapping.tags.items;

import imgui.ImGui;
import javagems3d.mapping.tags.TagID;
import org.jetbrains.annotations.Nullable;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.system.service.collections.Pair;
import java.util.Set;

public class TagFloat extends TagItem {
    public static final String TYPE_STRING = "TagFloat";

    private float value;
    private final float min;
    private final float max;

    public TagFloat(float value, float min, float max) {
        super(TagFloat.TYPE_STRING);
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public TagFloat setValue(float value) {
        this.value = value;
        return this;
    }

    public float getValue() {
        return this.value;
    }

    public float getMin() {
        return this.min;
    }

    public float getMax() {
        return this.max;
    }

    @Override
    public TagItem copy() {
        return new TagFloat(this.getValue(), this.getMin(), this.getMax());
    }

    @Override
    public void ImGuiRendering(TagsContainer tagsContainer, @Nullable SceneObject currentSelected, TagItem tagItem, TagID tagID, Set<Pair<Integer, SceneObject>> sceneObjectsIDSet) {
        TagFloat tagFloat = (TagFloat) tagItem;
        float[] value = new float[] {tagFloat.getValue()};
        if (ImGui.dragFloat("##" + tagID.getDescription(), value, 0.1f, tagFloat.getMin(), tagFloat.getMax())) {
            tagFloat.setValue(JGemsMathHelper.clamp(value[0], tagFloat.getMin(), tagFloat.getMax()));
        }
    }
}
