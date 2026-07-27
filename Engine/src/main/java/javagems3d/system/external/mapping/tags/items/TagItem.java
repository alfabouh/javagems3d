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

import com.google.gson.reflect.TypeToken;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.ui.snapshots.helper.UITrackingHelper;
import javagems3d.system.external.gaming.def.misc.set.GameResourcesSet;
import javagems3d.system.external.mapping.tags.TagID;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.resources.managing.resources.data.ICopyable;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import javagems3d.system.service.files.json.JSONFileManaging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public abstract class TagItem implements ICopyable<TagItem> {
    public static final Map<String, TypeToken<? extends TagItem>> tokensMap = new HashMap<>();

    public static void REGISTER_ALL_TAGS() {
        TagItem.putTypeToken(TagCheckBoolean.TYPE_STRING, new TypeToken<TagCheckBoolean>() {});
        TagItem.putTypeToken(TagColor.TYPE_STRING, new TypeToken<TagColor>() {});
        TagItem.putTypeToken(TagFloat.TYPE_STRING, new TypeToken<TagFloat>() {});
        TagItem.putTypeToken(TagInt.TYPE_STRING, new TypeToken<TagInt>() {});
        TagItem.putTypeToken(TagRadioBoolean.TYPE_STRING, new TypeToken<TagRadioBoolean>() {});
        TagItem.putTypeToken(TagString.TYPE_STRING, new TypeToken<TagString>() {});
        TagItem.putTypeToken(TagObjectsList.TYPE_STRING, new TypeToken<TagObjectsList>() {});
        TagItem.putTypeToken(TagVector.TYPE_STRING, new TypeToken<TagVector>() {});
        TagItem.putTypeToken(TagGameResourcesList.TYPE_STRING, new TypeToken<TagGameResourcesList>() {});
        TagItem.putTypeToken(TagStringOptionsList.TYPE_STRING, new TypeToken<TagStringOptionsList>() {});
    }

    public static void putTypeToken(String typeString, TypeToken<? extends TagItem> token) {
        TagItem.tokensMap.put(typeString, token);
    }

    public static TypeToken<? extends TagItem> getTypeToken(String type) {
        return TagItem.tokensMap.get(type);
    }

    private final String typeString;

    public TagItem(@NotNull String typeString) {
        if (typeString.isEmpty()) {
            throw new JGemsRuntimeException("TagItem " + this.getClass() +" should not have empty type");
        }
        this.typeString = typeString;
        TagsContainer.addLazyItemSerializationRule(this);
    }

    public abstract void ImGuiRendering(TagsContainer tagsContainer, @Nullable SceneObject currentSelected, TagItem tagItem, TagID tagID, Set<Pair<Integer, SceneObject>> sceneObjectsIDSet, @Nullable Supplier<UITrackingHelper> trackingHelper, @Nullable GameResourcesSet gameResourcesSet);

    public @Nullable JSONFileManaging.SerializationRules<? extends TagItem> getSerializationRule() {
        return null;
    }

    public String getTypeString() {
        return this.typeString;
    }
}
