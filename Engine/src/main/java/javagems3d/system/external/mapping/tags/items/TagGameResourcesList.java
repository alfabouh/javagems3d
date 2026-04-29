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
import java.util.Set;
import java.util.function.Supplier;

public class TagGameResourcesList extends TagItem {
    public static final String TYPE_STRING = "TagGameResourcesList";
    private String value;
    private ResourceType resourceType;

    public TagGameResourcesList(String value, ResourceType resourceType) {
        super(TagGameResourcesList.TYPE_STRING);
        this.value = value;
        this.resourceType = resourceType;
    }

    public String getValue() {
        return this.value;
    }

    public TagGameResourcesList setValue(String value) {
        this.value = value;
        return this;
    }

    public ResourceType getResourceType() {
        return this.resourceType;
    }

    public TagGameResourcesList setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
        return this;
    }

    @Override
    public TagItem copy() {
        return new TagGameResourcesList(this.getValue(), this.getResourceType());
    }

    @Override
    public void ImGuiRendering(TagsContainer tagsContainer, @Nullable SceneObject currentSelected, TagItem tagItem, TagID tagID, Set<Pair<Integer, SceneObject>> sceneObjectsIDSet, @Nullable Supplier<UITrackingHelper> trackingHelper, @Nullable GameResourcesSet gameResourcesSet) {
        if (gameResourcesSet == null) {
            ImGui.text("Resources not loaded");
            return;
        }
        TagGameResourcesList tagList = (TagGameResourcesList) tagItem;
        GameResourceAssetsFolder<? extends IAsset> rootFolder = switch (tagList.getResourceType()) {
            case MODEL -> gameResourcesSet.models();
            case TEXTURE -> gameResourcesSet.textures();
            case SOUND -> gameResourcesSet.sounds();
        };
        List<String> labels = new ArrayList<>();
        List<String> values = new ArrayList<>();
        labels.add("None");
        values.add("");
        this.collectResources(rootFolder, labels, values);
        int currentIndex = 0;
        String currentValue = tagList.getValue();
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).equals(currentValue)) {
                currentIndex = i;
                break;
            }
        }
        String[] items = labels.toArray(new String[0]);
        ImInt curr = new ImInt(currentIndex);
        if (ImGui.combo("##" + tagID.getNormalName(), curr, items, 10)) {
            if (trackingHelper != null) {
                trackingHelper.get().takeSnapshot();
            }
            int selectedIndex = curr.get();
            if (selectedIndex >= 0 && selectedIndex < values.size()) {
                tagList.setValue(values.get(selectedIndex));
            }
        }
    }

    private void collectResources(VirtualObjectsFolder<? extends IAsset> folder, List<String> labels, List<String> values) {
        for (IAsset asset : folder.getObjectsThere()) {
            String fullPath = folder.getHierarchy() + "/" + asset.name();
            labels.add(fullPath);
            values.add(fullPath);
        }
        for (VirtualObjectsFolder<? extends IAsset> child : folder.getFoldersThere()) {
            this.collectResources(child, labels, values);
        }
    }

    @Override
    public String toString() {
        return "TagGameResourcesList{" +
                "value='" + value + '\'' +
                ", resourceType=" + resourceType +
                '}';
    }
}
