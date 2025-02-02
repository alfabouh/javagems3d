package javagems3d.graphics.rendering.scene.renderer.processors.skybox;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.entities.background.SceneBackgroundProp;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.geometry.DirectGeometryRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.geometry.IndirectGeometryRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.CubeMapTexture;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BackgroundRenderProcessor extends IRenderProcessor.Template {
    private final FBOTexture2DProgram inColor;
    private final SkyBox skyBox;
    private DirectGeometryRenderProcessor directGeometryRenderProcessor;
    private IndirectGeometryRenderProcessor indirectGeometryRenderProcessor;
    private FBOTexture2DProgram background;

    public BackgroundRenderProcessor(@NotNull FBOTexture2DProgram inColor, @NotNull SkyBox skyBox, @NotNull OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.skyBox = skyBox;
        this.inColor = inColor;
    }

    @SuppressWarnings("all")
    @Override
    public void createResources() {
        final Consumer<JGemsShaderManager> uniformsHandler = (shaderManager) -> {
            final ICamera camera = this.getSkyBox().getBackground().getScaledCameraBackground();
            final Matrix4f cameraMatrix = TransformUtils.getViewMatrix(camera);
            final Matrix4f projection = JGemsTransformManager.INSTANCE.getPerspectiveMatrix();
            final CubeMapTexture cubeMapProgram = this.getSkyBox().getSky2DTexture();

            shaderManager.performUniformNoWarn(new UniformString("camera_pos"), UniformFunctions.VEC3F(camera.getCamPosition()));
            if (cubeMapProgram != null && shaderManager.isUniformExist(new UniformString("ambient_cube_map"))) {
                shaderManager.performUniformTexture(new UniformString("ambient_cube_map"), cubeMapProgram);
            }
            shaderManager.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(projection));
            shaderManager.performUniform(new UniformString("view_matrix"), UniformFunctions.MAT4F(cameraMatrix));
        };

        this.directGeometryRenderProcessor = new DirectGeometryRenderProcessor(Pipeline.SCENE, this.getOpenGLRenderer());
        this.indirectGeometryRenderProcessor = new IndirectGeometryRenderProcessor(uniformsHandler, Pipeline.SCENE, this.getOpenGLRenderer());

        this.getDirectGeometryRenderProcessor().createResources();
        this.getIndirectGeometryRenderProcessor().createResources();

        this.background = new FBOTexture2DProgram(true);
        T2DAttachmentContainer clr = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGBA16F, GL46.GL_RGBA);
            add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_RGB16F, GL46.GL_RGB);
        }};
        this.background.createFrameBuffer2DTexture(this.getRenderingResolution(), clr, true, GL46.GL_LINEAR, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
    }

    @Override
    public void destroyResources() {
        this.getDirectGeometryRenderProcessor().destroyResources();
        this.getIndirectGeometryRenderProcessor().destroyResources();

        if (this.getBackground() != null) {
            this.getBackground().clearFBO();
        }
    }

    @Override
    public void runProcessorRendering(FrameTicking frameTicking) {
        this.renderBackground(frameTicking);
    }

    protected void renderBackground(FrameTicking frameTicking) {
        Set<SceneBackgroundProp> toRender = this.getSkyBox().getBackground().getToRenderSet();
        Pair<List<SceneObject>, List<SceneObject>> groups = this.divideSet2Groups(toRender);
        List<SceneObject> directRenderObjects = groups.getFirst();
        List<SceneObject> indirectRenderObjects = groups.getSecond();

        this.getBackground().bindFBO();
        GL46.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        this.renderIndirectObjects(frameTicking, indirectRenderObjects);
        this.renderDirectObjects(frameTicking, directRenderObjects);
        GL46.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        this.getBackground().unBindFBO();
    }

    protected void renderDirectObjects(FrameTicking frameTicking, List<SceneObject> objects) {
        this.getDirectGeometryRenderProcessor().setDirectMeshObjects(objects);
        this.getDirectGeometryRenderProcessor().runProcessorRendering(frameTicking);
    }

    protected void renderIndirectObjects(FrameTicking frameTicking, List<SceneObject> objects) {
        this.getIndirectGeometryRenderProcessor().setIndirectMeshObjects(objects);
        this.getIndirectGeometryRenderProcessor().runProcessorRendering(frameTicking);
    }

    protected Pair<List<SceneObject>, List<SceneObject>> divideSet2Groups(Set<SceneBackgroundProp> filteredObjectsSet) {
        Map<Boolean, List<SceneObject>> partitionedModels = filteredObjectsSet.stream().collect(Collectors.partitioningBy(e -> e.getModel().getMeshStructure().canBeUsedInIndirectRendering()));
        return new Pair<>(partitionedModels.get(false), partitionedModels.get(true));
    }

    public FBOTexture2DProgram getBackground() {
        return this.background;
    }

    public DirectGeometryRenderProcessor getDirectGeometryRenderProcessor() {
        return this.directGeometryRenderProcessor;
    }

    public IndirectGeometryRenderProcessor getIndirectGeometryRenderProcessor() {
        return this.indirectGeometryRenderProcessor;
    }

    public SkyBox getSkyBox() {
        return this.skyBox;
    }

    public FBOTexture2DProgram getInColor() {
        return this.inColor;
    }
}
