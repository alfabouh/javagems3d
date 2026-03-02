package javagems3d.system.external.mapping.tags.items;

import imgui.ImGui;
import javagems3d.graphics.rendering.ui.snapshots.helper.UITrackingHelper;
import javagems3d.system.external.mapping.tags.TagID;
import org.jetbrains.annotations.Nullable;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.service.collections.Pair;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Supplier;

public class TagRadioBoolean extends TagItem {
    public static final String TYPE_STRING = "TagRadioBoolean";

    private final Info[] values;

    public TagRadioBoolean(Info... values) {
        super(TagRadioBoolean.TYPE_STRING);
        this.values = values;
    }

    public Info[] getValues() {
        return this.values;
    }

    public Info[] getCopiedValues() {
        Info[] copy = new Info[this.values.length];
        for (int i = 0; i < this.values.length; i++) {
            Info original = this.values[i];
            copy[i] = new Info(original.getName(), original.isFlag());
        }
        return copy;
    }

    @Override
    public TagItem copy() {
        return new TagRadioBoolean(this.getCopiedValues());
    }

    @Override
    public void ImGuiRendering(TagsContainer tagsContainer, @Nullable SceneObject currentSelected, TagItem tagItem, TagID tagID, Set<Pair<Integer, SceneObject>> sceneObjectsIDSet, @Nullable Supplier<UITrackingHelper> trackingHelper) {
        TagRadioBoolean tagRadioBoolean = (TagRadioBoolean) tagItem;
        TagRadioBoolean.Info[] infos = tagRadioBoolean.getValues();
        for (int i = 0; i < infos.length; i++) {
            boolean selected = infos[i].isFlag();
            if (ImGui.radioButton(infos[i].getName(), selected)) {
                if (trackingHelper != null) {
                    trackingHelper.get().takeSnapshot();
                }
                for (int j = 0; j < infos.length; j++) {
                    infos[j].setFlag(j == i);
                }
            }
        }
    }

    public static final class Info {
        private final String name;
        private boolean flag;

        public Info(String name, boolean flag) {
            this.name = name;
            this.flag = flag;
        }

        public String getName() {
            return this.name;
        }

        public Info setFlag(boolean flag) {
            this.flag = flag;
            return this;
        }

        public boolean isFlag() {
            return this.flag;
        }

        @Override
        public String toString() {
            return "Info{" +
                    "name='" + name + '\'' +
                    ", flag=" + flag +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TagRadioBoolean{" +
                "values=" + Arrays.toString(values) +
                '}';
    }
}
