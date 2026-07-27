/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.graphics.rendering.ui.jgems_imgui.elements;

import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.UIElement;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;

public class UIPictureStaticSelectable extends UIPictureStatic {
    private boolean selected;

    public UIPictureStaticSelectable(IWindow window, @NotNull ITexture2DProgram texture2DProgram, @NotNull Vector2f position, @NotNull Vector2f textureXY, @NotNull Vector2f textureWH, float zValue) {
        super(window, texture2DProgram, position, textureXY, textureWH, zValue);
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
        shaderManager.performOrthographicMatrix(new UniformString(DefaultUniformDefinitions.PROJECTION_MODEL_MATRIX),
                (this.imageModel.getPose()),
                UIElement.getProjection(this.getWindow(), this.GLOBAL_SCALE_FACTOR()));
        shaderManager.performUniformTexture(new UniformString(DefaultUniformDefinitions.TEXTURE_MAP), this.iImageSample);
        this.getCurrentShader().performUniform(new UniformString(DefaultUniformDefinitions.SELECTED), UniformFunctions.BOOLEAN(this.isSelected()));
        JGemsHelper.render().renderModel2D(this.imageModel, GL46.GL_TRIANGLES);
        shaderManager.endShading();
    }
}
