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
    public void ImGuiRendering(TagsContainer tagsContainer, TagItem tagItem, TagID tagID, Set<Pair<Integer, SceneObject>> sceneObjectsIDSet) {
        TagVector tagFloat = (TagVector) tagItem;
        float[] values = new float[] {tagFloat.getValues().x, tagFloat.getValues().y, tagFloat.getValues().z};
        if (ImGui.dragFloat3("##" + tagID.getDescription(), values, 0.1f, tagFloat.getMin(), tagFloat.getMax())) {
        //    tagFloat.setValue(JGemsMathHelper.clamp(value[0], tagFloat.getMin(), tagFloat.getMax()));
        }
    }
}
