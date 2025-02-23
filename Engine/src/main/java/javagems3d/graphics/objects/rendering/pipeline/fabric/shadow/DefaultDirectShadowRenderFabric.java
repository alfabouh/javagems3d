package javagems3d.graphics.objects.rendering.pipeline.fabric.shadow;

import javagems3d.global.JGemsRenderingGlobalConstants;
import javagems3d.graphics.objects.IAnimated;
import javagems3d.graphics.objects.IModeled;
import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.objects.rendering.pipeline.fabric.scene.DefaultDirectRenderFabric;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;

import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.base.ImageBasedTexture;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import logger.Log;
import org.lwjgl.opengl.GL46;

public class DefaultDirectShadowRenderFabric extends DefaultDirectRenderFabric {
    public DefaultDirectShadowRenderFabric() {
        super(Stage.SHADOW_DIRECT);
    }

    @Override
    public void onRender(Pipeline pipeline, JGemsShaderManager shaderManager, OpenGLRenderer openGLRenderer, IRendered renderedItem, ArbitraryArguments metaData) {
        if (renderedItem instanceof IModeled) {
            IModeled modeled = (IModeled) renderedItem;
            if (renderedItem.canBeRendered()) {
                Model3D model = modeled.getModel();
                shaderManager.getUtils().performModel3DMatrix(model);
                this.renderModelForShadow(modeled, shaderManager, model);
            }
        }
    }

    protected void renderModelForShadow(IAnimated animated, JGemsShaderManager shaderManager, Model3D model) {
        shaderManager.performUniform(new UniformString("alpha_discard"), UniformFunctions.FLOAT(JGemsRenderingGlobalConstants.MAX_ALPHA_TO_DISCARD_SHADOW_FRAGMENT));
        shaderManager.getUtils().performAnimationsInfo(animated);
        try {
            for (MeshNode3D<RenderMesh> meshNode3D : model.<MeshGroup>getMeshStructureCast().getAllNodes()) {
                if (meshNode3D.getMaterial().getDiffuse() instanceof ImageBasedTexture) {
                    ImageBasedTexture imageBasedTexture = (ImageBasedTexture) meshNode3D.getMaterial().getDiffuse();
                    shaderManager.performUniformTexture(new UniformString("texture_sampler"), imageBasedTexture);
                    shaderManager.performUniform(new UniformString("use_texture"), UniformFunctions.BOOLEAN(true));
                } else {
                    shaderManager.performUniform(new UniformString("use_texture"), UniformFunctions.BOOLEAN(false));
                }
                GL46.glBindVertexArray(meshNode3D.getMeshData().getVao());
                meshNode3D.getMeshData().enableAllMeshAttributes();
                GL46.glDrawElements(GL46.GL_TRIANGLES, meshNode3D.getMeshData().getTotalVertices(), GL46.GL_UNSIGNED_INT, 0);
                meshNode3D.getMeshData().disableAllMeshAttributes();
                GL46.glBindVertexArray(0);
            }
        } catch (Exception e) {
            Log.get().exception(e);
            throw new JGemsRuntimeException("There was an error, while rendering model for shadows. ");
        }
    }
}
