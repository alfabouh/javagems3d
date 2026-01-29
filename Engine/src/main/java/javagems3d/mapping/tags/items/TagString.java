package javagems3d.mapping.tags.items;

import imgui.ImGui;
import imgui.type.ImString;
import javagems3d.mapping.tags.TagID;
import javagems3d.system.service.args.ArbitraryArguments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.mapping.tags.TagID;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.system.service.collections.Pair;
import java.util.Set;

public class TagString extends TagItem {
    public static final String TYPE_STRING = "TagString";

    private String text;
    private transient final ImString value;

    public TagString(@NotNull String text) {
        super(TagString.TYPE_STRING);
        this.value = new ImString(32);
        this.text = text;
    }

    @Override
    public void ImGuiRendering(TagsContainer tagsContainer, @Nullable SceneObject currentSelected, TagItem tagItem, TagID tagID, Set<Pair<Integer, SceneObject>> sceneObjectsIDSet) {
        if (ImGui.inputText("##" + tagID.getDescription(), this.value)) {
            this.setText(value.get());
        }
    }

    public TagString setText(String text) {
        this.text = text;
        return this;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public TagItem copy() {
        return new TagString(this.getText());
    }

    @Override
    public String toString() {
        return "TagString{" +
                "text='" + text + '\'' +
                ", value=" + value +
                '}';
    }
}
