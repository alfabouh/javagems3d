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

package api.application.workbench.resources.data.wbench;

import api.application.workbench.resources.data.DefaultMarker;
import javagems3d.system.external.mapping.tags.Tag;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;
import javagems3d.system.external.mapping.tags.items.TagItem;
import javagems3d.system.service.files.JGemsPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class WBenchMarkerData extends WBenchData {
    private final JGemsPath pathToModel;
    private final DefaultMarker defaultMarker;
    private final Vector3f color;
    private final boolean transparent;

    public WBenchMarkerData(@NotNull JGemsPath pathToModel, @NotNull TranslationConstraints translationConstraints, @Nullable Vector3f color, boolean transparent) {
        super(new TagsContainer(), translationConstraints);
        this.color = color;
        this.pathToModel = pathToModel;
        this.transparent = transparent;
        this.defaultMarker = null;
    }

    public WBenchMarkerData(@NotNull DefaultMarker defaultMarker, @Nullable Vector3f color, boolean transparent) {
        super(new TagsContainer(), defaultMarker.getTranslationConstraints());
        this.color = color;
        this.pathToModel = null;
        this.transparent = transparent;
        this.defaultMarker = defaultMarker;
    }

    @Override
    @SafeVarargs
    public final WBenchMarkerData addTags(Tag<? extends TagItem>... tags) {
        return (WBenchMarkerData) super.addTags(tags);
    }

    @Override
    public WBenchMarkerData addTag(Tag<? extends TagItem> tag) {
        return (WBenchMarkerData) super.addTag(tag);
    }

    public boolean isTransparent() {
        return this.transparent;
    }

    public Vector3f getColor() {
        return this.color;
    }

    public DefaultMarker getDefaultMarker() {
        return this.defaultMarker;
    }

    public JGemsPath getPathToModel() {
        return this.pathToModel;
    }
}
