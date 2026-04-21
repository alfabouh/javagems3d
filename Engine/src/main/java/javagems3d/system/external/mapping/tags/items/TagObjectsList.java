package javagems3d.system.external.mapping.tags.items;

import imgui.ImGui;
import imgui.type.ImInt;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.ui.snapshots.helper.UITrackingHelper;
import javagems3d.system.external.mapping.tags.TagID;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class TagObjectsList extends TagItem {
    public static final String TYPE_STRING = "TagObjectsList";
    private int value;

    protected TagObjectsList(int value) {
        super(TagObjectsList.TYPE_STRING);
        this.value = value;
    }

    public TagObjectsList() {
        super(TagObjectsList.TYPE_STRING);
        this.value = -1;
    }

    public TagObjectsList setValue(int value) {
        this.value = value;
        return this;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public TagItem copy() {
        return new TagObjectsList(this.getValue());
    }

    @Override
    public void ImGuiRendering(TagsContainer tagsContainer, @Nullable SceneObject currentSelected, TagItem tagItem, TagID tagID, Set<Pair<Integer, SceneObject>> sceneObjectsIDSet, @Nullable Supplier<UITrackingHelper> trackingHelper) {
        TagObjectsList tagList = (TagObjectsList) tagItem;

        List<Pair<Integer, SceneObject>> objectList = new ArrayList<>(sceneObjectsIDSet);
        List<String> labels = new ArrayList<>();
        labels.add("None");
        int currentIndex = 0;

        for (int i = 0; i < objectList.size(); i++) {
            Pair<Integer, SceneObject> pair = objectList.get(i);
            labels.add(pair.second().toString());

            if (pair.first().equals(tagList.getValue())) {
                currentIndex = i + 1;
            }
        }

        String[] items = labels.toArray(new String[0]);

        ImInt curr = new ImInt(currentIndex);
        if (ImGui.combo("##" + tagID.getNormalName(), curr, items, 6)) {
            if (trackingHelper != null) {
                trackingHelper.get().takeSnapshot();
            }
            currentIndex = curr.get();
            if (currentIndex == 0) {
                tagList.setValue(-1);
            } else {
                tagList.setValue(objectList.get(currentIndex - 1).first());
            }
        }
    }

    @Override
    public String toString() {
        return "TagObjectsList{}";
    }
}
