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

package api.application.workbench.resources.data.jgems;

import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.files.source.JGemsPathSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record JGemsPropData(JGemsPathSource pathToModel, PropRenderData propRenderData) implements IJGemsObjectData {
    public JGemsPropData(@Nullable JGemsPathSource pathToModel, @NotNull PropRenderData propRenderData) {
        this.propRenderData = propRenderData;
        this.pathToModel = pathToModel;
    }

    public JGemsPropData(@Nullable JGemsPathSource pathToModel) {
        this(pathToModel, JGemsResourceManager.globalRenderDataAssets.defaultPropIndirect);
    }

    public JGemsPropData(@NotNull PropRenderData propRenderData) {
        this(null, propRenderData);
    }
}
