package javagems3d.graphics.rendering.scene.renderer.processors.post;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.decals.fx.DecalFX;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.help.JGemsHelper;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;

import java.util.Collection;

public class DeferredColorRenderProcessor extends IRenderProcessor.Template {
    private final JGemsShaderManager lightPassShader;
    private final FBOTexture2DProgram gBuffer;
    private final FBOTexture2DProgram ssaoBuffer;
    private final DeferredDecalsRenderProcessor deferredDecalsRenderProcessor;

    public DeferredColorRenderProcessor(@NotNull OpenGLRenderer openGLRenderer, @NotNull JGemsShaderManager deferredDecalsShaderManager, @Nullable FBOTexture2DProgram gBuffer, @Nullable FBOTexture2DProgram ssaoBuffer, @NotNull JGemsShaderManager lightPassShader) {
        super(openGLRenderer);
        this.deferredDecalsRenderProcessor = new DeferredDecalsRenderProcessor(deferredDecalsShaderManager);
        this.lightPassShader = lightPassShader;
        this.gBuffer = gBuffer;
        this.ssaoBuffer = ssaoBuffer;
    }

    public DeferredDecalsRenderProcessor getDeferredDecalsRenderProcessor() {
        return this.deferredDecalsRenderProcessor;
    }

    @Override
    public void createResources() {
    }

    @Override
    public void destroyResources() {
    }

    @Override
    public void runProcessorRendering(FrameTicking frameTicking) {
        FBOTexture2DProgram gBuffer = this.getGBuffer();
        FBOTexture2DProgram ssaoBuffer = this.getSsaoBuffer();

        JGemsShaderManager deferredShader = this.getLightPassShader();
        deferredShader.beginShading();
        final ICubeMapProgram cubeMapProgram = this.getWorld().getEnvironment().getSkyBox().getTexture();
        deferredShader.performUniformNoWarn(new UniformString(DefaultUniformDefinitions.CAMERA_POS), UniformFunctions.VEC3F(this.getWorld().getCamera().getCamPosition()));
        if (cubeMapProgram != null && deferredShader.isUniformExist(new UniformString(DefaultUniformDefinitions.AMBIENT_CUBEMAP))) {
            deferredShader.performUniformTextureBindless(new UniformString(DefaultUniformDefinitions.AMBIENT_CUBEMAP), cubeMapProgram);
            if (deferredShader.isUniformExist(new UniformString(DefaultUniformDefinitions.USE_CUBE_MAP))) {
                deferredShader.performUniform(new UniformString(DefaultUniformDefinitions.USE_CUBE_MAP), UniformFunctions.BOOLEAN(true));
            }
        } else {
            if (deferredShader.isUniformExist(new UniformString(DefaultUniformDefinitions.USE_CUBE_MAP))) {
                deferredShader.performUniform(new UniformString(DefaultUniformDefinitions.USE_CUBE_MAP), UniformFunctions.BOOLEAN(false));
            }
        }
        deferredShader.performUniform(new UniformString(DefaultUniformDefinitions.VIEW_MATRIX), UniformFunctions.MAT4F(JGemsTransformManager.INSTANCE.getCameraViewMatrix()));
        deferredShader.performUniform(new UniformString(DefaultUniformDefinitions.SHOW_CASCADES), UniformFunctions.BOOLEAN(JGemsConfig.DEBUG.SHOW_CASCADES));
        deferredShader.performUniformTexture(new UniformString(DefaultUniformDefinitions.G_POSITIONS), gBuffer.getTextureByIndex(0));
        deferredShader.performUniformTexture(new UniformString(DefaultUniformDefinitions.G_NORMALS), gBuffer.getTextureByIndex(1));
        deferredShader.performUniformTexture(new UniformString(DefaultUniformDefinitions.G_TEXTURE), gBuffer.getTextureByIndex(2));
        deferredShader.performUniformTexture(new UniformString(DefaultUniformDefinitions.G_EMISSION), gBuffer.getTextureByIndex(3));
        deferredShader.performUniformTexture(new UniformString(DefaultUniformDefinitions.G_METALLIC_ROUGHNESS), gBuffer.getTextureByIndex(4));
        if (ssaoBuffer != null) {
            deferredShader.performUniformTexture(new UniformString(DefaultUniformDefinitions.SSAO_MAP), ssaoBuffer.getTextureByIndex(0));
            deferredShader.performUniform(new UniformString(DefaultUniformDefinitions.IS_SSAO_VALID), UniformFunctions.BOOLEAN(true));
        } else {
            deferredShader.performUniform(new UniformString(DefaultUniformDefinitions.IS_SSAO_VALID), UniformFunctions.BOOLEAN(false));
        }
        deferredShader.performOrthographicMatrix(new UniformString(DefaultUniformDefinitions.PROJECTION_MODEL_MATRIX), this.getOpenGLRenderer().getScreenModel(), JGemsTransformManager.INSTANCE.getOrthographicMatrix());
        JGemsHelper.render().performShadowsInfo(this.getOpenGLRenderer().getWorld().getEnvironment(), deferredShader);
        JGemsHelper.render().renderModel2D(this.getOpenGLRenderer().getScreenModel(), GL46.GL_TRIANGLES);
        deferredShader.endShading();
    }

