package api.scripting.coding.env.internal.util.world.render.data;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.instances.models.JSMaterial;
import api.scripting.coding.env.internal.util.resources.instances.shaders.JSShader;
import javagems3d.graphics.objects.rendering.data.LiquidRenderData;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSLiquidRenderData", description = "Wrapper for LiquidRenderData containing material and shader manager for liquids.")
public class JSLiquidRenderData {
    @JSHideFromDoc
    private final LiquidRenderData data;

    @JSCodingConstructor(description = "Create a new liquid render data", paramNames = {"material", "shaderManager"})
    public JSLiquidRenderData(@NotNull JSMaterial material, @NotNull JSShader shaderManager) {
        this.data = new LiquidRenderData(material.getJavaMaterial(), shaderManager.getJavaShaderManager());
    }

    @JSHideFromDoc
    public JSLiquidRenderData(LiquidRenderData renderLiquidData) {
        this.data = renderLiquidData;
    }

    @JSCodingFunctionOrMethod(description = "Returns the underlying Material used for the liquid")
    public JSMaterial getLiquidMaterial() {
        return new JSMaterial(this.data.liquidMaterial());
    }

    @JSCodingFunctionOrMethod(description = "Returns the shader manager for this liquid")
    public JSShader getShaderManager() {
        return new JSShader(this.data.shaderManager());
    }

    @JSCodingFunctionOrMethod(description = "Returns the underlying Java LiquidRenderData object (unsafe, internal use)")
    public LiquidRenderData getJavaLiquidRenderData() {
        return this.data;
    }
}