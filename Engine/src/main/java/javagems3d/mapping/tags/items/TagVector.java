package javagems3d.mapping.tags.items;

import imgui.ImGui;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.help.JGemsMathHelper;
import javagems3d.mapping.tags.TagID;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.mapping.tags.base.VectorMode;
import javagems3d.system.service.collections.Pair;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jetbrains.annotations.Nullable;
import java.util.Set;

public class TagVector extends TagItem {
    public static final String TYPE_STRING = "TagVector";

    private final VectorMode vectorMode;
    private final Vector4f values;
    private final float min;
    private final float max;

    public TagVector(VectorMode vectorMode, Vector4f values, float min, float max) {
        super(TagVector.TYPE_STRING);
        this.vectorMode = vectorMode;
        this.values = values;
        this.min = min;
        this.max = max;
    }

    public TagVector setValue(Vector4f values) {
        this.getValues().set(values);
        return this;
    }

    public VectorMode getVectorMode() {
        return vectorMode;
    }

    public Vector4f getValues() {
        return this.values;
    }

    public float getMin() {
        return this.min;
    }

    public float getMax() {
        return this.max;
    }

    @Override
    public TagItem copy() {
        return new TagVector(this.getVectorMode(), this.getValues(), this.getMin(), this.getMax());
    }

    @Override
    public void ImGuiRendering(TagsContainer tagsContainer, @Nullable SceneObject currentSelected, TagItem tagItem, TagID tagID, Set<Pair<Integer, SceneObject>> sceneObjectsIDSet) {
        TagVector tagVector = (TagVector) tagItem;
        Vector4f vec = tagVector.getValues();
        float[] values = new float[] {vec.x, vec.y, vec.z, vec.w};
        String label = "##" + tagID.getDescription();

        boolean changed = false;
        switch (tagVector.getVectorMode()) {
            case VEC2F:
                changed = ImGui.dragFloat2(label, values, 0.1f, tagVector.getMin(), tagVector.getMax());
                break;
            case VEC3F:
                changed = ImGui.dragFloat3(label, values, 0.1f, tagVector.getMin(), tagVector.getMax());
                break;
            case VEC4F:
                changed = ImGui.dragFloat4(label, values, 0.1f, tagVector.getMin(), tagVector.getMax());
                break;
        }

        if (changed) {
            float x = JGemsMathHelper.clamp(values[0], tagVector.getMin(), tagVector.getMax());
            float y = JGemsMathHelper.clamp(values[1], tagVector.getMin(), tagVector.getMax());
            float z = JGemsMathHelper.clamp(values[2], tagVector.getMin(), tagVector.getMax());
            float w = JGemsMathHelper.clamp(values[3], tagVector.getMin(), tagVector.getMax());

            vec.set(x, y, z, w);
        }
    }
}
