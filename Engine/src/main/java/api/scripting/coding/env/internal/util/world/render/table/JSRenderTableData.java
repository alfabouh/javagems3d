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

package api.scripting.coding.env.internal.util.world.render.table;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.resources.instances.shaders.JSShader;
import api.scripting.coding.env.internal.util.world.render.table.fabrics.JSDirectRenderFabric;
import api.scripting.coding.env.internal.util.world.render.table.fabrics.JSIndirectRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.graphics.objects.rendering.pipeline.fabric.DirectRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IndirectRenderFabric;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSRenderTableData", description = "Wrapper for RenderTable.Data. Use JS access to shader and render fabric.")
public class JSRenderTableData {

    @JSCodingField(description = "Underlying Java RenderTable.Data object")
    protected final RenderTable.Data data;

    @JSCodingConstructor(description = "Constructs JS wrapper for RenderTable.Data", paramNames = {"data"})
    public JSRenderTableData(@NotNull RenderTable.Data data) {
        this.data = data;
    }

    @JSCodingFunctionOrMethod(description = "Get shader manager", paramNames = {})
    public JSShader getShader() {
        return new JSShader(this.data.getShaderManager());
    }

    @JSCodingFunctionOrMethod(description = "Get render fabric (direct or indirect)", paramNames = {})
    public Object getRenderFabric() {
        IRenderFabric fabric = this.data.getRenderFabric();
        if (fabric instanceof DirectRenderFabric direct) {
            return new JSDirectRenderFabric(direct);
        } else if (fabric instanceof IndirectRenderFabric indirect) {
            return new JSIndirectRenderFabric(indirect);
        }
        return null;
    }
}