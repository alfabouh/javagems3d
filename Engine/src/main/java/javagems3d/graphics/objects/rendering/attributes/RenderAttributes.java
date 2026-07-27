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

package javagems3d.graphics.objects.rendering.attributes;

import javagems3d.graphics.objects.rendering.attributes.base.IRenderAttributes;
import javagems3d.graphics.objects.rendering.attributes.base.RenderProperties;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.graphics.rendering.scene.culling.rules.CullingRules;
import javagems3d.system.resources.managing.resources.data.ICopyable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@SuppressWarnings("all")
public class RenderAttributes implements IRenderAttributes, ICopyable<RenderAttributes> {
    private RenderTable renderTable;
    private RenderProperties renderProperties;

    public RenderAttributes(@NotNull RenderTable renderTable, @NotNull RenderProperties renderProperties) {
        this.renderTable = renderTable;
        this.renderProperties = renderProperties;
    }

    public static @NotNull RenderAttributes get(@Nullable RenderTable renderTable, @NotNull RenderProperties renderProperties) {
        return (renderTable == null || renderProperties == null) ? null : new RenderAttributes(renderTable, renderProperties);
    }

    public RenderAttributes setRenderTable(@NotNull RenderTable renderTable) {
        this.renderTable = renderTable;
        return this;
    }

    public RenderAttributes setRenderProperties(@NotNull RenderProperties renderProperties) {
        this.renderProperties = renderProperties;
        return this;
    }

    public RenderTable getRenderTable() {
        return this.renderTable;
    }

    public RenderProperties getProperties() {
        return this.renderProperties;
    }


    public static RenderAttributes getDefaultDirect() {
        return new RenderAttributes(RenderTable.getDirect(), JGemsRenderProperties.getDefault());
    }

    public static RenderAttributes getDefaultIndirect() {
        return new RenderAttributes(RenderTable.getIndirect(), JGemsRenderProperties.getDefault());
    }

    public static RenderAttributes getDefaultDirect(@NotNull RenderProperties renderProperties) {
        return new RenderAttributes(RenderTable.getDirect(), renderProperties);
    }

    public static RenderAttributes getDefaultIndirect(@NotNull RenderProperties renderProperties) {
        return new RenderAttributes(RenderTable.getIndirect(), renderProperties);
    }

    @Override
    public @NotNull RenderAttributes copy() {
        return new RenderAttributes(this.getRenderTable().copy(), this.getProperties().copy());
    }

    public @NotNull CullingRules getCullingRules() {
        return this.getProperties().getCullingRules();
    }
}
