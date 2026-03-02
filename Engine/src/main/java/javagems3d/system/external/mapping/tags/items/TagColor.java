package javagems3d.system.external.mapping.tags.items;

import imgui.ImGui;
import javagems3d.graphics.rendering.ui.snapshots.helper.UITrackingHelper;
import javagems3d.system.external.mapping.tags.TagID;
import javagems3d.system.external.mapping.tags.base.ColorMode;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.service.collections.Pair;

import java.util.Set;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

public class TagColor extends TagItem {
    public static final String TYPE_STRING = "TagColor";

    private final Vector4f colorVector;
    private final ColorMode colorMode;

    public TagColor(ColorMode colorMode, @NotNull Vector4f colorVector) {
        super(TagColor.TYPE_STRING);
        this.colorVector = colorVector;
        this.colorMode = colorMode;
    }

    public ColorMode getColorMode() {
        return this.colorMode;
    }

    public void setColor(Vector4f color) {
        this.getColorVector().set(color);
    }

    public Vector4f getColorVector() {
        return this.colorVector;
    }

    @Override
    public TagItem copy() {
        return new TagColor(this.getColorMode(), new Vector4f(this.getColorVector()));
    }

    @Override
    public void ImGuiRendering(@NotNull TagsContainer tagsContainer, @Nullable SceneObject currentSelected, @NotNull TagItem tagItem, @NotNull TagID tagID, @Nullable Set<Pair<Integer, SceneObject>> sceneObjectsIDSet, @Nullable Supplier<UITrackingHelper> trackingHelper) {
        TagColor tagColor = (TagColor) tagItem;
        Vector4f color = tagColor.getColorVector();
        ColorMode colorMode = tagColor.getColorMode();
        try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TRACK_TAG_colorMode_" + currentSelected, trackingHelper)) {
            if (trackingHelper != null && currentSelected != null) {
                if (colorMode == ColorMode.COLOR3) {
                    float[] colorArray = new float[]{color.x, color.y, color.z};
                    if (ImGui.colorEdit3("##" + tagID.getDescription(), colorArray)) {
                        uiTrackingHelper.saveSnapshot();
                        tagColor.setColor(new Vector4f(colorArray[0], colorArray[1], colorArray[2], color.w));
                    }
                } else {
                    float[] colorArray = new float[]{color.x, color.y, color.z, color.w};
                    if (ImGui.colorEdit4("##" + tagID.getDescription(), colorArray)) {
                        uiTrackingHelper.saveSnapshot();
                        tagColor.setColor(new Vector4f(colorArray[0], colorArray[1], colorArray[2], colorArray[3]));
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "TagColor{" +
                "colorVector=" + colorVector +
                ", colorMode=" + colorMode +
                '}';
    }
}
