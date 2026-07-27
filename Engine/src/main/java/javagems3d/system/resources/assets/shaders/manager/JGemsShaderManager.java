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

package javagems3d.system.resources.assets.shaders.manager;

import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ITextureProgram;
import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.pose.Pose2D;
import javagems3d.system.resources.assets.texturing.colors.ISampleColor2;
import javagems3d.system.resources.assets.texturing.colors.ISampleColor3;
import javagems3d.system.resources.assets.texturing.colors.ISampleColor4;
import org.joml.Matrix4f;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.system.resources.assets.texturing.ISample;

import javagems3d.system.resources.assets.shaders.base.ShadersContainer;
import javagems3d.system.resources.assets.shaders.buffers.UniformBufferObject;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;

public class JGemsShaderManager extends ShaderManager {
    public JGemsShaderManager(ShadersContainer shadersContainer) {
        super(shadersContainer);
    }

    @Override
    public JGemsShaderManager attachUBOs(UniformBufferObject... uniformBufferObjects) {
        return (JGemsShaderManager) super.attachUBOs(uniformBufferObjects);
    }

    public JGemsShaderManager copy() {
        return new JGemsShaderManager(this.getShadersContainer());
    }

    public void performUniformSample(UniformString uniform, ISample sample) {
        if (sample instanceof ISampleColor4 color) {
            this.performUniform(uniform, UniformFunctions.VEC4F(color.color()));
        } else if (sample instanceof ISampleColor3 color) {
            this.performUniform(uniform, UniformFunctions.VEC3F(color.color()));
        } else if (sample instanceof ISampleColor2 color) {
            this.performUniform(uniform, UniformFunctions.VEC2F(color.color()));
        } else {
            if (sample instanceof ITextureProgram textureProgram) {
                this.performUniformTextureBindless(uniform, textureProgram);
            }
        }
    }

    public void performOrthographicMatrix(UniformString uniform, Pose2D pose2D, Matrix4f orthographicMatrix) {
        this.performUniform(uniform, UniformFunctions.MAT4F(TransformUtils.getModelOrthographicMatrix(pose2D, orthographicMatrix)));
    }

    public void performOrthographicMatrix(UniformString uniform, Model2D model, Matrix4f orthographicMatrix) {
        this.performUniform(uniform, UniformFunctions.MAT4F(TransformUtils.getModelOrthographicMatrix(model.getPose(), orthographicMatrix)));
    }

    public void performModel3DViewMatrix(UniformString uniform, Model3D model, Matrix4f view) {//new UniformString(DefaultUniformDefinitions.MODEL_VIEW_MATRIX)
        this.performUniform(uniform, UniformFunctions.MAT4F(TransformUtils.getModelViewMatrix(model.getPose(), view)));
    }

    public void performModel3DViewMatrix(UniformString uniform, Matrix4f model, Matrix4f view) {//new UniformString(DefaultUniformDefinitions.MODEL_VIEW_MATRIX)
        this.performUniform(uniform, UniformFunctions.MAT4F(new Matrix4f(view).mul(model)));
    }

    public void performMatrix4(UniformString uniform, Matrix4f mat) {
        this.performUniform(uniform, UniformFunctions.MAT4F(mat));
    }

    public void performModel3DMatrix(UniformString uniform, Model3D model) {//new UniformString(DefaultUniformDefinitions.MODEL_MATRIX)
        this.performUniform(uniform, UniformFunctions.MAT4F(TransformUtils.getModelMatrix(model.getPose())));
    }
}
