package javagems3d.mapping.tags.items;

import imgui.ImGui;
import imgui.type.ImInt;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.help.JGemsMathHelper;
import javagems3d.mapping.tags.TagID;
import javagems3d.mapping.tags.TagsContainer;
import javagems3d.system.service.collections.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    public void ImGuiRendering(TagsContainer tagsContainer, TagItem tagItem, TagID tagID, Set<Pair<Integer, SceneObject>> sceneObjectsIDSet) {
        TagObjectsList tagList = (TagObjectsList) tagItem;

        List<Pair<Integer, SceneObject>> objectList = new ArrayList<>(sceneObjectsIDSet);
        List<String> labels = new ArrayList<>();
        labels.add("None");
        int currentIndex = 0;

        for (int i = 0; i < objectList.size(); i++) {
            Pair<Integer, SceneObject> pair = objectList.get(i);
            labels.add(pair.getSecond().toString());

            if (pair.getFirst().equals(tagList.getValue())) {
                currentIndex = i + 1;
            }
        }

        String[] items = labels.toArray(new String[0]);

        ImInt curr = new ImInt(currentIndex);
        if (ImGui.combo("##" + tagID.getDescription(), curr, items, 6)) {
            currentIndex = curr.get();
            if (currentIndex == 0) {
                tagList.setValue(-1);
            } else {
                tagList.setValue(objectList.get(currentIndex - 1).getFirst());
            }
        }
    }
}
