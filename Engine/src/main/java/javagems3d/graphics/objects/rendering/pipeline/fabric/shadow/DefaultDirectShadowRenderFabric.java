package javagems3d.graphics.objects.rendering.pipeline.fabric.shadow;

import javagems3d.global.JGemsRenderingGlobalConstants;
import javagems3d.graphics.objects.IAnimated;
import javagems3d.graphics.objects.IModeled;
import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.objects.rendering.pipeline.fabric.DirectRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.scene.DefaultDirectRenderFabric;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.mesh.structures.MeshGroup;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.RGBAColor;
import javagems3d.system.resources.assets.texturing.base.ImageBasedTexture;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
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
                Model<Format3D> model = modeled.getModel();
                shaderManager.getUtils().performModel3DMatrix(model);
                this.renderModelForShadow(modeled, shaderManager, model);
            }
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
}
