package javagems3d.graphics.rendering.scene.renderer.processors.post;

import javagems3d.JGemsHelper;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

public class GluingRenderProcessor extends IRenderProcessor.Template {
    private final FBOTexture2DProgram inColorScene;
    private final FBOTexture2DProgram inColorTransparency;
    private final JGemsShaderManager gluingShader;

    public GluingRenderProcessor(@NotNull FBOTexture2DProgram inColorTransparency, @NotNull FBOTexture2DProgram inColorScene, @NotNull OpenGLRenderer openGLRenderer, @NotNull JGemsShaderManager gluingShader) {
        super(openGLRenderer);
        this.inColorScene = inColorScene;
        this.inColorTransparency = inColorTransparency;
        this.gluingShader = gluingShader;
    }

    @Override
    public void createResources() {
    }

    @Override
    public void destroyResources() {
    }

    @Override
    public void runProcessorRendering(FrameTicking frameTicking) {
        JGemsShaderManager gluing = this.getGluingShader();
        gluing.beginShading();
        gluing.performUniformTexture(new UniformString("texture_sampler"), this.getInColorScene().getTextureByIndex(0));
        gluing.performUniformTexture(new UniformString("bloom_sampler"), this.getInColorScene().getTextureByIndex(1));
        gluing.performUniformTexture(new UniformString("bloom_sampler2"), this.getInColorTransparency().getTextureByIndex(2));
        gluing.performUniformTexture(new UniformString("accumulated_alpha"), this.getInColorTransparency().getTextureByIndex(0));
        gluing.performUniformTexture(new UniformString("reveal_alpha"), this.getInColorTransparency().getTextureByIndex(1));
        gluing.getUtils().performOrthographicMatrix(this.getOpenGLRenderer().getScreenModel());
        JGemsHelper.RENDERING.renderModel2D(this.getOpenGLRenderer().getScreenModel(), GL46.GL_TRIANGLES);
        gluing.endShading();
    }

    public JGemsShaderManager getGluingShader() {
        return this.gluingShader;
    }

    public FBOTexture2DProgram getInColorScene() {
        return this.inColorScene;
    }

    public FBOTexture2DProgram getInColorTransparency() {
        return this.inColorTransparency;
    }
}
