/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.graphics.rendering.ui.jgems_imgui.elements;

import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.system.resources.assets.texturing.ImageTexture;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;
import javagems3d.graphics.rendering.JGemsSceneUtils;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;

public class UIPictureStaticSelectable extends UIPictureStatic {
    private boolean selected;

    public UIPictureStaticSelectable(@NotNull ImageTexture iImageSample, @NotNull Vector2i position, @NotNull Vector2f textureXY, @NotNull Vector2f textureWH, float zValue) {
        super(iImageSample, position, textureXY, textureWH, zValue);
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
        this.imageModel.getFormat().setPosition(new Vector2f(this.getPosition()));
        this.imageModel.getFormat().setScale(new Vector2f(this.getScaling()));
        JGemsShaderManager shaderManager = this.getCurrentShader();
        shaderManager.beginShading();
        shaderManager.getUtils().performOrthographicMatrix(this.imageModel);
        GL46.glActiveTexture(GL46.GL_TEXTURE0);
        this.iImageSample.bindTexture();
        shaderManager.performUniform(new UniformString("texture_sampler"), UniformFunctions.INTEGER(0));
        this.getCurrentShader().performUniform(new UniformString("selected"), UniformFunctions.BOOLEAN(this.isSelected()));
        JGemsSceneUtils.renderModel(this.imageModel, GL46.GL_TRIANGLES);
        shaderManager.endShading();
    }
}
