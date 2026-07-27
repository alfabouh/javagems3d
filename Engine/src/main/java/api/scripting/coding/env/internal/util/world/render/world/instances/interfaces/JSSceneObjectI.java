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

package api.scripting.coding.env.internal.util.world.render.world.instances.interfaces;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.world.render.processing.JSCullingRules;
import api.scripting.coding.env.internal.util.world.render.processing.JSRenderAttributes;
import api.scripting.coding.env.internal.util.world.render.table.JSRenderTable;
import api.scripting.coding.env.internal.util.world.render.table.fabrics.JSRenderFabricI;
import api.scripting.coding.env.internal.util.world.render.table.properties.JSPipeline;
import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IRenderFabric;

import java.util.HashSet;
import java.util.Set;

@JSCodingClass(binding = "JSSceneObjectI", description = "Base interface for scene objects with rendering control")
public interface JSSceneObjectI {

    @JSCodingFunctionOrMethod(description = "Get underlying Java SceneObject", paramNames = {})
    SceneObject getJavaSceneObject();

    @JSCodingFunctionOrMethod(description = "Check if object has valid render attributes", paramNames = {})
    default boolean canBeRendered() {
        return ((IRendered) this.getJavaSceneObject()).canBeRendered();
    }

    @JSCodingFunctionOrMethod(description = "Check if object can be rendered in specific pipeline", paramNames = {"pipeline"})
    default boolean canBeRendered(JSPipeline pipeline) {
        return ((IRendered) this.getJavaSceneObject()).canBeRendered(pipeline.getJavaPipeline());
    }

    @JSCodingFunctionOrMethod(description = "Get culling rules", paramNames = {})
    default JSCullingRules getCullingRules() {
        return new JSCullingRules(((IRendered) this.getJavaSceneObject()).getCullingRules());
    }

    @JSCodingFunctionOrMethod(description = "Get render attributes", paramNames = {})
    default JSRenderAttributes getRenderAttributes() {
        return new JSRenderAttributes(((IRendered) this.getJavaSceneObject()).getRenderAttributes());
    }

    @JSCodingFunctionOrMethod(description = "Get render table", paramNames = {})
    default JSRenderTable getRenderTable() {
        return new JSRenderTable(((IRendered) this.getJavaSceneObject()).getRenderTable());
    }

    @JSCodingFunctionOrMethod(description = "Get render fabrics set", paramNames = {})
    default Set<JSRenderFabricI> getRenderFabricsSet() {
        Set<IRenderFabric> set = this.getJavaSceneObject().getRenderFabricsSet();
        Set<JSRenderFabricI> result = new HashSet<>();
        for (IRenderFabric fabric : set) {
            result.add(() -> fabric);
        }
        return result;
    }

    @JSCodingFunctionOrMethod(description = "Get render fabric for pipeline", paramNames = {"pipeline"})
    default JSRenderFabricI getRenderFabric(JSPipeline pipeline) {
        return () -> JSSceneObjectI.this.getJavaSceneObject().getRenderFabric(pipeline.getJavaPipeline());
    }
}