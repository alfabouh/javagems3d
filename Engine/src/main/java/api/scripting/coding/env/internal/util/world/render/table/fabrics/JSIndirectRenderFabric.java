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

package api.scripting.coding.env.internal.util.world.render.table.fabrics;

import api.scripting.coding.env.def.*;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IndirectRenderFabric;
import javagems3d.graphics.rendering.scene.renderer.indirect.scene_objects.IndirectSceneObjectsRenderer;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSIndirectRenderFabric", description = "Wrapper for IndirectRenderFabric. Recommended to manage indirect rendering from Java code due to high complexity.")
public class JSIndirectRenderFabric implements JSRenderFabricI {

    @JSCodingField(description = "The underlying Java IndirectRenderFabric object")
    protected final IndirectRenderFabric fabric;

    @JSCodingConstructor(description = "Constructs the JS wrapper for an IndirectRenderFabric", paramNames = {"fabric"})
    public JSIndirectRenderFabric(@NotNull IndirectRenderFabric fabric) {
        this.fabric = fabric;
    }

    @JSCodingFunctionOrMethod(description = "Returns the underlying Java IndirectRenderFabric object")
    public IndirectRenderFabric getJavaFabric() {
        return this.fabric;
    }

    @JSCodingFunctionOrMethod(description = "Returns the rendering stage of this fabric")
    public Stage getRenderingStage() {
        return this.fabric.getRenderingStage();
    }

    @JSCodingFunctionOrMethod(description = "Returns the rendering function associated with this fabric")
    public IndirectSceneObjectsRenderer.IRenderingFunction getRenderingFunction() {
        return this.fabric.getRenderingFunction();
    }

    @JSHideFromDoc
    @Override
    public IRenderFabric getJavaRenderFabric() {
        return this.fabric;
    }
}