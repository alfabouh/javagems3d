package javagems3d.system.external.mapping.tags.items;

import imgui.ImGui;
import imgui.type.ImInt;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.ui.snapshots.helper.UITrackingHelper;
import javagems3d.system.external.gaming.def.IAsset;
import javagems3d.system.external.gaming.def.misc.set.GameResourcesSet;
import javagems3d.system.external.gaming.def.util.GameResourceAssetsFolder;
import javagems3d.system.external.mapping.tags.TagID;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.ResourceType;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.files.VirtualObjectsFolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public class TagStringOptionsList extends TagItem {
    public static final String TYPE_STRING = "TagStringOptionsList";

    private String value;
    private final List<String> options;

    public TagStringOptionsList(String value, List<String> options) {
        super(TagStringOptionsList.TYPE_STRING);
        this.value = value;
        this.options = new ArrayList<>(options);
    }

    public String getValue() {
        return this.value;
    }

    public TagStringOptionsList setValue(String value) {
        this.value = value;
        return this;
    }

    public List<String> getOptions() {
        return this.options;
    }

    @Override
    public TagItem copy() {
        return new TagStringOptionsList(this.value, this.options);
    }

    @Override
    public void ImGuiRendering(TagsContainer tagsContainer, @Nullable SceneObject currentSelected, TagItem tagItem, TagID tagID, Set<Pair<Integer, SceneObject>> sceneObjectsIDSet, @Nullable Supplier<UITrackingHelper> trackingHelper, @Nullable GameResourcesSet gameResourcesSet) {
        TagStringOptionsList tagList = (TagStringOptionsList) tagItem;
        int currentIndex = 0;
        for (int i = 0; i < tagList.options.size(); i++) {
            if (Objects.equals(tagList.options.get(i), tagList.value)) {
                currentIndex = i;
                break;
            }
        }
        String[] items = tagList.options.toArray(new String[0]);
        ImInt selected = new ImInt(currentIndex);
        if (ImGui.combo("##" + tagID.getNormalName(), selected, items, 10)) {
            if (trackingHelper != null) {
                trackingHelper.get().takeSnapshot();
            }
            int index = selected.get();
            if (index >= 0 && index < tagList.options.size()) {
                tagList.value = tagList.options.get(index);
            }
        }
    }

    @Override
    public String toString() {
        return "TagStringOptionsList{" +
                "value='" + this.value + '\'' +
                ", options=" + this.options +
                '}';
    }
}