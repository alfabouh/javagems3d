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