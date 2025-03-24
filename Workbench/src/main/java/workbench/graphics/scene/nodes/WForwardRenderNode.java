package workbench.graphics.scene.nodes;

import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.geometry.DirectGeometryRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.skybox.BackgroundRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.skybox.SkyboxRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.help.JGemsRenderingHelper;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import workbench.WBench;
import workbench.graphics.scene.nodes.templates.WIForwardRenderNode;
import workbench.graphics.scene.world.WBenchWorld;
import workbench.resources.WBenchResourceManager;

import java.util.Collection;
import java.util.function.Consumer;

public final class WForwardRenderNode extends IRenderNode.Template implements WIForwardRenderNode {
    private Collection<SceneObject> forwardRenderingObjects;
    private DirectGeometryRenderProcessor directGeometryRenderProcessor;
    private SkyboxRenderProcessor skyboxRenderProcessor;
    private BackgroundRenderProcessor backgroundRenderProcessor;
    private final FBOTexture2DProgram inColor;
    private Model3D flat;

    public WForwardRenderNode(@NotNull FBOTexture2DProgram inColor, OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.inColor = inColor;
    }

    @Override
    public FBOTexture2DProgram getInColorBuffer() {
        return this.inColor;
    }

    @Override
    public FBOTexture2DProgram getOutColorBuffer() {
        return this.getInColorBuffer();
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glBlendFunc(GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
        this.getBackgroundRenderProcessor().runProcessorRendering(frameTicking);
        GL46.glDisable(GL46.GL_BLEND);

        this.getOutColorBuffer().bindFBO();
        this.getDirectGeometryRenderProcessor().setDirectMeshObjects(this.getForwardRenderingObjects());
        this.getDirectGeometryRenderProcessor().runProcessorRendering(frameTicking);

        this.getSkyboxRenderProcessor().setBackgroundTexture(this.getBackgroundRenderProcessor().getBackground().getTextureByIndex(0));
        this.getSkyboxRenderProcessor().runProcessorRendering(frameTicking);

        GL46.glEnable(GL46.GL_BLEND);
        GL46.glBlendFunc(GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
        WBenchResourceManager.localShaderAssets.simple_flat.beginShading();
        WBenchResourceManager.localShaderAssets.simple_flat.performPerspectiveMatrix(new UniformString("projection_matrix"), JGemsTransformManager.INSTANCE.getPerspectiveMatrix());
        WBenchResourceManager.localShaderAssets.simple_flat.performModel3DMatrix(new UniformString("model_matrix"), TransformUtils.getModelMatrix(this.flat.getPose()));
        WBenchResourceManager.localShaderAssets.simple_flat.performViewMatrix(new UniformString("view_matrix"), JGemsTransformManager.INSTANCE.getCameraViewMatrix());
        WBenchResourceManager.localShaderAssets.simple_flat.performUniform(new UniformString("color"), UniformFunctions.VEC4F(new Vector4f(0.35f, 0.35f, 0.65f, 0.5f)));
        JGemsRenderingHelper.renderModel3D(this.flat, MeshStructure3D.SOLID_LAYER, GL46.GL_TRIANGLES);
        WBenchResourceManager.localShaderAssets.simple_flat.endShading();
        this.getOutColorBuffer().unBindFBO();
        GL46.glDisable(GL46.GL_BLEND);
    }

    public void initProcessors() {
        final Consumer<Pair<JGemsShaderManager, IRendered>> uniformsHandlerD = WDeferredRenderNode.getDefaultConsumerForDirectObjects((WBenchWorld) this.getWBenchWorld());
        WBenchWorld wBenchWorld = (WBenchWorld) this.getWBenchWorld();
        this.directGeometryRenderProcessor = new DirectGeometryRenderProcessor(uniformsHandlerD, Pipeline.SCENE, this.getOpenGLRenderer());
        this.skyboxRenderProcessor = new SkyboxRenderProcessor(wBenchWorld.getEnvironment().getSkyBox(), WBenchResourceManager.localShaderAssets.skybox, WBenchResourceManager.localModelAssets.defaultCube_gr, this.getOpenGLRenderer());
        this.backgroundRenderProcessor = new BackgroundRenderProcessor(this.getInColorBuffer(), WBenchResourceManager.localShaderAssets.IndirectBufferData, WBenchResourceManager.localShaderAssets.PropertiesData, wBenchWorld.getEnvironment().getSkyBox(), this.getOpenGLRenderer());
    }

    public void initFBOs() {
    }

    @Override
    public void createResources() {
        this.flat = MeshHelper.generatePlane3DModel(new Vector3f(-WBench.MAP_SIZE, 0.0f, -WBench.MAP_SIZE), new Vector3f(-WBench.MAP_SIZE, 0.0f, WBench.MAP_SIZE), new Vector3f(WBench.MAP_SIZE, 0.0f, -WBench.MAP_SIZE), new Vector3f(WBench.MAP_SIZE, 0.0f, WBench.MAP_SIZE));
        this.initFBOs();
        this.initProcessors();

        this.getDirectGeometryRenderProcessor().createResources();
        this.getSkyboxRenderProcessor().createResources();
        this.getBackgroundRenderProcessor().createResources();
    }

    @Override
    public void destroyResources() {
        if (this.flat != null) {
            this.flat.clear();
        }
        this.getDirectGeometryRenderProcessor().destroyResources();
        this.getSkyboxRenderProcessor().destroyResources();
        this.getBackgroundRenderProcessor().destroyResources();
    }

    @Override
    public void setForwardRenderingObjects(@NotNull Collection<SceneObject> forwardRenderingObjects) {
        this.forwardRenderingObjects = forwardRenderingObjects;
    }

    public BackgroundRenderProcessor getBackgroundRenderProcessor() {
        return this.backgroundRenderProcessor;
    }

    public SkyboxRenderProcessor getSkyboxRenderProcessor() {
        return this.skyboxRenderProcessor;
    }

    public DirectGeometryRenderProcessor getDirectGeometryRenderProcessor() {
        return this.directGeometryRenderProcessor;
    }

    @Override
    public Collection<SceneObject> getForwardRenderingObjects() {
        return this.forwardRenderingObjects;
    }

    @Override
    public Collection<SceneObject> getRejectedDirectForwardRenderingObjects() {
        return this.getDirectGeometryRenderProcessor().getRejected();
    }
}
