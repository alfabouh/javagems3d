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

package javagems3d.graphics.objects;

import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IRenderFabric;
import javagems3d.graphics.rendering.scene.culling.rules.CullingRules;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

public interface IRendered extends ICulled {
    RenderAttributes getRenderAttributes();

    @Override
    default @NotNull CullingRules getCullingRules() {
        return this.getRenderAttributes().getCullingRules();
    }

    default boolean canBeRendered() {
        return this.getRenderAttributes() != null;
    }

    default boolean canBeRendered(Pipeline pipeline) {
        return this.canBeRendered() && this.getRenderTable().validate(pipeline);
    }

    default Set<IRenderFabric> getRenderFabricsSet() {
        return this.getRenderTable().getRenderFabricsSet();
    }

    default IRenderFabric getRenderFabric(Pipeline pipeline) {
        return Objects.requireNonNull(this.getRenderTable().getRenderingData(pipeline)).getRenderFabric();
    }

    default RenderTable getRenderTable() {
        return this.getRenderAttributes().getRenderTable();
    }
}