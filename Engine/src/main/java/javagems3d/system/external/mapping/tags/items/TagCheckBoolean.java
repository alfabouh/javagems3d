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
import javagems3d.system.external.gaming.def.misc.set.GameResourcesSet;
import javagems3d.system.external.mapping.tags.TagID;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.Nullable;
import java.util.Set;
import java.util.function.Supplier;

public class TagCheckBoolean extends TagItem {
    public static final String TYPE_STRING = "TagCheckBoolean";

    private boolean flag;

    public TagCheckBoolean(boolean flag) {
        super(TagCheckBoolean.TYPE_STRING);
        this.flag = flag;
    }

    public TagCheckBoolean setFlag(boolean flag) {
        this.flag = flag;
        return this;
    }

    public boolean isFlag() {
        return this.flag;
    }

    @Override
    public TagItem copy() {
        return new TagCheckBoolean(this.isFlag());
    }

    @Override
    public void ImGuiRendering(TagsContainer tagsContainer, @Nullable SceneObject currentSelected, TagItem tagItem, TagID tagID, Set<Pair<Integer, SceneObject>> sceneObjectsIDSet, @Nullable Supplier<UITrackingHelper> trackingHelper, @Nullable GameResourcesSet gameResourcesSet) {
        TagCheckBoolean tagCheckBoolean = (TagCheckBoolean) tagItem;
        boolean value = tagCheckBoolean.isFlag();
        if (ImGui.checkbox("True/False ##" + tagID.getId(), value)) {
            if (trackingHelper != null) {
                trackingHelper.get().takeSnapshot();
            }
            tagCheckBoolean.setFlag(!value);
        }
    }

    @Override
    public String toString() {
        return "TagCheckBoolean{" +
                "flag=" + flag +
                '}';
    }
}
