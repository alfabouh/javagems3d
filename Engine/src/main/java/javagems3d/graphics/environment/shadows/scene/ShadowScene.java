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
import javagems3d.graphics.objects.rendering.configuration.ShadingTable;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.indirect.IndirectObjectsRenderer;
import javagems3d.graphics.transformation.JGemsTransformation;
import javagems3d.system.resources.assets.models.mesh.structures.MeshGroup;
import javagems3d.system.resources.assets.texturing.CubeMapTexture;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.joml.*;
import org.lwjgl.opengl.GL46;
import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.global.JGemsDebugGlobalConstants;
import javagems3d.global.JGemsRenderingGlobalConstants;
import javagems3d.graphics.objects.SceneObject;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ShadowScene implements IShadowScene {
    private IndirectObjectsRenderer indirectObjectsRenderer;
    private final Environment environment;
    private List<PointLightShadow> pointLightShadows;
    private SunLightShadow sunLightShadow;

    private static final IndirectObjectsRenderer.RenderingFunction func = (shaderManager, indirectBufferCommandsBuilder, renderBuffer, metaData) -> {
        Matrix4f lightProjection = null;
        try {
            lightProjection = (Matrix4f) metaData[0];
        } catch (Exception e) {
            throw new JGemsRuntimeException("ShadowScene caught an error!\n", e);
        }
        shaderManager.beginShading();
        shaderManager.performUniform(new UniformString("projection_view_matrix"), UniformFunctions.MAT4F(new Matrix4f(lightProjection)));
        GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, indirectBufferCommandsBuilder.getRenderBufferHandle());
        GL46.glBindVertexArray(renderBuffer.getStaticVao());
        GL46.glMultiDrawElementsIndirect(GL46.GL_TRIANGLES, GL46.GL_UNSIGNED_INT, 0, indirectBufferCommandsBuilder.getDrawCount(), 0);
        GL46.glBindVertexArray(0);
        GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, 0);
        shaderManager.endShading();
    };

    public ShadowScene(Environment environment) {
        this.environment = environment;
        this.initPointLightShadows();
        this.initSunLightShadow();
    }

    public static float qualityMultiplier() {
        int i = (int) JGemsHelper.MATH.clamp(JGems3D.get().getGameSettings().shadowQuality.getValue(), 0.0f, 2.0f);
        return i == 2 ? 1.0f : i == 1 ? 0.5f : 0.25f;
    }

    public void createResources() {
        this.indirectObjectsRenderer = new IndirectObjectsRenderer(JGemsHelper.getScreen().getScene().getSceneRenderer(), ShadowScene.func, null, false, true);
        this.getPointLightShadows().forEach(PointLightShadow::createResources);
        this.getSunLightShadow().createResources();
    }

    public void destroyResources() {
        this.getPointLightShadows().forEach(PointLightShadow::destroyResources);
        this.getSunLightShadow().destroyResources();
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
      //  this.renderSceneInShadowMap(modeledSceneObjectSet);
    }

    public void renderSceneInShadowMap(Set<SceneObject> modeledSceneObjectSet) {
        if (!JGemsRenderingGlobalConstants.USE_SHADOWS || JGemsDebugGlobalConstants.FULL_BRIGHT) {
            this.renderNullShadows();
            return;
        }
        this.getSunLightShadow().refreshCascades();
        Set<SceneObject> filtered = modeledSceneObjectSet.stream().filter(e -> e.hasModel() && e.getRenderAttributes().isShadowCaster()).collect(Collectors.toSet());
        boolean oldV = GL46.glIsEnabled(GL46.GL_CULL_FACE);
        if (JGemsRenderingGlobalConstants.DRAW_BACK_FACES_FOR_SHADOWS) {
            GL46.glDisable(GL46.GL_CULL_FACE);
        }
        this.sunShadows(filtered);
        this.pointLightShadows(filtered);
        if (oldV) {
            GL46.glEnable(GL46.GL_CULL_FACE);
        }
    }

    protected Pair<List<SceneObject>, List<SceneObject>> divideSet2Groups(Set<SceneObject> filteredObjectsSet) {
        Map<Boolean, List<SceneObject>> partitionedModels = filteredObjectsSet.stream().collect(Collectors.partitioningBy(e -> e.getModel().getMeshStructure().canBeUsedInIndirectRendering()));
        return new Pair<>(partitionedModels.get(false), partitionedModels.get(true));
    }

    protected void pointLightShadows(Set<SceneObject> filteredObjectsSet) {
        Pair<List<SceneObject>, List<SceneObject>> groups = this.divideSet2Groups(filteredObjectsSet);
        List<SceneObject> directRenderObjects = groups.getFirst();
        List<SceneObject> indirectRenderObjects = groups.getSecond();

        for (int i = 0; i < JGemsGlobalConfiguration.MAX_POINT_LIGHTS_SHADOWS; i++) {
            PointLightShadow pointLightShadow = this.getPointLightShadows().get(i);
            if (pointLightShadow.isAttachedToLight() && pointLightShadow.getPointLight().isEnabled()) {
                pointLightShadow.getPointLightCubeMap().bindFBO();
                GL46.glViewport(0, 0, pointLightShadow.getShadowMapResolution().x, pointLightShadow.getShadowMapResolution().y);
                pointLightShadow.configureMatrices();
                for (int j = 0; j < 6; j++) {
                    pointLightShadow.getPointLightCubeMap().connectCubeMapToBuffer(GL46.GL_COLOR_ATTACHMENT0, j);
                    GL46.glClearColor(1.0f, 1.0f, 0.0f, 0.0f);
                    GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT | GL46.GL_COLOR_BUFFER_BIT);
                    Matrix4f lightProjection = pointLightShadow.getShadowDirections().get(j);
                    this.renderModelsIndirect(ShadingTable.Category.POINT_L_SHADOW_MAP, lightProjection, indirectRenderObjects);
                    this.renderModelsDirect((shaderManager) -> {
                        shaderManager.performUniform(new UniformString("far_plane"), UniformFunctions.FLOAT(pointLightShadow.farPlane()));
                        shaderManager.performUniform(new UniformString("lightPos"), UniformFunctions.VEC3F(pointLightShadow.getPointLight().getLightPos()));
                    }, ShadingTable.Category.POINT_L_SHADOW_MAP, lightProjection, directRenderObjects);
                    GL46.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                }
                pointLightShadow.getPointLightCubeMap().unBindFBO();
            }
        }
    }

    protected void sunShadows(Set<SceneObject> filteredObjectsSet) {
        Pair<List<SceneObject>, List<SceneObject>> groups = this.divideSet2Groups(filteredObjectsSet);
        List<SceneObject> directRenderObjects = groups.getFirst();
        List<SceneObject> indirectRenderObjects = groups.getSecond();

        this.getSunLightShadow().getSunShadowFBO().bindFBO();
        GL46.glViewport(0, 0, this.getSunLightShadow().getShadowMapResolution().x, this.getSunLightShadow().getShadowMapResolution().y);
        for (int i = 0; i < JGemsRenderingGlobalConstants.CASCADE_SPLITS; i++) {
            SunLightShadow.Cascade cascade = this.getSunLightShadow().getCascades().get(i);
            this.getSunLightShadow().getSunShadowFBO().connectTextureToBuffer(GL46.GL_COLOR_ATTACHMENT0, i);
            GL46.glClearColor(JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.x, JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.y, JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.x * JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.x, JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.y * JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.y);
            GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT | GL46.GL_COLOR_BUFFER_BIT);
            Matrix4f lightProjection = cascade.getLightProjectionViewMatrix();
            this.renderModelsIndirect(ShadingTable.Category.SUN_L_SHADOW_MAP, lightProjection, indirectRenderObjects);
            this.renderModelsDirect((shaderManager) -> {
                shaderManager.performUniformNoWarn(new UniformString("PosExp"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.EVSM_POSITIVE_EXPONENT));
                shaderManager.performUniformNoWarn(new UniformString("NegExp"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.EVSM_NEGATIVE_EXPONENT));
            }, ShadingTable.Category.SUN_L_SHADOW_MAP, lightProjection, directRenderObjects);
            GL46.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        }
        this.getSunLightShadow().getSunShadowFBO().unBindFBO();
    }

    protected void renderModelsIndirect(ShadingTable.Category category, Matrix4f lightProjection, List<SceneObject> filteredObjectsSet) {
        this.getIndirectObjectsRenderer().setIndirectMeshObjects(filteredObjectsSet);
        this.getIndirectObjectsRenderer().processAndRender(e -> e.getRenderAttributes().getShadingTable().getShader(category), lightProjection);
    }

    protected void renderModelsDirect(Consumer<JGemsShaderManager> functionToHandleUniforms, ShadingTable.Category category, Matrix4f lightProjection, List<SceneObject> filteredObjectsSet) {
        Map<JGemsShaderManager, List<SceneObject>> groupedObjects = filteredObjectsSet.stream().collect(Collectors.groupingBy(e -> e.getRenderAttributes().getShadingTable().getShader(category)));
        for (Map.Entry<JGemsShaderManager, List<SceneObject>> entry : groupedObjects.entrySet()) {
            JGemsShaderManager shaderManager = entry.getKey();
            shaderManager.beginShading();
            functionToHandleUniforms.accept(shaderManager);
            shaderManager.performUniform(new UniformString("projection_view_matrix"), UniformFunctions.MAT4F(new Matrix4f(lightProjection)));
            for (SceneObject modeledSceneObject : filteredObjectsSet) {
                Model<Format3D> model = modeledSceneObject.getModel();
                if (model == null || model.getMeshStructure() == null) {
                    continue;
                }
                shaderManager.getUtils().performModel3DMatrix(model);
                this.renderModelForShadow(modeledSceneObject, shaderManager, model);
            }
            shaderManager.endShading();
        }
    }

    protected void renderModelForShadow(IAnimated animated, JGemsShaderManager shaderManager, Model<?> model) {
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

    protected void renderNullShadows() {
        GL46.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.getSunLightShadow().getSunShadowFBO().bindFBO();
        for (int i = 0; i < JGemsRenderingGlobalConstants.CASCADE_SPLITS; i++) {
            this.getSunLightShadow().getSunShadowFBO().connectTextureToBuffer(GL46.GL_COLOR_ATTACHMENT0, i);
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
        }
        this.getSunLightShadow().getSunShadowFBO().unBindFBO();

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

    protected IndirectObjectsRenderer getIndirectObjectsRenderer() {
        return this.indirectObjectsRenderer;
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