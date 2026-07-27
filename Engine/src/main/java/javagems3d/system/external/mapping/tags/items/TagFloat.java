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

public class TagFloat extends TagItem {
    public static final String TYPE_STRING = "TagFloat";

    private float value;
    private final float min;
    private final float max;

    public TagFloat(float value, float min, float max) {
        super(TagFloat.TYPE_STRING);
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public TagFloat setValue(float value) {
        this.value = value;
        return this;
    }

    public float getValue() {
        return this.value;
    }

    public float getMin() {
        return this.min;
    }

    public float getMax() {
        return this.max;
    }

    @Override
    public TagItem copy() {
        return new TagFloat(this.getValue(), this.getMin(), this.getMax());
    }

    @Override
    public void ImGuiRendering(TagsContainer tagsContainer, @Nullable SceneObject currentSelected, TagItem tagItem, TagID tagID, Set<Pair<Integer, SceneObject>> sceneObjectsIDSet, @Nullable Supplier<UITrackingHelper> trackingHelper, @Nullable GameResourcesSet gameResourcesSet) {
        TagFloat tagFloat = (TagFloat) tagItem;
        if (trackingHelper != null && currentSelected != null) {
            try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TagFloatTAG_" + currentSelected, trackingHelper)) {
                float[] value = new float[]{tagFloat.getValue()};
                if (ImGui.dragFloat("##" + tagID.getNormalName(), value, 0.01f, tagFloat.getMin(), tagFloat.getMax())) {
                    uiTrackingHelper.saveSnapshot();
                    tagFloat.setValue(JGemsHelper.math().clamp(value[0], tagFloat.getMin(), tagFloat.getMax()));
                }
            }
        }
    }

    @Override
    public String toString() {
        return "TagFloat{" +
                "value=" + value +
                ", min=" + min +
                ", max=" + max +
                '}';
    }
}
