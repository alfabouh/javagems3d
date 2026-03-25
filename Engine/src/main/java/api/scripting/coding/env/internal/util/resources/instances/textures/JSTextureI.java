package api.scripting.coding.env.internal.util.resources.instances.textures;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.graphics.rendering.programs.textures.base.ITextureProgram;

@JSCodingClass(binding = "JSTextureI", description = "Interface for texture programs.")
public interface JSTextureI {
    @JSHideFromDoc
    ITextureProgram getJavaTextureI();
}
