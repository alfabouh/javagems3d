package workbench.graphics.scene.ui.game.editor.scenes.window;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.args.ArbitraryArguments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;
import workbench.WBench;
import workbench.graphics.scene.ui.game.editor.ActionsInterfaceComponentG;
import workbench.graphics.scene.ui.game.editor.instances.mapping.SkyBoxAssetPreview;
import workbench.graphics.screen.WBenchScreen;
import javagems3d.system.external.gaming.def.misc.GameResourceTextureAsset;
import workbench.resources.WBenchResourceManager;
import workbench.resources.initialization.game.WBenchGameEditorTextureAssetsInitializer;

public class SkyBoxPreviewEditorWindow {
    private final ActionsInterfaceComponentG actionsInterfaceComponentG;
    private final OpenGLRenderer openGLRenderer;

    public SkyBoxPreviewEditorWindow(ActionsInterfaceComponentG actionsInterfaceComponentG, OpenGLRenderer openGLRenderer) {
        this.actionsInterfaceComponentG = actionsInterfaceComponentG;
        this.openGLRenderer = openGLRenderer;
    }

    public void render(Matrix4f projection, Matrix4f view, @NotNull SkyBoxAssetPreview skyBoxAssetPreview, FBOTexture2DProgram modelScenePreview) {
        modelScenePreview.bindFBO();
        GL46.glClearColor(0.0f, 0.0f, 0.5f, 1.0f);
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        OpenGLRenderer.setViewPort(new Vector2i(1024, 1024));
        this.renderSkyFace(projection, view
                , new Vector3f(-1.0f, 1.0f, -1.0f)
                , new Vector3f(-1.0f, 1.0f, 1.0f)
                , new Vector3f(1.0f, 1.0f, 1.0f)
                , new Vector3f(1.0f, 1.0f, -1.0f)
                , skyBoxAssetPreview.getAsset().getCmTextures().getTextureUPPath() == null ? null : skyBoxAssetPreview.getAsset().getCmTextures().getTextureUPPath().toString(),
                WBenchGameEditorTextureAssetsInitializer.hintUP
        );
        this.renderSkyFace(projection, view
                , new Vector3f(-1.0f, -1.0f, -1.0f)
                , new Vector3f(-1.0f, -1.0f, 1.0f)
                , new Vector3f(-1.0f, 1.0f, 1.0f)
                , new Vector3f(-1.0f, 1.0f, -1.0f)
                , skyBoxAssetPreview.getAsset().getCmTextures().getTextureLEFTPath() == null ? null : skyBoxAssetPreview.getAsset().getCmTextures().getTextureLEFTPath().toString(),
                WBenchGameEditorTextureAssetsInitializer.hintLEFT
        );
        this.renderSkyFace(projection, view
                , new Vector3f(1.0f, -1.0f, 1.0f)
                , new Vector3f(1.0f, -1.0f, -1.0f)
                , new Vector3f(1.0f, 1.0f, -1.0f)
                , new Vector3f(1.0f, 1.0f, 1.0f)
                , skyBoxAssetPreview.getAsset().getCmTextures().getTextureRIGHTPath() == null ? null : skyBoxAssetPreview.getAsset().getCmTextures().getTextureRIGHTPath().toString(),
                WBenchGameEditorTextureAssetsInitializer.hintRIGHT
        );
        this.renderSkyFace(projection, view
                , new Vector3f(-1.0f, -1.0f,  1.0f)
                , new Vector3f( 1.0f, -1.0f,  1.0f)
                , new Vector3f( 1.0f,  1.0f,  1.0f)
                , new Vector3f(-1.0f,  1.0f,  1.0f)
                , skyBoxAssetPreview.getAsset().getCmTextures().getTextureFRONTPath() == null ? null : skyBoxAssetPreview.getAsset().getCmTextures().getTextureFRONTPath().toString(),
                WBenchGameEditorTextureAssetsInitializer.hintFRONT
        );
        this.renderSkyFace(projection, view
                , new Vector3f( 1.0f, -1.0f, -1.0f)
                , new Vector3f(-1.0f, -1.0f, -1.0f)
                , new Vector3f(-1.0f,  1.0f, -1.0f)
                , new Vector3f( 1.0f,  1.0f, -1.0f)
                , skyBoxAssetPreview.getAsset().getCmTextures().getTextureBACKPath() == null ? null : skyBoxAssetPreview.getAsset().getCmTextures().getTextureBACKPath().toString(),
                WBenchGameEditorTextureAssetsInitializer.hintBACK
        );
        this.renderSkyFace(projection, view
                , new Vector3f(1.0f, -1.0f, -1.0f)
                , new Vector3f(1.0f, -1.0f, 1.0f)
                , new Vector3f(-1.0f, -1.0f, 1.0f)
                , new Vector3f(-1.0f, -1.0f, -1.0f)
                , skyBoxAssetPreview.getAsset().getCmTextures().getTextureBOTTOMPath() == null ? null : skyBoxAssetPreview.getAsset().getCmTextures().getTextureBOTTOMPath().toString(),
                WBenchGameEditorTextureAssetsInitializer.hintBOTTOM
        );
        OpenGLRenderer.setViewPort(this.openGLRenderer.getRenderingResolution());
        modelScenePreview.unBindFBO();
        WBenchScreen.clearColor();
    }

