package api.scripting.coding.env.internal.util.resources.instances.textures;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.JSCanBeCachedInMemory;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITextureProgram;

@JSCodingClass(binding = "JSTexture2D", description = "...")
public class JSTexture2D implements JSCanBeCachedInMemory, JSTextureI {
    @JSHideFromDoc
    private final ITexture2DProgram texture2DProgram;

    public JSTexture2D(ITexture2DProgram texture2DProgram) {
        this.texture2DProgram = texture2DProgram;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public ITexture2DProgram getJavaTexture2DProgram() {
        return this.texture2DProgram;
    }

    @JSHideFromDoc
    @Override
    public ITextureProgram getJavaTextureI() {
        return this.texture2DProgram;
    }
}
