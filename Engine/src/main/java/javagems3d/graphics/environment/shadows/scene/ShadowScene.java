package javagems3d.graphics.environment.shadows.scene;

import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.pipeline.enums.Type;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.environment.shadows.PointLightShadow;
import javagems3d.graphics.environment.shadows.SunLightShadow;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.fabric.DirectRenderFabric;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.indirect.GroupedIndirectRenderer;

import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.collections.Pair;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class ShadowScene implements IShadowScene {
    private OpenGLRenderer openGLRenderer;
    private GroupedIndirectRenderer pointLightIndirectRendered;
    private GroupedIndirectRenderer sunlightIndirectRendered;
    private final IEnvironment environment;
    private List<PointLightShadow> pointLightShadows;
    private SunLightShadow sunLightShadow;

    public ShadowScene(IEnvironment environment, int totalCascades) {
        this.environment = environment;
        this.initPointLightShadows();
        this.initSunLightShadow(totalCascades);
    }
    
    public void createResources(OpenGLRenderer openGLRenderer) {
        this.getSunLightShadow().setShadowMapResolution(this.getShadowResolution());
        this.getPointLightShadows().forEach(e -> e.setShadowMapResolution(this.getShadowResolution()));

        this.openGLRenderer = openGLRenderer;
        this.pointLightIndirectRendered = new GroupedIndirectRenderer(openGLRenderer, this.getIndirectSSBO(), this.getPropertiesSSBO(), Pipeline.POINT_LIGHT_SHADOW_MAP, true, true);
        this.sunlightIndirectRendered = new GroupedIndirectRenderer(openGLRenderer, this.getIndirectSSBO(), this.getPropertiesSSBO(), Pipeline.SUN_LIGHT_SHADOW_MAP, true, true);
        this.getPointLightShadows().forEach(PointLightShadow::createResources);
        this.getSunLightShadow().createResources();
    }

    public void destroyResources() {
        this.getPointLightShadows().forEach(PointLightShadow::destroyResources);
        this.getSunLightShadow().destroyResources();
    }

    protected abstract @NotNull ShaderStorageBufferObject getIndirectSSBO();
    protected abstract @NotNull ShaderStorageBufferObject getPropertiesSSBO();

    protected abstract @NotNull Vector2i getShadowResolution();
    protected abstract int getMaxPointLightShadows();
    protected abstract boolean shouldNotRenderShadows();

    protected void initSunLightShadow(int totalCascades) {
        this.sunLightShadow = new SunLightShadow(this.getEnvironment(), this.getShadowResolution(), totalCascades);
    }

    protected void initPointLightShadows() {
        this.pointLightShadows = new ArrayList<>(this.getMaxPointLightShadows());
        for (int i = 0; i < this.getMaxPointLightShadows(); i++) {
            this.pointLightShadows.add(new PointLightShadow(this.getEnvironment(), this.getShadowResolution(), i));
        }
    }

    public void renderAllModelsInShadowMap(Set<? extends SceneObject> modeledSceneObjectSet) {
        this.renderSceneInShadowMap(modeledSceneObjectSet);
    }

    public void renderSceneInShadowMap(Set<? extends SceneObject> modeledSceneObjectSet) {
        if (this.shouldNotRenderShadows()) {
            this.renderNullShadows();
            return;
        }
        this.getSunLightShadow().refreshCascades();
        Set<SceneObject> filtered = modeledSceneObjectSet.stream().filter(e -> e.hasModel() && e.getRenderAttributes().getProperties().getBool(JGemsRenderProperties.KEY_SHADOW_CASTER)).collect(Collectors.toSet());
        boolean oldV = GL46.glIsEnabled(GL46.GL_CULL_FACE);
        if (JGemsConfig.SYSTEM.DRAW_BACK_FACES_FOR_SHADOWS) {
            GL46.glDisable(GL46.GL_CULL_FACE);
        }
        this.sunShadows(this.divideSet2Groups(filtered, Pipeline.SUN_LIGHT_SHADOW_MAP));
        this.pointLightShadows(this.divideSet2Groups(filtered, Pipeline.POINT_LIGHT_SHADOW_MAP));
        if (oldV) {
            GL46.glEnable(GL46.GL_CULL_FACE);
        }
        this.blurShadows(this.getSunLightShadow().getSunShadowFBO());
    }

    protected abstract void blurShadows(FBOTexture2DProgram sunShadowFBO);

    protected Pair<List<SceneObject>, List<SceneObject>> divideSet2Groups(Set<SceneObject> filteredObjectsSet, Pipeline pipeline) {
        Map<Boolean, List<SceneObject>> partitionedModels = filteredObjectsSet.stream().collect(Collectors.partitioningBy(e -> Objects.requireNonNull(e.getRenderTable().getRenderingData(pipeline)).getRenderFabric().getRenderingType().equals(Type.INDIRECT)));
        return new Pair<>(partitionedModels.get(false), partitionedModels.get(true));
    }

    @SuppressWarnings("all")
    protected void pointLightShadows(Pair<List<SceneObject>, List<SceneObject>> groups) {
        List<SceneObject> directRenderObjects = groups.getFirst();
        List<SceneObject> indirectRenderObjects = groups.getSecond();

        for (int i = 0; i < this.getMaxPointLightShadows(); i++) {
            PointLightShadow pointLightShadow = this.getPointLightShadows().get(i);
            if (pointLightShadow.isAttachedToLight() && pointLightShadow.getPointLight().isActive()) {
                pointLightShadow.getPointLightCubeMap().bindFBO();
                OpenGLRenderer.setViewPort(pointLightShadow.getShadowMapResolution());
                pointLightShadow.configureMatrices();
                for (int j = 0; j < 6; j++) {
                    pointLightShadow.getPointLightCubeMap().connectCubeMapToBuffer(GL46.GL_COLOR_ATTACHMENT0, j);
                    GL46.glClearColor(1.0f, 1.0f, 0.0f, 0.0f);
                    GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT | GL46.GL_COLOR_BUFFER_BIT);
                    Matrix4f lightProjection = pointLightShadow.getShadowDirections().get(j);
                    Consumer<JGemsShaderManager> consumer = this.getUniformsConsumerPointLightShadows(pointLightShadow, lightProjection);
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
        for (int i = 0; i < this.getSunLightShadow().getTotalCascades(); i++) {
            SunLightShadow.Cascade cascade = this.getSunLightShadow().getCascades().get(i);
            this.getSunLightShadow().getSunShadowFBO().connectTextureToBuffer(GL46.GL_COLOR_ATTACHMENT0, i);
            GL46.glClearColor(JGemsConfig.SYSTEM.NEUTRAL_SHADOWS.x, JGemsConfig.SYSTEM.NEUTRAL_SHADOWS.y, JGemsConfig.SYSTEM.NEUTRAL_SHADOWS.x * JGemsConfig.SYSTEM.NEUTRAL_SHADOWS.x, JGemsConfig.SYSTEM.NEUTRAL_SHADOWS.y * JGemsConfig.SYSTEM.NEUTRAL_SHADOWS.y);
            GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT | GL46.GL_COLOR_BUFFER_BIT);
            final Matrix4f lightProjection = cascade.getLightProjectionViewMatrix();
            Consumer<JGemsShaderManager> consumer = this.getUniformsConsumerSunShadows(cascade, lightProjection);
            this.renderModelsIndirect(consumer, Pipeline.SUN_LIGHT_SHADOW_MAP, indirectRenderObjects);
            this.renderModelsDirect(consumer, Pipeline.SUN_LIGHT_SHADOW_MAP, directRenderObjects);
        }
        GL46.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        this.getSunLightShadow().getSunShadowFBO().unBindFBO();
    }

    protected abstract @NotNull Consumer<JGemsShaderManager> getUniformsConsumerSunShadows(SunLightShadow.Cascade cascade, Matrix4f lightProjection);
    protected abstract @NotNull Consumer<JGemsShaderManager> getUniformsConsumerPointLightShadows(PointLightShadow pointLightShadow, Matrix4f lightProjection);

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
        Map<JGemsShaderManager, List<SceneObject>> groupedObjects = OpenGLRenderer.groupObjectsFromShaders(filteredObjectsSet, pipeline);
        for (Map.Entry<JGemsShaderManager, List<SceneObject>> entry : groupedObjects.entrySet()) {
            JGemsShaderManager shaderManager = entry.getKey();
            shaderManager.beginShading();
            for (SceneObject modeledSceneObject : entry.getValue()) {
                Model3D model = modeledSceneObject.getModel();
                if (model == null || !model.isValid()) {
                    continue;
                }
                DirectRenderFabric directRenderFabric = Objects.requireNonNull(modeledSceneObject.getRenderTable().getRenderingData(pipeline)).getRenderFabric();
                directRenderFabric.onPreRender(pipeline, shaderManager, this.openGLRenderer, modeledSceneObject, null);
                directRenderFabric.onRender(pipeline, shaderManager, this.openGLRenderer, modeledSceneObject, ArbitraryArguments.pass(functionToHandleUniforms));
                directRenderFabric.onPostRender(pipeline, shaderManager, this.openGLRenderer, modeledSceneObject, null);
            }
            shaderManager.endShading();
        }
    }

    protected void renderNullShadows() {
        GL46.glClearColor(JGemsConfig.SYSTEM.NEUTRAL_SHADOWS.x, JGemsConfig.SYSTEM.NEUTRAL_SHADOWS.y, JGemsConfig.SYSTEM.NEUTRAL_SHADOWS.x * JGemsConfig.SYSTEM.NEUTRAL_SHADOWS.x, JGemsConfig.SYSTEM.NEUTRAL_SHADOWS.y * JGemsConfig.SYSTEM.NEUTRAL_SHADOWS.y);
        this.getSunLightShadow().getSunShadowFBO().bindFBO();
        for (int i = 0; i < this.getSunLightShadow().getTotalCascades(); i++) {
            this.getSunLightShadow().getSunShadowFBO().connectTextureToBuffer(GL46.GL_COLOR_ATTACHMENT0, i);
            GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
        }
        this.getSunLightShadow().getSunShadowFBO().unBindFBO();

        for (int i = 0; i < this.getMaxPointLightShadows(); i++) {
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
        if (attachCode >= this.getMaxPointLightShadows()) {
            Log.get().warn("Couldn't attach point light with code: " + attachCode + ", because reached limit: " + this.getMaxPointLightShadows());
            return;
        }
        PointLightShadow pointLightShadow = this.getPointLightShadows().get(attachCode);
        pointLightShadow.setPointLight(pointLight);
    }

    public void unBindPointLightFromShadowScene(PointLight pointLight) {
        if (pointLight.getAttachedShadowSceneId() < 0) {
            Log.get().warn("Point Light " + pointLight.getAttachedShadowSceneId() + " is not attached to shadow scene");
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