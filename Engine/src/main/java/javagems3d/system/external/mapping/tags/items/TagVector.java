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
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.ui.snapshots.helper.UITrackingHelper;
import javagems3d.help.JGemsHelper;
import javagems3d.system.external.gaming.def.misc.set.GameResourcesSet;
import javagems3d.system.external.mapping.tags.TagID;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.VectorMode;
import javagems3d.system.service.collections.Pair;
import org.joml.Vector4f;
import org.jetbrains.annotations.Nullable;
import java.util.Set;
import java.util.function.Supplier;

public class TagVector extends TagItem {
    public static final String TYPE_STRING = "TagVector";

    private final VectorMode vectorMode;
    private final Vector4f values;
    private final float min;
    private final float max;

    public TagVector(VectorMode vectorMode, Vector4f values, float min, float max) {
        super(TagVector.TYPE_STRING);
        this.vectorMode = vectorMode;
        this.values = values;
        this.min = min;
        this.max = max;
    }

    public TagVector setValue(Vector4f values) {
        this.getValues().set(values);
        return this;
    }

    public VectorMode getVectorMode() {
        return vectorMode;
    }

    public Vector4f getValues() {
        return new Vector4f(this.values);
    }

    public float getMin() {
        return this.min;
    }

    public float getMax() {
        return this.max;
    }

    @Override
    public TagItem copy() {
        return new TagVector(this.getVectorMode(), this.getValues(), this.getMin(), this.getMax());
    }

    @Override
    public void ImGuiRendering(TagsContainer tagsContainer, @Nullable SceneObject currentSelected, TagItem tagItem, TagID tagID, Set<Pair<Integer, SceneObject>> sceneObjectsIDSet, @Nullable Supplier<UITrackingHelper> trackingHelper, @Nullable GameResourcesSet gameResourcesSet) {
        TagVector tagVector = (TagVector) tagItem;
        float[] values = new float[] {this.values.x, this.values.y, this.values.z, this.values.w};
        String label = "##" + tagID.getNormalName();

        switch (tagVector.getVectorMode()) {
            case VEC2F:
                try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TagVectorTAG_" + currentSelected, trackingHelper)) {
                    if (ImGui.dragFloat2(label, values, 0.01f, tagVector.getMin(), tagVector.getMax())) {
                        float x = JGemsHelper.math().clamp(values[0], tagVector.getMin(), tagVector.getMax());
                        float y = JGemsHelper.math().clamp(values[1], tagVector.getMin(), tagVector.getMax());
                        float z = JGemsHelper.math().clamp(values[2], tagVector.getMin(), tagVector.getMax());
                        float w = JGemsHelper.math().clamp(values[3], tagVector.getMin(), tagVector.getMax());
                        uiTrackingHelper.saveSnapshot();
                        this.values.set(x, y, z, w);
                    }
                }
                break;
            case VEC3F:
                try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TagVectorTAG_" + currentSelected, trackingHelper)) {
                    if (ImGui.dragFloat3(label, values, 0.01f, tagVector.getMin(), tagVector.getMax())) {
                        float x = JGemsHelper.math().clamp(values[0], tagVector.getMin(), tagVector.getMax());
                        float y = JGemsHelper.math().clamp(values[1], tagVector.getMin(), tagVector.getMax());
                        float z = JGemsHelper.math().clamp(values[2], tagVector.getMin(), tagVector.getMax());
                        float w = JGemsHelper.math().clamp(values[3], tagVector.getMin(), tagVector.getMax());
                        uiTrackingHelper.saveSnapshot();
                        this.values.set(x, y, z, w);
                    }
                }
                break;
            case VEC4F:
                try (UITrackingHelper uiTrackingHelper = UITrackingHelper.create("TagVectorTAG_" + currentSelected, trackingHelper)) {
                    if (ImGui.dragFloat4(label, values, 0.01f, tagVector.getMin(), tagVector.getMax())) {
                        float x = JGemsHelper.math().clamp(values[0], tagVector.getMin(), tagVector.getMax());
                        float y = JGemsHelper.math().clamp(values[1], tagVector.getMin(), tagVector.getMax());
                        float z = JGemsHelper.math().clamp(values[2], tagVector.getMin(), tagVector.getMax());
                        float w = JGemsHelper.math().clamp(values[3], tagVector.getMin(), tagVector.getMax());
                        uiTrackingHelper.saveSnapshot();
                        this.values.set(x, y, z, w);
                    }
                }
                break;
        }
    }

    @Override
    public String toString() {
        return "TagVector{" +
                "vectorMode=" + vectorMode +
                ", values=" + values +
                ", min=" + min +
                ", max=" + max +
                '}';
    }
}
