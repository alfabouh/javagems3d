package javagems3d.mapping.tags.items;

import imgui.ImGui;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.mapping.tags.TagID;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.Nullable;
import java.util.Set;

public class TagCheckBoolean extends TagItem {
    public static final String TYPE_STRING = "TagCheckBoolean";

    private boolean flag;

    public TagCheckBoolean(boolean flag) {
        super(TagCheckBoolean.TYPE_STRING);
        this.flag = flag;
    }

    public TagCheckBoolean setFlag(boolean flag) {
        this.flag = flag;
        return this;
    }

    public boolean isFlag() {
        return this.flag;
    }

    @Override
    public TagItem copy() {
        return new TagCheckBoolean(this.isFlag());
    }

    @Override
    public void ImGuiRendering(TagsContainer tagsContainer, @Nullable SceneObject currentSelected, TagItem tagItem, TagID tagID, Set<Pair<Integer, SceneObject>> sceneObjectsIDSet) {
        TagCheckBoolean tagCheckBoolean = (TagCheckBoolean) tagItem;
        boolean value = tagCheckBoolean.isFlag();
        if (ImGui.checkbox("Flag ##" + tagID.getId(), value)) {
            tagCheckBoolean.setFlag(!value);
        }
    }

    @Override
    public String toString() {
        return "TagCheckBoolean{" +
                "flag=" + flag +
                '}';
    }
}