    private void renderSkyFace(Matrix4f projection, Matrix4f view, @NotNull Vector3f pos1, @NotNull Vector3f pos2, @NotNull Vector3f pos3, @NotNull Vector3f pos4, @Nullable String textureRelativePos, @NotNull ITexture2DProgram hint) {
        try (Model3D model = MeshHelper.generatePlane3DModel(ArbitraryArguments.pass(false), pos1, pos2, pos3, pos4)) {
            WBenchResourceManager.localShaderAssets.simple_skybox_face.beginShading();
            WBenchResourceManager.localShaderAssets.simple_skybox_face.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(projection));
            WBenchResourceManager.localShaderAssets.simple_skybox_face.performViewMatrix(new UniformString("view"), view);
            final GameResourceTextureAsset program = textureRelativePos == null ? null : WBench.get().getGameProjectManager().getGameResourcesManager().extractFromCacheTexture(textureRelativePos);
            WBenchResourceManager.localShaderAssets.simple_skybox_face.performUniformTexture(new UniformString("skybox_face2D"), program == null ? JGemsResourceManager.DEFAULT_TEXTURE() : program.getTexture2DProgram());
            WBenchResourceManager.localShaderAssets.simple_skybox_face.performUniformTexture(new UniformString("skybox_faceHint"), hint);
            WBenchResourceManager.localShaderAssets.simple_skybox_face.performUniform(new UniformString("doHint"), UniformFunctions.BOOLEAN(this.actionsInterfaceComponentG.getScenePreviewSkyBoxG().isShowHint()));
            JGemsHelper.render().renderModel3D(model, MeshStructure3D.SOLID_LAYER, GL46.GL_TRIANGLES);
            WBenchResourceManager.localShaderAssets.simple_skybox_face.endShading();
        }
    }

    /*
    GL46.glEnable(GL46.GL_BLEND);
        GL46.glBlendFunc(GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
        WBenchResourceManager.localShaderAssets.simple_flat.beginShading();
        WBenchResourceManager.localShaderAssets.simple_flat.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(projection));
        WBenchResourceManager.localShaderAssets.simple_flat.performModel3DMatrix(new UniformString("model_matrix"), new Matrix4f().identity().translate(0.0f, -5.0f, 0.0f));
        WBenchResourceManager.localShaderAssets.simple_flat.performViewMatrix(new UniformString("view_matrix"), view);
        WBenchResourceManager.localShaderAssets.simple_flat.performUniform(new UniformString("color"), UniformFunctions.VEC4F(new Vector4f(0.35f, 0.35f, 0.65f, 0.5f)));
        WBenchResourceManager.localShaderAssets.simple_flat.performUniform(new UniformString("drawCenterRect"), UniformFunctions.FLOAT(-1.0f));
        JGemsHelper.render().renderModel3D(WBenchOpenGLRenderer.flatTerrain, MeshStructure3D.SOLID_LAYER, GL46.GL_TRIANGLES);
        WBenchResourceManager.localShaderAssets.simple_flat.endShading();
        GL46.glDisable(GL46.GL_BLEND);
     */
}
