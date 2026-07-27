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

import api.application.workbench.resources.data.wbench.properties.WBenchRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.base.RenderProperties;
import javagems3d.system.external.mapping.tags.Tag;
import javagems3d.system.external.mapping.tags.TagsContainer;
import javagems3d.system.external.mapping.tags.base.AxisConstraints;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;
import javagems3d.system.external.mapping.tags.items.TagItem;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.JGemsPathSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WBenchObjectData extends WBenchData {
    private final JGemsPathSource pathToModel;
    private final RenderProperties renderProperties;

    public WBenchObjectData(@Nullable JGemsPathSource pathToModel, @NotNull RenderProperties renderProperties, @NotNull TranslationConstraints translationConstraints) {
        super(new TagsContainer(), translationConstraints);
        this.pathToModel = pathToModel;
        this.renderProperties = renderProperties;
    }

    public WBenchObjectData(@Nullable JGemsPathSource pathToModel, @NotNull RenderProperties renderProperties) {
        this(pathToModel, renderProperties, new TranslationConstraints(AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ, AxisConstraints.AXIS_XYZ));
    }

    public WBenchObjectData(@Nullable JGemsPathSource pathToModel) {
        this(pathToModel, WBenchRenderProperties.getDefault());
    }

    @Override
    @SafeVarargs
    public final WBenchObjectData addTags(Tag<? extends TagItem>... tags) {
        return (WBenchObjectData) super.addTags(tags);
    }

    @Override
    public WBenchObjectData addTag(Tag<? extends TagItem> tag) {
        return (WBenchObjectData) super.addTag(tag);
    }

    public JGemsPathSource getPathToModel() {
        return this.pathToModel;
    }

    public RenderProperties getRenderProperties() {
        return this.renderProperties;
    }

    @SuppressWarnings("all")
    public <T extends RenderProperties> T getRenderPropertiesUnsafeCast() {
        return (T) this.renderProperties;
    }
}
