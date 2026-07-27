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

package workbench.graphics.scene.nodes;

import javagems3d.graphics.environment.lights.SpotLight;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.debug.DebugLinesDrawer;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions.ForwardRenderNode;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import workbench.WBench;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.graphics.scene.world.WBenchWorld;
import workbench.project.map.settings.MapProjectSettings;
import workbench.resources.WBenchResourceManager;

public class WBenchForwardRenderNode extends ForwardRenderNode {
    public WBenchForwardRenderNode(@NotNull FBOTexture2DProgram inColor, OpenGLRenderer openGLRenderer) {
        super(inColor, openGLRenderer);
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        super.onRender(frameTicking);

        if (WBench.get().getMapProjectManager().mapProjectSettings.VIEW_CHESS_TERRAIN || WBenchOpenGLRenderer.isRenderingBackgroundScene()) {
            final WBenchWorld wBenchWorld = (WBenchWorld) this.getWorld();
            this.getOutColorBuffer().bindFBO();
            GL46.glEnable(GL46.GL_BLEND);
            GL46.glBlendFunc(GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
            WBenchResourceManager.localShaderAssets.simple_flat.beginShading();
            WBenchResourceManager.localShaderAssets.simple_flat.performMatrix4(new UniformString(DefaultUniformDefinitions.PROJECTION_MATRIX), JGemsTransformManager.INSTANCE.getPerspectiveMatrix());
            WBenchResourceManager.localShaderAssets.simple_flat.performMatrix4(new UniformString(DefaultUniformDefinitions.MODEL_MATRIX), TransformUtils.getModelMatrix(WBenchOpenGLRenderer.flatTerrain.getPose()));
            WBenchResourceManager.localShaderAssets.simple_flat.performMatrix4(new UniformString(DefaultUniformDefinitions.VIEW_MATRIX), JGemsTransformManager.INSTANCE.getCameraViewMatrix());
            if (WBenchOpenGLRenderer.isRenderingBackgroundScene()) {
                WBenchResourceManager.localShaderAssets.simple_flat.performUniform(new UniformString(DefaultUniformDefinitions.COLOR), UniformFunctions.VEC4F(new Vector4f(0.35f, 0.65f, 0.35f, 0.5f)));
                WBenchResourceManager.localShaderAssets.simple_flat.performUniform(new UniformString(DefaultUniformDefinitions.DRAW_CENTER_RECT), UniformFunctions.FLOAT(128.0f / wBenchWorld.getEnvironment().getSkyBox().getBackground().getViewScaling()));
            } else {
                WBenchResourceManager.localShaderAssets.simple_flat.performUniform(new UniformString(DefaultUniformDefinitions.COLOR), UniformFunctions.VEC4F(new Vector4f(0.35f, 0.35f, 0.65f, 0.5f)));
                WBenchResourceManager.localShaderAssets.simple_flat.performUniform(new UniformString(DefaultUniformDefinitions.DRAW_CENTER_RECT), UniformFunctions.FLOAT(-1.0f));
            }
            JGemsHelper.render().renderModel3D(WBenchOpenGLRenderer.flatTerrain, MeshStructure3D.SOLID_LAYER, GL46.GL_TRIANGLES);
            WBenchResourceManager.localShaderAssets.simple_flat.endShading();

            // DEBUG
            if (false)
            {
                for (SpotLight spotLight : this.getWorld().getEnvironment().getLightScene().getSpotLights()) {
                    float length = spotLight.getClipRadius();
                    float angle = (float) Math.toRadians(spotLight.getCutOffDegrees());
                    final float projection = length * (float) Math.tan(angle);
                    Matrix4f matrix4f = new Matrix4f().identity();
                    matrix4f.translate(spotLight.getLightPosition());
                    matrix4f.rotateTowards(spotLight.getLightDirection(), spotLight.getLightDirection().dot(new Vector3f(0, 1, 0)) == -1.0f ? new Vector3f(1.0f, 0.0f, 0.0f) : new Vector3f(0f, 1f, 0f));
                    matrix4f.scale(new Vector3f(projection * 0.5f, projection * 0.5f, length * 0.5f));
                    matrix4f.translate(0.0f, 0.0f, 1.0f);

                    WBenchResourceManager.localShaderAssets.weighted_oit_simple.beginShading();
                    WBenchResourceManager.localShaderAssets.weighted_oit_simple.performMatrix4(new UniformString(DefaultUniformDefinitions.PROJECTION_MATRIX), JGemsTransformManager.INSTANCE.getPerspectiveMatrix());
                    WBenchResourceManager.localShaderAssets.weighted_oit_simple.performMatrix4(new UniformString(DefaultUniformDefinitions.MODEL_MATRIX), matrix4f);
                    WBenchResourceManager.localShaderAssets.weighted_oit_simple.performMatrix4(new UniformString(DefaultUniformDefinitions.VIEW_MATRIX), JGemsTransformManager.INSTANCE.getCameraViewMatrix());
                    JGemsHelper.render().renderMeshList3D(JGemsResourceManager.DEFAULT_CUBE_MESHGROUP().getAllNodes(), GL46.GL_TRIANGLES);
                    WBenchResourceManager.localShaderAssets.weighted_oit_simple.endShading();
                }
            }


            GL46.glDisable(GL46.GL_BLEND);
            this.getOutColorBuffer().unBindFBO();
        }
    }

    @Override
    public @NotNull ITexture2DProgram getAnimationsTexture() {
        return WBenchResourceManager.getAnimationsTextureBuffer();
    }

    @Override
    public void createResources() {
        super.createResources();
    }

    @Override
    public void destroyResources() {
        super.destroyResources();
    }

    @Override
    public boolean renderBackground() {
        return ((MapEditorInterface) WBenchOpenGLRenderer.getMapEditorInterface()).getOldCamera() == null;
    }

    @Override
    public @NotNull ShaderStorageBufferObject getIndirectBufferData() {
        return WBenchResourceManager.localShaderAssets.MainSceneIndirectBufferData;
    }

    @Override
    public @NotNull ShaderStorageBufferObject getPropertiesData() {
        return WBenchResourceManager.localShaderAssets.MainScenePropertiesData;
    }

    @Override
    public @NotNull JGemsShaderManager getSkyBoxShader() {
        return WBenchResourceManager.localShaderAssets.skybox;
    }
}
