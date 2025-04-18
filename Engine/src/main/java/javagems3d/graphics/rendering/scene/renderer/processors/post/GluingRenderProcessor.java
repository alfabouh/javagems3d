package javagems3d.graphics.rendering.scene.renderer.processors.post;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.help.JGemsHelper;
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
        gluing.performUniformTextureBindless(new UniformString("texture_map"), this.getInColorScene().getTextureByIndex(0));
        gluing.performUniformTextureBindless(new UniformString("bloom_map1"), this.getInColorScene().getTextureByIndex(1));
        gluing.performUniformTextureBindless(new UniformString("bloom_map2"), this.getInColorTransparency().getTextureByIndex(2));
        gluing.performUniformTextureBindless(new UniformString("accumulated_alpha"), this.getInColorTransparency().getTextureByIndex(0));
        gluing.performUniformTextureBindless(new UniformString("reveal_alpha"), this.getInColorTransparency().getTextureByIndex(1));
        gluing.performOrthographicMatrix(new UniformString("projection_model_matrix"), this.getOpenGLRenderer().getScreenModel(), JGemsTransformManager.INSTANCE.getOrthographicMatrix());
        JGemsHelper.render().renderModel2D(this.getOpenGLRenderer().getScreenModel(), GL46.GL_TRIANGLES);
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