    protected FBOTexture2DProgram getGBuffer() {
        return this.gBuffer;
    }

    protected @Nullable FBOTexture2DProgram getSsaoBuffer() {
        return this.ssaoBuffer;
    }

    public JGemsShaderManager getLightPassShader() {
        return this.lightPassShader;
    }


    public class DeferredDecalsRenderProcessor {
        private final JGemsShaderManager deferredDecalsShaderManager;

        public DeferredDecalsRenderProcessor(JGemsShaderManager deferredDecalsShaderManager) {
            this.deferredDecalsShaderManager = deferredDecalsShaderManager;
        }

        public void renderDecals(Collection<DecalFX> filteredDecalsToRender, IEnvironment environment) {
            FBOTexture2DProgram gBuffer = DeferredColorRenderProcessor.this.getGBuffer();
            JGemsShaderManager deferredShader = this.deferredDecalsShaderManager;

            Vector3f cam = ((IRenderWorld) environment.getWorld()).getCamera().getCamPosition();

            boolean oldV = GL46.glIsEnabled(GL46.GL_CULL_FACE);
            GL46.glDisable(GL46.GL_CULL_FACE);
            for (DecalFX decalFX : filteredDecalsToRender) {
                //Vector3f min = decalFX.getCullingData().getAabbMin();
                //Vector3f max = decalFX.getCullingData().getAabbMax();
                //boolean cameraInside = cam.x >= min.x && cam.x <= max.x && cam.y >= min.y && cam.y <= max.y && cam.z >= min.z && cam.z <= max.z;
                //if (cameraInside) {
                  //  GL46.glDisable(GL46.GL_CULL_FACE);
                    GL46.glDisable(GL46.GL_DEPTH_TEST);
                //}
                deferredShader.beginShading();
                deferredShader.disableWarns();
                deferredShader.performUniform(new UniformString(DefaultUniformDefinitions.DECAL_INV_MODEL_MATRIX), UniformFunctions.MAT4F(decalFX.getInverseModelMatrix()));
                deferredShader.performUniform(new UniformString(DefaultUniformDefinitions.VIEW_MAT_INVERTED), UniformFunctions.MAT4F(JGemsTransformManager.INSTANCE.getCameraViewMatrix().invert()));
                deferredShader.performUniformTexture(new UniformString(DefaultUniformDefinitions.G_POSITIONS), gBuffer.getTextureByIndex(0));
                deferredShader.performUniformTexture(new UniformString(DefaultUniformDefinitions.G_NORMALS), gBuffer.getTextureByIndex(1));
                deferredShader.performUniformTexture(new UniformString(DefaultUniformDefinitions.G_TEXTURE), gBuffer.getTextureByIndex(2));
                deferredShader.performUniformTexture(new UniformString(DefaultUniformDefinitions.G_OBJ_DECAL_LAYERS), gBuffer.getTextureByIndex(5));
                deferredShader.performUniform(new UniformString(DefaultUniformDefinitions.DECAL_DIFFUSE_COLOR), UniformFunctions.VEC4F(new Vector4f(decalFX.getMaterial().diffuseColor().color(), decalFX.getDecalTextureProperties().getTransparency())));
                deferredShader.performUniformTexture(new UniformString(DefaultUniformDefinitions.G_EMISSION), gBuffer.getTextureByIndex(3));
                deferredShader.performUniformTexture(new UniformString(DefaultUniformDefinitions.DECAL_DIFFUSE_MAP), decalFX.getMaterial().textureMap());
                deferredShader.performUniform(new UniformString(DefaultUniformDefinitions.EMISSIVE_FACTOR), UniformFunctions.FLOAT(decalFX.getMaterial().emissiveFactor()));
                deferredShader.performUniform(new UniformString(DefaultUniformDefinitions.DECAL_ENT_LAYER_ID), UniformFunctions.UINTEGER(decalFX.getTerrainLayerID()));
                deferredShader.performUniform(new UniformString(DefaultUniformDefinitions.PROJECTION_MATRIX), UniformFunctions.MAT4F(JGemsTransformManager.INSTANCE.getPerspectiveMatrix()));
                deferredShader.performUniform(new UniformString(DefaultUniformDefinitions.MODEL_VIEW_MATRIX), UniformFunctions.MAT4F(TransformUtils.getModelViewMatrix(decalFX.getModelMatrix(), JGemsTransformManager.INSTANCE.getCameraViewMatrix())));
                //JGemsHelper.render().renderModel2D(DeferredColorRenderProcessor.this.getOpenGLRenderer().getScreenModel(), GL46.GL_TRIANGLES);
                JGemsHelper.render().renderMeshList3D(JGemsResourceManager.DEFAULT_CUBE_MESHGROUP().getAllNodes(), 0);
                deferredShader.endShading();
                deferredShader.enableWarns();
                GL46.glEnable(GL46.GL_DEPTH_TEST);
            }
            if (oldV) {
                GL46.glEnable(GL46.GL_CULL_FACE);
            }
        }
    }
}