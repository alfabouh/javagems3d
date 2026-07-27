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
import imgui.type.ImInt;
import javagems3d.graphics.rendering.ui.snapshots.helper.UITrackingHelper;
import javagems3d.help.JGemsHelper;
import javagems3d.system.external.gaming.def.misc.set.GameResourcesSet;
import javagems3d.system.external.mapping.tags.TagID;
import org.jetbrains.annotations.Nullable;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.service.collections.Pair;
import java.util.Set;
import java.util.function.Supplier;

public class TagInt extends TagItem {
    public static final String TYPE_STRING = "TagInt";

    private int value;
    private final int min;
    private final int max;

    public TagInt(int value, int min, int max) {
        super(TagInt.TYPE_STRING);
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public TagInt setValue(int value) {
        this.value = value;
        return this;
    }

    public int getValue() {
        return this.value;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    @Override
    public TagItem copy() {
        return new TagInt(this.getValue(), this.getMin(), this.getMax());
    }

    @Override
    public void ImGuiRendering(TagsContainer tagsContainer, @Nullable SceneObject currentSelected, TagItem tagItem, TagID tagID, Set<Pair<Integer, SceneObject>> sceneObjectsIDSet, @Nullable Supplier<UITrackingHelper> trackingHelper, @Nullable GameResourcesSet gameResourcesSet) {
        TagInt tagInt = (TagInt) tagItem;
        if (trackingHelper != null && currentSelected != null) {
            try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TagIntTAG_" + currentSelected, trackingHelper)) {
                ImInt value = new ImInt(tagInt.getValue());
                if (ImGui.inputInt("##" + tagID.getNormalName(), value, 1)) {
                    uiTrackingHelper.saveSnapshot();
                    tagInt.setValue(JGemsHelper.math().clamp(value.intValue(), tagInt.getMin(), tagInt.getMax()));
                }
            }
        }
    }

    @Override
    public String toString() {
        return "TagInt{" +
                "value=" + value +
                ", min=" + min +
                ", max=" + max +
                '}';
    }
}
