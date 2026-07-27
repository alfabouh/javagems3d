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

package javagems3d.graphics.rendering.scene.renderer.processors.post;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
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
        gluing.performUniformTexture(new UniformString(DefaultUniformDefinitions.TEXTURE_MAP), this.getInColorScene().getTextureByIndex(0));
        gluing.performUniformTexture(new UniformString(DefaultUniformDefinitions.BLOOM_MAP1), this.getInColorScene().getTextureByIndex(1));
        gluing.performUniformTexture(new UniformString(DefaultUniformDefinitions.BLOOM_MAP2), this.getInColorTransparency().getTextureByIndex(2));
        gluing.performUniformTexture(new UniformString(DefaultUniformDefinitions.ACCUMULATED_ALPHA), this.getInColorTransparency().getTextureByIndex(0));
        gluing.performUniformTexture(new UniformString(DefaultUniformDefinitions.REVEAL_ALPHA), this.getInColorTransparency().getTextureByIndex(1));
        gluing.performOrthographicMatrix(new UniformString(DefaultUniformDefinitions.PROJECTION_MODEL_MATRIX), this.getOpenGLRenderer().getScreenModel(), JGemsTransformManager.INSTANCE.getOrthographicMatrix());
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
