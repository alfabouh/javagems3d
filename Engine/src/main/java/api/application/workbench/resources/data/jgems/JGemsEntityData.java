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

import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.attributes.base.RenderProperties;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.files.source.JGemsPathSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record JGemsEntityData(JGemsPathSource pathToModel, EntityRenderData entityRenderData) implements IJGemsObjectData {
    public JGemsEntityData(@Nullable JGemsPathSource pathToModel, @NotNull EntityRenderData entityRenderData) {
        this.entityRenderData = entityRenderData;
        this.pathToModel = pathToModel;
    }

    public JGemsEntityData(@Nullable JGemsPathSource pathToModel, @NotNull RenderProperties renderProperties) {
        this(pathToModel, new EntityRenderData(JGemsResourceManager.globalRenderDataAssets.defaultEntityIndirect, RenderAttributes.getDefaultIndirect(renderProperties)));
    }

    public JGemsEntityData(@Nullable JGemsPathSource pathToModel) {
        this(pathToModel, JGemsResourceManager.globalRenderDataAssets.defaultEntityIndirect);
    }

    public JGemsEntityData() {
        this(null, JGemsResourceManager.globalRenderDataAssets.defaultEntityIndirect);
    }

    @SuppressWarnings("all")
    public <T extends RenderProperties> T getRenderPropertiesUnsafeCast() {
        return (T) this.entityRenderData().getObjectRenderAttributes().getProperties();
    }
}
