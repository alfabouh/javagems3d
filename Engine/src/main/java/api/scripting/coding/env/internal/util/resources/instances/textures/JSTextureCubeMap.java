package api.scripting.coding.env.internal.util.resources.instances.textures;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.JSCanBeCachedInMemory;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITextureProgram;

@JSCodingClass(binding = "JSTextureCubeMap", description = "Cube map texture with caching and program access.")
public class JSTextureCubeMap implements JSCanBeCachedInMemory, JSTextureI {
    @JSHideFromDoc
    private final ICubeMapProgram cubeMapProgram;

    public JSTextureCubeMap(ICubeMapProgram cubeMapProgram) {
        this.cubeMapProgram = cubeMapProgram;
    }

    @JSCodingFunctionOrMethod(description = "Get cube map program")
    public ICubeMapProgram getJavaCubeMapProgram() {
        return this.cubeMapProgram;
    }

    @JSHideFromDoc
    @Override
    public ITextureProgram getJavaTextureI() {
        return this.cubeMapProgram;
    }
}