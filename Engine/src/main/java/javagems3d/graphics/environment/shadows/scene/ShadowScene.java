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

package javagems3d.graphics.environment.shadows.scene;

import javagems3d.global.JGemsGlobalConfiguration;
import javagems3d.graphics.environment.Environment;
import javagems3d.graphics.environment.shadows.PointLightShadow;
import javagems3d.graphics.environment.shadows.SunLightShadow;
import javagems3d.graphics.objects.IAnimated;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.objects.rendering.configuration.ShadingTable;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.indirect.IndirectObjectsRenderer;
import javagems3d.system.resources.assets.models.mesh.structures.MeshGroup;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.joml.*;
import org.lwjgl.opengl.GL46;
import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.global.JGemsDebugGlobalConstants;
import javagems3d.global.JGemsRenderingGlobalConstants;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.transformation.TransformationUtils;
import javagems3d.system.resources.assets.texturing.RGBAColor;
import javagems3d.system.resources.assets.texturing.base.ImageBasedTexture;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ShadowScene implements IShadowScene {
    private final IndirectObjectsRenderer indirectObjectsRenderer;
    private final Environment environment;
    private List<PointLightShadow> pointLightShadows;
    private SunLightShadow sunLightShadow;

    public ShadowScene(Environment environment) {
        this.environment = environment;
        this.sunShadowFBO = new FBOTexture2DProgram(true);
        this.indirectObjectsRenderer = new IndirectObjectsRenderer(JGemsHelper.getScreen().getScene().getSceneRenderer(), null, null, false, true);
        this.initPointLightShadows();
        this.initSunLightShadow();
    }

    public static float qualityMultiplier() {
        int i = (int) JGemsHelper.MATH.clamp(JGems3D.get().getGameSettings().shadowQuality.getValue(), 0.0f, 2.0f);
        return i == 2 ? 1.0f : i == 1 ? 0.5f : 0.25f;
    }

    public void createResources() {
        this.getPointLightShadows().forEach(PointLightShadow::createResources);
        this.ge
    }

    public void destroyResources() {
        this.getPointLightShadows().forEach(PointLightShadow::destroyResources);
    }

    private Vector2i getShadowResolution() {
        return new Vector2i((int) (JGemsRenderingGlobalConstants.MAX_SHADOW_RES * ShadowScene.qualityMultiplier()));
    }

    private void initSunLightShadow() {
        this.sunLightShadow = new SunLightShadow(this.getEnvironment(), this.getShadowResolution());
    }

    private void initPointLightShadows() {
        this.pointLightShadows = new ArrayList<>(JGemsGlobalConfiguration.MAX_POINT_LIGHTS_SHADOWS);
        for (int i = 0; i < JGemsGlobalConfiguration.MAX_POINT_LIGHTS_SHADOWS; i++) {
            this.pointLightShadows.add(new PointLightShadow(this.getEnvironment(), this.getShadowResolution(), i));
        }
    }

    public void renderAllModelsInShadowMap(Set<SceneObject> modeledSceneObjectSet) {
        this.renderSceneInShadowMap(modeledSceneObjectSet);
    }

    public void renderSceneInShadowMap(Set<SceneObject> modeledSceneObjectSet) {
        if (!JGemsRenderingGlobalConstants.USE_SHADOWS || JGemsDebugGlobalConstants.FULL_BRIGHT) {
            this.renderNullShadows();
            return;
        }

        this.updateCascadeShadows(this.getCascadeShadows());
        Set<SceneObject> filtered = modeledSceneObjectSet.stream().filter(e -> e.getRenderAttributes().isShadowCaster()).collect(Collectors.toSet());
        boolean oldV = GL46.glIsEnabled(GL46.GL_CULL_FACE);
        if (JGemsRenderingGlobalConstants.DRAW_BACK_FACES_FOR_SHADOWS) {
            GL46.glDisable(GL46.GL_CULL_FACE);
        }
        //this.sunScene(filtered);
        //this.pointLightsScene(filtered);
        if (oldV) {
            GL46.glEnable(GL46.GL_CULL_FACE);
        }
    }

    private void renderShadows(Set<SceneObject> filteredObjectsSet) {
        Map<Boolean, List<SceneObject>> partitionedModels = filteredObjectsSet.stream().collect(Collectors.partitioningBy(e -> e.getModel().getMeshStructure().canBeUsedInIndirectRendering()));
        List<SceneObject> directRenderObjects = partitionedModels.get(false);
        List<SceneObject> indirectRenderObjects = partitionedModels.get(true);

        this.getSunShadowFBO().bindFBO();
        GL46.glViewport(0, 0, this.getShadowDim().x, this.getShadowDim().y);
        for (int i = 0; i < JGemsRenderingGlobalConstants.CASCADE_SPLITS; i++) {
            SunLightShadow.Cascade cascade = this.getCascadeShadows().get(i);
            this.getSunShadowFBO().connectTextureToBuffer(GL46.GL_COLOR_ATTACHMENT0, i);
            GL46.glClearColor(JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.x, JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.y, JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.x * JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.x, JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.y * JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.y);
            GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT | GL46.GL_COLOR_BUFFER_BIT);
            this.renderModelsIndirect(ShadingTable.Category.SUN_L_SHADOW_MAP, cascade.getLightProjectionViewMatrix(), indirectRenderObjects);
            this.renderModelsDirect(ShadingTable.Category.SUN_L_SHADOW_MAP, cascade.getLightProjectionViewMatrix(), directRenderObjects);
            GL46.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        }
        this.getSunShadowFBO().unBindFBO();
    }

    private void renderModelsIndirect(ShadingTable.Category category, Matrix4f lightProjection, List<SceneObject> filteredObjectsSet) {

    }

    private void renderModelsDirect(ShadingTable.Category category, Matrix4f lightProjection, List<SceneObject> filteredObjectsSet) {
        Map<JGemsShaderManager, List<SceneObject>> groupedObjects = filteredObjectsSet.stream().collect(Collectors.groupingBy(e -> e.getRenderAttributes().getShadingTable().getShader(category)));

        for (Map.Entry<JGemsShaderManager, List<SceneObject>> entry : groupedObjects.entrySet()) {
            JGemsShaderManager shaderManager = entry.getKey();
            shaderManager.beginShading();
            shaderManager.performUniformNoWarn(new UniformString("PosExp"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.EVSM_POSITIVE_EXPONENT));
            shaderManager.performUniformNoWarn(new UniformString("NegExp"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.EVSM_NEGATIVE_EXPONENT));
            shaderManager.performUniform(new UniformString("projection_view_matrix"), UniformFunctions.MAT4F(new Matrix4f(lightProjection)));
            for (SceneObject modeledSceneObject : filteredObjectsSet) {
                Model<Format3D> model = modeledSceneObject.getModel();
                if (model == null || model.getMeshStructure() == null) {
                    continue;
                }
                shaderManager.getUtils().performModel3DMatrix(model);
                this.renderModelForShadow(modeledSceneObject, shaderManager, modeledSceneObject.getRenderAttributes(), model);
            }
            shaderManager.endShading();
        }
    }

    private void renderNullShadows() {
        GL46.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.getSunShadowFBO().bindFBO();
        for (int i = 0; i < JGemsRenderingGlobalConstants.CASCADE_SPLITS; i++) {
            this.getSunShadowFBO().connectTextureToBuffer(GL46.GL_COLOR_ATTACHMENT0, i);
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
        }
        this.getSunShadowFBO().unBindFBO();

        for (int i = 0; i < JGemsGlobalConfiguration.MAX_POINT_LIGHTS_SHADOWS; i++) {
            PointLightShadow pointLightShadow = this.getPointLightShadows().get(i);
            pointLightShadow.getPointLightCubeMap().bindFBO();
            for (int j = 0; j < 6; j++) {
                pointLightShadow.getPointLightCubeMap().connectCubeMapToBuffer(GL46.GL_COLOR_ATTACHMENT0, j);
                GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
            }
            pointLightShadow.getPointLightCubeMap().unBindFBO();
        }

        GL46.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    private void sunScene(Set<SceneObject> modeledSceneObjectSet) {
        this.getSunShadowShader().beginShading();
        this.getSunShadowShader().performUniformNoWarn(new UniformString("PosExp"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.EVSM_POSITIVE_EXPONENT));
        this.getSunShadowShader().performUniformNoWarn(new UniformString("NegExp"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.EVSM_NEGATIVE_EXPONENT));
        this.getSunShadowFBO().bindFBO();
        GL46.glViewport(0, 0, this.getShadowDim().x, this.getShadowDim().y);

        for (int i = 0; i < JGemsRenderingGlobalConstants.CASCADE_SPLITS; i++) {
            SunLightShadow.Cascade cascade = this.getCascadeShadows().get(i);
            this.getSunShadowFBO().connectTextureToBuffer(GL46.GL_COLOR_ATTACHMENT0, i);
            GL46.glClearColor(JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.x, JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.y, JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.x * JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.x, JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.y * JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.y);
            GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT | GL46.GL_COLOR_BUFFER_BIT);
            this.getSunShadowShader().performUniform(new UniformString("projection_view_matrix"), UniformFunctions.MAT4F(new Matrix4f(cascade.getLightProjectionViewMatrix())));
            for (SceneObject modeledSceneObject : modeledSceneObjectSet) {
                Model<Format3D> model = modeledSceneObject.getModel();
                if (model == null || model.getMeshStructure() == null) {
                    continue;
                }
                this.getSunShadowShader().getUtils().performModel3DMatrix(model);
                this.renderModelForShadow(modeledSceneObject, this.getSunShadowShader(), modeledSceneObject.getRenderAttributes(), model);
            }
            GL46.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        }
        this.getSunShadowFBO().unBindFBO();
        this.getSunShadowShader().endShading();

        JGemsShaderManager shaderManager = JGemsResourceManager.globalShaderAssets.blur_box;
        this.getSunShadowFBO().bindFBO();
        GL46.glViewport(0, 0, this.getShadowDim().x, this.getShadowDim().y);
        shaderManager.beginShading();
        shaderManager.performUniform(new UniformString("projection_model_matrix"), UniformFunctions.MAT4F(TransformationUtils.getModelOrthographicMatrix(this.sunPostModel.getFormat(), TransformationUtils.getOrthographic2DMatrix(0, this.getShadowDim().x, this.getShadowDim().y, 0))));
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
        for (int i = 0; i < JGemsRenderingGlobalConstants.CASCADE_SPLITS; i++) {
            GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT);
            this.getSunShadowFBO().connectTextureToBuffer(GL46.GL_COLOR_ATTACHMENT0, i);
            shaderManager.performUniform(new UniformString("blur"), UniformFunctions.FLOAT(1.0f));
            shaderManager.performUniformTexture(new UniformString("texture_sampler"), this.getSunShadowFBO().getTextureIDByIndex(i), GL46.GL_TEXTURE_2D);
            JGemsHelper.RENDERING.renderModel(this.sunPostModel, GL46.GL_TRIANGLES);
        }
        shaderManager.endShading();
        this.getSunShadowFBO().unBindFBO();
    }

    private void pointLightsScene(Set<SceneObject> modeledSceneObjectSet) {
        this.getPointLightShadowShader().beginShading();
        GL46.glViewport(0, 0, this.getShadowDim().x, this.getShadowDim().y);

        for (int i = 0; i < JGemsGlobalConfiguration.MAX_POINT_LIGHTS_SHADOWS; i++) {
            PointLightShadow pointLightShadow = this.getPointLightShadows().get(i);
            if (pointLightShadow.isAttachedToLight() && pointLightShadow.getPointLight().isEnabled()) {
                pointLightShadow.getPointLightCubeMap().bindFBO();
                pointLightShadow.configureMatrices();
                this.getPointLightShadowShader().performUniform(new UniformString("far_plane"), UniformFunctions.FLOAT(pointLightShadow.farPlane()));
                this.getPointLightShadowShader().performUniform(new UniformString("lightPos"), UniformFunctions.VEC3F(pointLightShadow.getPointLight().getLightPos()));
                for (int j = 0; j < 6; j++) {
                    pointLightShadow.getPointLightCubeMap().connectCubeMapToBuffer(GL46.GL_COLOR_ATTACHMENT0, j);
                    this.getPointLightShadowShader().performUniform(new UniformString("view_matrix"), UniformFunctions.MAT4F(pointLightShadow.getShadowDirections().get(j)));
                    GL46.glClearColor(1.0f, 1.0f, 0.0f, 0.0f);
                    GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT | GL46.GL_COLOR_BUFFER_BIT);
                    for (SceneObject modeledSceneObject : modeledSceneObjectSet) {
                        Model<Format3D> model = modeledSceneObject.getModel();
                        if (model == null || model.getMeshStructure() == null) {
                            continue;
                        }
                        this.getPointLightShadowShader().getUtils().performModel3DMatrix(model);
                        this.renderModelForShadow(modeledSceneObject, this.getPointLightShadowShader(), modeledSceneObject.getRenderAttributes(), model);
                    }
                    GL46.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                }
                pointLightShadow.getPointLightCubeMap().unBindFBO();
            }
        }
        this.getPointLightShadowShader().endShading();
    }

    private void renderModelForShadow(IAnimated animated, JGemsShaderManager shaderManager, RenderAttributes modelRenderProperties, Model<?> model) {
        shaderManager.performUniform(new UniformString("alpha_discard"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.MAX_ALPHA_TO_DISCARD_SHADOW_FRAGMENT));
        shaderManager.getUtils().performAnimationsInfo(animated);
        float alphaValue = 1.0f;
        try {
            for (MeshGroup.MeshGroupNode meshNode : model.<MeshGroup>getMeshStructureWithUnSafeCast().getMeshNodes()) {
                if (meshNode.getMaterial().getDiffuse() instanceof ImageBasedTexture) {
                    shaderManager.performUniform(new UniformString("texture_sampler"), UniformFunctions.INTEGER(0));
                    GL46.glActiveTexture(GL46.GL_TEXTURE0);
                    ((ImageBasedTexture) meshNode.getMaterial().getDiffuse()).bindTexture();
                    shaderManager.performUniform(new UniformString("use_texture"), UniformFunctions.BOOLEAN(true));
                } else {
                    if (meshNode.getMaterial().getDiffuse() instanceof RGBAColor) {
                        RGBAColor RGBAColor = (RGBAColor) meshNode.getMaterial().getDiffuse();
                        alphaValue *= RGBAColor.getColor().w;
                    }
                    shaderManager.performUniform(new UniformString("use_texture"), UniformFunctions.BOOLEAN(false));
                }
                if (alphaValue * meshNode.getMaterial().getFullOpacity() <= JGemsRenderingGlobalConstants.MAX_ALPHA_TO_IGNORE_SHADOW) {
                    continue;
                }
                GL46.glBindVertexArray(meshNode.getMesh().getVao());
                meshNode.getMesh().enableAllMeshAttributes();
                GL46.glDrawElements(GL46.GL_TRIANGLES, meshNode.getMesh().getTotalVertices(), GL46.GL_UNSIGNED_INT, 0);
                meshNode.getMesh().disableAllMeshAttributes();
                GL46.glBindVertexArray(0);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new JGemsRuntimeException("There was an error, while rendering model for shadows. ");
        }
    }

    public void bindPointLightToShadowScene(int attachCode, PointLight pointLight) {
        if (attachCode >= JGemsGlobalConfiguration.MAX_POINT_LIGHTS_SHADOWS) {
            JGemsHelper.getLogger().warn("Couldn't attach point light with code: " + attachCode + ", because reached limit: " + JGemsGlobalConfiguration.MAX_POINT_LIGHTS_SHADOWS);
            return;
        }
        PointLightShadow pointLightShadow = this.getPointLightShadows().get(attachCode);
        pointLightShadow.setPointLight(pointLight);
    }

    public void unBindPointLightFromShadowScene(PointLight pointLight) {
        if (pointLight.getAttachedShadowSceneId() < 0) {
            JGemsHelper.getLogger().warn("Point Light " + pointLight.getAttachedShadowSceneId() + " is not attached to shadow scene!");
            return;
        }
        this.getPointLightShadows().get(pointLight.getAttachedShadowSceneId()).setPointLight(null);
    }

    public SunLightShadow getSunLightShadow() {
        return this.sunLightShadow;
    }

    public List<PointLightShadow> getPointLightShadows() {
        return this.pointLightShadows;
    }

    public Environment getEnvironment() {
        return this.environment;
    }
}