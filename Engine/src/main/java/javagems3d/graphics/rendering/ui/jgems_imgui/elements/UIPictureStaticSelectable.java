package javagems3d.graphics.rendering.ui.jgems_imgui.elements;

import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.help.JGemsRenderingHelper;
import javagems3d.system.resources.assets.texturing.ImageTexture;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;

public class UIPictureStaticSelectable extends UIPictureStatic {
    private boolean selected;

    public UIPictureStaticSelectable(@NotNull ITexture2DProgram texture2DProgram, @NotNull Vector2i position, @NotNull Vector2f textureXY, @NotNull Vector2f textureWH, float zValue) {
        super(texture2DProgram, position, textureXY, textureWH, zValue);
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public JGemsShaderManager getCurrentShader() {
        return JGemsResourceManager.globalShaderAssets.gui_image_selectable;
    }

    @Override
    public void render(float frameDeltaTicks) {
        this.imageModel.getPose().setPosition(new Vector2f(this.getPosition()));
        this.imageModel.getPose().setScale(new Vector2f(this.getScaling()));
        JGemsShaderManager shaderManager = this.getCurrentShader();
        shaderManager.beginShading();
        shaderManager.performOrthographicMatrix(new UniformString("projection_model_matrix"), this.imageModel, JGemsTransformManager.INSTANCE.getOrthographicMatrix());
        shaderManager.performUniformTexture(new UniformString("texture_bindless"), this.iImageSample);
        this.getCurrentShader().performUniform(new UniformString("selected"), UniformFunctions.BOOLEAN(this.isSelected()));
        JGemsRenderingHelper.renderModel2D(this.imageModel, GL46.GL_TRIANGLES);
        shaderManager.endShading();
    }
}
