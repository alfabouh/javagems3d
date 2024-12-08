package javagems3d.graphics.objects.rendering.fabric;

import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.rendering.fabric.args.ArbitraryArguments;
import javagems3d.graphics.rendering.programs.indirect.IndirectBufferCommandsBuilder;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.lwjgl.opengl.GL46;

public class IndirectGBufferRenderFabric extends IRenderFabric.Template {
    @Override
    public void onRender(OpenGLRenderer openGLRenderer, IRendered rendered, ArbitraryArguments arbitraryArguments) {

    }

    @Override
    public void createResources(IRendered rendered) {

    }

    @Override
    public void destroyResources(IRendered rendered) {

    }
}