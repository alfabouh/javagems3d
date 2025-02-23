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

import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.global.JGemsDebugGlobalConstants;
import javagems3d.global.JGemsGlobalConfiguration;
import javagems3d.global.JGemsRenderingGlobalConstants;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.JGemsEnvironment;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.environment.shadows.PointLightShadow;
import javagems3d.graphics.environment.shadows.SunLightShadow;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.fabric.DirectRenderFabric;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.indirect.GroupedIndirectRenderer;
import javagems3d.graphics.transformation.TransformUtils;

import javagems3d.system.resources.assets.models.Model2D;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.collections.Pair;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ShadowScene implements IShadowScene {
    private OpenGLRenderer openGLRenderer;
    private GroupedIndirectRenderer pointLightIndirectRendered;
    private GroupedIndirectRenderer sunlightIndirectRendered;
    private final IEnvironment environment;
    private List<PointLightShadow> pointLightShadows;
    private SunLightShadow sunLightShadow;

    public ShadowScene(IEnvironment environment) {
        this.environment = environment;
        this.initPointLightShadows();
        this.initSunLightShadow();
    }

    public static float qualityMultiplier() {
        int i = (int) JGemsHelper.MATH.clamp(JGems3D.get().getGameSettings().shadowQuality.getValue(), 0.0f, 2.0f);
        return i == 2 ? 1.0f : i == 1 ? 0.5f : 0.25f;
    }

    public void createResources(OpenGLRenderer openGLRenderer) {
        this.getSunLightShadow().setShadowMapResolution(this.getShadowResolution());
        this.getPointLightShadows().forEach(e -> e.setShadowMapResolution(this.getShadowResolution()));

        this.openGLRenderer = openGLRenderer;
        this.pointLightIndirectRendered = new GroupedIndirectRenderer(openGLRenderer, Pipeline.POINT_LIGHT_SHADOW_MAP, false, true);
        this.sunlightIndirectRendered = new GroupedIndirectRenderer(openGLRenderer, Pipeline.SUN_LIGHT_SHADOW_MAP, false, true);
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
        this.renderSceneInShadowMap(modeledSceneObjectSet);
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
        Pair<List<SceneObject>, List<SceneObject>> groups = this.divideSet2Groups(filtered);
        this.sunShadows(groups);
        this.pointLightShadows(groups);
        if (oldV) {
            GL46.glEnable(GL46.GL_CULL_FACE);
        }
        try (Model2D screenModel = MeshHelper.generatePlane2DModelInverted(new Vector2f(0.0f), new Vector2f(this.getSunLightShadow().getShadowMapResolution()), 0)) {
            final JGemsShaderManager blurring = JGemsResourceManager.globalShaderAssets.blur_box;
            this.blurSunShadow(screenModel, blurring);
        }
    }

    private void blurSunShadow(Model2D screenModel, final JGemsShaderManager blurring) {
        this.getSunLightShadow().getSunShadowFBO().bindFBO();
        Vector2i resolution = this.getSunLightShadow().getShadowMapResolution();
        OpenGLRenderer.setViewPort(resolution);
        blurring.beginShading();
        blurring.performUniform(new UniformString("projection_model_matrix"), UniformFunctions.MAT4F(TransformUtils.getModelOrthographicMatrix(screenModel.getPose(), TransformUtils.getOrthographic2DMatrix(0, resolution.x, resolution.y, 0))));
        for (int i = 0; i < JGemsRenderingGlobalConstants.CASCADE_SPLITS; i++) {
            GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT);
            this.getSunLightShadow().getSunShadowFBO().connectTextureToBuffer(GL46.GL_COLOR_ATTACHMENT0, i);
            blurring.performUniform(new UniformString("blur"), UniformFunctions.FLOAT(0.0f));
            blurring.performUniformTexture(new UniformString("texture_sampler"), this.getSunLightShadow().getSunShadowFBO().getTextureByIndex(i));
            JGemsHelper.RENDERING.renderModel2D(screenModel, GL46.GL_TRIANGLES);
        }
        blurring.endShading();
        this.getSunLightShadow().getSunShadowFBO().unBindFBO();
    }

    protected Pair<List<SceneObject>, List<SceneObject>> divideSet2Groups(Set<SceneObject> filteredObjectsSet) {
        Map<Boolean, List<SceneObject>> partitionedModels = filteredObjectsSet.stream().collect(Collectors.partitioningBy(e -> e.getModel().getMeshStructure().canBeUsedInIndirectRendering()));
        return new Pair<>(partitionedModels.get(false), partitionedModels.get(true));
    }

    @SuppressWarnings("all")
    protected void pointLightShadows(Pair<List<SceneObject>, List<SceneObject>> groups) {
        List<SceneObject> directRenderObjects = groups.getFirst();
        List<SceneObject> indirectRenderObjects = groups.getSecond();

        for (int i = 0; i < JGemsGlobalConfiguration.MAX_POINT_LIGHTS_SHADOWS; i++) {
            PointLightShadow pointLightShadow = this.getPointLightShadows().get(i);
            if (pointLightShadow.isAttachedToLight() && pointLightShadow.getPointLight().isEnabled()) {
                pointLightShadow.getPointLightCubeMap().bindFBO();
                OpenGLRenderer.setViewPort(pointLightShadow.getShadowMapResolution());
                pointLightShadow.configureMatrices();
                for (int j = 0; j < 6; j++) {
                    pointLightShadow.getPointLightCubeMap().connectCubeMapToBuffer(GL46.GL_COLOR_ATTACHMENT0, j);
                    GL46.glClearColor(1.0f, 1.0f, 0.0f, 0.0f);
                    GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT | GL46.GL_COLOR_BUFFER_BIT);
                    Matrix4f lightProjection = pointLightShadow.getShadowDirections().get(j);
                    Consumer<JGemsShaderManager> consumer = (shaderManager) -> {
                        shaderManager.performUniform(new UniformString("projection_view_matrix"), UniformFunctions.MAT4F(new Matrix4f(lightProjection)));
                        shaderManager.performUniform(new UniformString("far_plane"), UniformFunctions.FLOAT(pointLightShadow.farPlane()));
                        shaderManager.performUniform(new UniformString("lightPos"), UniformFunctions.VEC3F(pointLightShadow.getPointLight().getLightPos()));
                    };
                    this.renderModelsIndirect(consumer, Pipeline.POINT_LIGHT_SHADOW_MAP, indirectRenderObjects);
                    this.renderModelsDirect(consumer, Pipeline.POINT_LIGHT_SHADOW_MAP, directRenderObjects);
                    GL46.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                }
                pointLightShadow.getPointLightCubeMap().unBindFBO();
            }
        }
    }

    @SuppressWarnings("all")
    protected void sunShadows(Pair<List<SceneObject>, List<SceneObject>> groups) {
        List<SceneObject> directRenderObjects = groups.getFirst();
        List<SceneObject> indirectRenderObjects = groups.getSecond();

        this.getSunLightShadow().getSunShadowFBO().bindFBO();
        OpenGLRenderer.setViewPort(this.getSunLightShadow().getShadowMapResolution());
        for (int i = 0; i < JGemsRenderingGlobalConstants.CASCADE_SPLITS; i++) {
            SunLightShadow.Cascade cascade = this.getSunLightShadow().getCascades().get(i);
            this.getSunLightShadow().getSunShadowFBO().connectTextureToBuffer(GL46.GL_COLOR_ATTACHMENT0, i);
            GL46.glClearColor(JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.x, JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.y, JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.x * JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.x, JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.y * JGemsRenderingGlobalConstants.NEUTRAL_SHADOWS.y);
            GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT | GL46.GL_COLOR_BUFFER_BIT);
            final Matrix4f lightProjection = cascade.getLightProjectionViewMatrix();
            Consumer<JGemsShaderManager> consumer = (shaderManager) -> {
                shaderManager.performUniform(new UniformString("projection_view_matrix"), UniformFunctions.MAT4F(new Matrix4f(lightProjection)));
                shaderManager.performUniformNoWarn(new UniformString("PosExp"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.EVSM_POSITIVE_EXPONENT));
                shaderManager.performUniformNoWarn(new UniformString("NegExp"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.EVSM_POSITIVE_EXPONENT));
                shaderManager.performUniformTexture(new UniformString("animationsMatrix"), JGemsResourceManager.getAnimationsTextureBuffer());
            };
            this.renderModelsIndirect(consumer, Pipeline.SUN_LIGHT_SHADOW_MAP, indirectRenderObjects);
            this.renderModelsDirect(consumer, Pipeline.SUN_LIGHT_SHADOW_MAP, directRenderObjects);
        }
        GL46.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        this.getSunLightShadow().getSunShadowFBO().unBindFBO();
    }

    protected void renderModelsIndirect(Consumer<JGemsShaderManager> functionToHandleUniforms, Pipeline pipeline, List<SceneObject> filteredObjectsSet) {
        switch (pipeline) {
            case POINT_LIGHT_SHADOW_MAP: {
                this.getPointLightIndirectRendered().setIndirectMeshObjects(filteredObjectsSet);
                this.getPointLightIndirectRendered().processAndRender(ArbitraryArguments.pass(functionToHandleUniforms));
                break;
            }
            case SUN_LIGHT_SHADOW_MAP: {
                this.getSunlightIndirectRendered().setIndirectMeshObjects(filteredObjectsSet);
                this.getSunlightIndirectRendered().processAndRender(ArbitraryArguments.pass(functionToHandleUniforms));
                break;
            }
        }
    }

    protected void renderModelsDirect(Consumer<JGemsShaderManager> functionToHandleUniforms, Pipeline pipeline, List<SceneObject> filteredObjectsSet) {
        Map<JGemsShaderManager, List<SceneObject>> groupedObjects = filteredObjectsSet.stream().collect(Collectors.groupingBy(e -> e.getRenderingTable().getShaderManager(pipeline)));
        for (Map.Entry<JGemsShaderManager, List<SceneObject>> entry : groupedObjects.entrySet()) {
            JGemsShaderManager shaderManager = entry.getKey();
            shaderManager.beginShading();
            functionToHandleUniforms.accept(shaderManager);
            for (SceneObject modeledSceneObject : entry.getValue()) {
                Model3D model = modeledSceneObject.getModel();
                if (model == null || !model.isValid()) {
                    continue;
                }
                DirectRenderFabric directRenderFabric = modeledSceneObject.getRenderingTable().getRenderFabric(pipeline);
                directRenderFabric.onPreRender(pipeline, shaderManager, this.openGLRenderer, modeledSceneObject, null);
                directRenderFabric.onRender(pipeline, shaderManager, this.openGLRenderer, modeledSceneObject, null);
                directRenderFabric.onPostRender(pipeline, shaderManager, this.openGLRenderer, modeledSceneObject, null);
            }
            shaderManager.endShading();
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
            JGemsHelper.getLogger().warn("Point Light " + pointLight.getAttachedShadowSceneId() + " is not attached to shadow scene");
            return;
        }
        this.getPointLightShadows().get(pointLight.getAttachedShadowSceneId()).setPointLight(null);
    }

    public GroupedIndirectRenderer getPointLightIndirectRendered() {
        return this.pointLightIndirectRendered;
    }

    public GroupedIndirectRenderer getSunlightIndirectRendered() {
        return this.sunlightIndirectRendered;
    }

    public SunLightShadow getSunLightShadow() {
        return this.sunLightShadow;
    }

    public List<PointLightShadow> getPointLightShadows() {
        return this.pointLightShadows;
    }

    public IEnvironment getEnvironment() {
        return this.environment;
    }
}