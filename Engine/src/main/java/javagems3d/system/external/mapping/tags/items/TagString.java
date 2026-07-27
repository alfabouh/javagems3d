/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.system.external.mapping.tags.items;

import imgui.ImGui;
import imgui.type.ImString;
import javagems3d.graphics.rendering.ui.snapshots.helper.UITrackingHelper;
import javagems3d.system.external.gaming.def.misc.set.GameResourcesSet;
import javagems3d.system.external.mapping.tags.TagID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.service.collections.Pair;
import java.util.Set;
import java.util.function.Supplier;

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
    public void ImGuiRendering(TagsContainer tagsContainer, @Nullable SceneObject currentSelected, TagItem tagItem, TagID tagID, Set<Pair<Integer, SceneObject>> sceneObjectsIDSet, @Nullable Supplier<UITrackingHelper> trackingHelper, @Nullable GameResourcesSet gameResourcesSet) {
        if (trackingHelper != null && currentSelected != null) {
            try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TagStringTAG_" + currentSelected, trackingHelper)) {
                if (ImGui.inputText("##" + tagID.getNormalName(), this.value)) {
                    uiTrackingHelper.saveSnapshot();
                    this.setText(value.get());
                }
            }
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
