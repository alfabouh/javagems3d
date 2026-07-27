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

import javagems3d.graphics.environment.decals.fx.DecalFX;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.environment.lights.SpotLight;
import javagems3d.graphics.environment.lights.scene.LightScene;
import javagems3d.graphics.environment.shadows.scene.ShadowScene;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.help.JGemsHelper;
import javagems3d.system.external.mapping.processing.ExternalMapProcessor;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;

public class DeferredLightRenderProcessor extends IRenderProcessor.Template {
    private final DeferredShaders shaders;
    private final FBOTexture2DProgram gBuffer;

    public DeferredLightRenderProcessor(@NotNull OpenGLRenderer openGLRenderer, @Nullable FBOTexture2DProgram gBuffer, @Nullable DeferredShaders shaders) {
        super(openGLRenderer);
        this.shaders = shaders;
        this.gBuffer = gBuffer;
    }

    @Override
    public void createResources() {
    }

    @Override
    public void destroyResources() {
    }

    @Override
    public void runProcessorRendering(FrameTicking frameTicking) {
        final FBOTexture2DProgram gBuffer = this.getGBuffer();
        {
            final JGemsShaderManager sun = this.getShaders().sunLight();
            sun.beginShading();
            sun.disableWarns();
            sun.performUniformTexture(new UniformString(DefaultUniformDefinitions.G_POSITIONS), gBuffer.getTextureByIndex(0));
            sun.performUniformTexture(new UniformString(DefaultUniformDefinitions.G_NORMALS), gBuffer.getTextureByIndex(1));
            sun.performUniformTexture(new UniformString(DefaultUniformDefinitions.G_METALLIC_ROUGHNESS), gBuffer.getTextureByIndex(4));
            sun.performOrthographicMatrix(new UniformString(DefaultUniformDefinitions.PROJECTION_MODEL_MATRIX), this.getOpenGLRenderer().getScreenModel(), JGemsTransformManager.INSTANCE.getOrthographicMatrix());
            sun.performUniform(new UniformString(DefaultUniformDefinitions.VIEW_MAT_INVERTED), UniformFunctions.MAT4F(JGemsTransformManager.INSTANCE.getCameraViewMatrix().invert()));
            JGemsHelper.render().performShadowsInfo(this.getOpenGLRenderer().getWorld().getEnvironment(), sun);
            JGemsHelper.render().renderModel2D(this.getOpenGLRenderer().getScreenModel(), GL46.GL_TRIANGLES);
            sun.enableWarns();
            sun.endShading();
        }

        if (!JGemsConfig.DEBUG.DISABLE_POINT_LIGHTS) {
            final JGemsShaderManager plight = this.getShaders().pointLights();
            plight.beginShading();
            GL46.glCullFace(GL46.GL_FRONT);
            GL46.glDisable(GL46.GL_DEPTH_TEST);
            for (PointLight pointLight : this.getOpenGLRenderer().getWorld().getEnvironment().getLightScene().getPointLights()) {
                plight.beginShading();
                plight.disableWarns();
                plight.performUniform(new UniformString("plight.position"), UniformFunctions.VEC3F(pointLight.getLightPosition()));
                plight.performUniform(new UniformString("plight.brightness"), UniformFunctions.FLOAT(pointLight.getBrightness()));
                plight.performUniform(new UniformString("plight.view_position"), UniformFunctions.VEC3F(LightScene.passVectorInViewSpace(pointLight.getLightPosition(), JGemsTransformManager.INSTANCE.getCameraViewMatrix(), 1.0f)));
                plight.performUniform(new UniformString("plight.attachedShadowSceneId"), UniformFunctions.INTEGER(((ShadowScene) (this.getOpenGLRenderer().getWorld().getEnvironment().getShadowScene())).getPointLightIdsHashMap().getOrDefault(pointLight, -1)));
                plight.performUniform(new UniformString("plight.color"), UniformFunctions.VEC3F(pointLight.getLightColor()));
                plight.performUniform(new UniformString("plight.clipRadius"), UniformFunctions.FLOAT(pointLight.getClipRadius()));

                plight.performUniformTexture(new UniformString(DefaultUniformDefinitions.G_POSITIONS), gBuffer.getTextureByIndex(0));
                plight.performUniformTexture(new UniformString(DefaultUniformDefinitions.G_NORMALS), gBuffer.getTextureByIndex(1));
                plight.performUniformTexture(new UniformString(DefaultUniformDefinitions.G_METALLIC_ROUGHNESS), gBuffer.getTextureByIndex(4));
                plight.performMatrix4(new UniformString(DefaultUniformDefinitions.PROJECTION_MATRIX), JGemsTransformManager.INSTANCE.getPerspectiveMatrix());
                plight.performUniform(new UniformString(DefaultUniformDefinitions.VIEW_MAT_INVERTED), UniformFunctions.MAT4F(JGemsTransformManager.INSTANCE.getCameraViewMatrix().invert()));
                final Matrix4f modelMatrix = new Matrix4f().identity().translate(pointLight.getLightPosition()).scale(pointLight.getClipRadius());
                plight.performUniform(new UniformString(DefaultUniformDefinitions.MODEL_VIEW_MATRIX), UniformFunctions.MAT4F(TransformUtils.getModelViewMatrix(modelMatrix, JGemsTransformManager.INSTANCE.getCameraViewMatrix())));
                JGemsHelper.render().performShadowsInfo(this.getOpenGLRenderer().getWorld().getEnvironment(), plight);
                JGemsHelper.render().renderMeshList3D(JGemsResourceManager.DEFAULT_CUBE_MESHGROUP().getAllNodes(), 0);
                plight.endShading();
                plight.enableWarns();
            }
            GL46.glEnable(GL46.GL_DEPTH_TEST);
            GL46.glCullFace(GL46.GL_BACK);
            plight.endShading();
        }

        if (!JGemsConfig.DEBUG.DISABLE_SPOT_LIGHTS) {
            final JGemsShaderManager slight = this.getShaders().spotLight();
            slight.beginShading();
            GL46.glCullFace(GL46.GL_FRONT);
            GL46.glDisable(GL46.GL_DEPTH_TEST);
            for (SpotLight spotLight : this.getOpenGLRenderer().getWorld().getEnvironment().getLightScene().getSpotLights()) {
                slight.beginShading();
                slight.disableWarns();
                slight.performUniform(new UniformString("slight.position"), UniformFunctions.VEC3F(spotLight.getLightPosition()));
                slight.performUniform(new UniformString("slight.brightness"), UniformFunctions.FLOAT(spotLight.getBrightness()));
                slight.performUniform(new UniformString("slight.direction"), UniformFunctions.VEC3F(LightScene.passVectorInViewSpace(spotLight.getLightDirection(), JGemsTransformManager.INSTANCE.getCameraViewMatrix(), 0.0f)));
                slight.performUniform(new UniformString("slight.attachedShadowSceneId"), UniformFunctions.INTEGER(((ShadowScene) (this.getOpenGLRenderer().getWorld().getEnvironment().getShadowScene())).getSpotLightIdsHashMap().getOrDefault(spotLight, -1)));
                slight.performUniform(new UniformString("slight.view_position"), UniformFunctions.VEC3F(LightScene.passVectorInViewSpace(spotLight.getLightPosition(), JGemsTransformManager.INSTANCE.getCameraViewMatrix(), 1.0f)));
                slight.performUniform(new UniformString("slight.color"), UniformFunctions.VEC3F(spotLight.getLightColor()));
                slight.performUniform(new UniformString("slight.cutOff"), UniformFunctions.FLOAT(spotLight.getCutOff()));
                slight.performUniform(new UniformString("slight.attenuationFactor"), UniformFunctions.FLOAT(spotLight.getAttenuationFactor()));
                slight.performUniform(new UniformString("slight.clipRadius"), UniformFunctions.FLOAT(spotLight.getClipRadius()));

                slight.performUniformTexture(new UniformString(DefaultUniformDefinitions.G_POSITIONS), gBuffer.getTextureByIndex(0));
                slight.performUniformTexture(new UniformString(DefaultUniformDefinitions.G_NORMALS), gBuffer.getTextureByIndex(1));
                slight.performUniformTexture(new UniformString(DefaultUniformDefinitions.G_METALLIC_ROUGHNESS), gBuffer.getTextureByIndex(4));
                slight.performMatrix4(new UniformString(DefaultUniformDefinitions.PROJECTION_MATRIX), JGemsTransformManager.INSTANCE.getPerspectiveMatrix());
                slight.performUniform(new UniformString(DefaultUniformDefinitions.VIEW_MAT_INVERTED), UniformFunctions.MAT4F(JGemsTransformManager.INSTANCE.getCameraViewMatrix().invert()));

                {
                    float length = spotLight.getClipRadius();
                    float angle = (float) Math.toRadians(spotLight.getCutOffDegrees());
                    final float projection = length * (float) Math.tan(angle);
                    Matrix4f matrix4f = new Matrix4f().identity();
                    matrix4f.translate(spotLight.getLightPosition());
                    matrix4f.rotateTowards(spotLight.getLightDirection(), spotLight.getLightDirection().dot(new Vector3f(0, 1, 0)) == -1.0f ? new Vector3f(1.0f, 0.0f, 0.0f) : new Vector3f(0f, 1f, 0f));
                    matrix4f.scale(new Vector3f(projection * 0.5f, projection * 0.5f, length * 0.5f));
                    matrix4f.translate(0.0f, 0.0f, 1.0f);
                    slight.performUniform(new UniformString(DefaultUniformDefinitions.MODEL_VIEW_MATRIX), UniformFunctions.MAT4F(TransformUtils.getModelViewMatrix(matrix4f, JGemsTransformManager.INSTANCE.getCameraViewMatrix())));
                }
                JGemsHelper.render().performShadowsInfo(this.getOpenGLRenderer().getWorld().getEnvironment(), slight);
                JGemsHelper.render().renderMeshList3D(JGemsResourceManager.DEFAULT_CUBE_MESHGROUP().getAllNodes(), 0);
                slight.endShading();
                slight.enableWarns();
            }
            GL46.glEnable(GL46.GL_DEPTH_TEST);
            GL46.glCullFace(GL46.GL_BACK);
            slight.endShading();
        }
    }

    protected FBOTexture2DProgram getGBuffer() {
        return this.gBuffer;
    }

    public DeferredShaders getShaders() {
        return this.shaders;
    }

    public record DeferredShaders(@NotNull JGemsShaderManager sunLight, @NotNull JGemsShaderManager pointLights, @NotNull JGemsShaderManager spotLight) { ; }
}