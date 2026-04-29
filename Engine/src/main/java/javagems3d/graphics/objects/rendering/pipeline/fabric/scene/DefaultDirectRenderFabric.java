package javagems3d.graphics.objects.rendering.pipeline.fabric.scene;

import javagems3d.graphics.objects.IModeled;
import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.objects.rendering.pipeline.fabric.DirectRenderFabric;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;

import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.help.JGemsHelper;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.collections.Pair;
import org.joml.Matrix4f;

import java.util.function.Consumer;

public class DefaultDirectRenderFabric extends DirectRenderFabric {
    private final boolean transparency;

    public DefaultDirectRenderFabric(Stage stage, boolean transparency) {
        super(stage);
        this.transparency = transparency;
    }

    @Override
    public void onPreRender(Pipeline pipeline, JGemsShaderManager shaderManager, OpenGLRenderer openGLRenderer, IRendered renderedItem, ArbitraryArguments metaData) {

    }

    @Override
    public void onRender(Pipeline pipeline, JGemsShaderManager shaderManager, OpenGLRenderer openGLRenderer, IRendered renderedItem, ArbitraryArguments metaData) {
        if (renderedItem instanceof IModeled modeled) {
            if (renderedItem.canBeRendered()) {
                Model3D model = modeled.getModel();
                Consumer<Pair<JGemsShaderManager, IRendered>> functionToHandleUniforms = metaData.getterFunc().getObject(0);
                if (functionToHandleUniforms != null) {
                    functionToHandleUniforms.accept(new Pair<>(shaderManager, renderedItem));
                }
                final Matrix4f viewMatrix = pipeline.equals(Pipeline.BACKGROUND) ? TransformUtils.getViewMatrix(openGLRenderer.getWorld().getEnvironment().getSkyBox().getBackground().getScaledCameraBackground()) : JGemsTransformManager.INSTANCE.getCameraViewMatrix();
                shaderManager.performMatrix4(new UniformString(DefaultUniformDefinitions.PROJECTION_MATRIX), JGemsTransformManager.INSTANCE.getPerspectiveMatrix());
                shaderManager.performModel3DMatrix(new UniformString(DefaultUniformDefinitions.MODEL_MATRIX), model);
                shaderManager.performMatrix4(new UniformString(DefaultUniformDefinitions.VIEW_MATRIX), viewMatrix);
                this.renderMeshList3D(openGLRenderer, shaderManager, model, this.transparency ? MeshStructure3D.TRANSPARENCY_LAYER : MeshStructure3D.SOLID_LAYER,
                        renderedItem.getRenderAttributes().getProperties().has(JGemsRenderProperties.KEY_ALPHA_DISCARD) ?
                        (float) renderedItem.getRenderAttributes().getProperties().getFloat(JGemsRenderProperties.KEY_ALPHA_DISCARD) :
                        JGemsConfig.SYSTEM.MAX_ALPHA_TO_DISCARD_SHADOW_FRAGMENT);
            }
        }
    }

    public void renderMeshList3D(OpenGLRenderer openGLRenderer, JGemsShaderManager shaderManager, Model3D model3D, int layer, float alpha) {
        for (MeshNode3D<RenderMesh> meshNode3D : model3D.<MeshStructure3D<RenderMesh>>getMeshStructureCast().getNodes(layer)) {
            JGemsHelper.render().performDefaultModelMaterialOnShader(openGLRenderer.getWorld().getEnvironment(), shaderManager, meshNode3D.getMaterial(), alpha);
            JGemsHelper.render().renderMeshNode(meshNode3D.getMeshData());
        }
    }

    @Override
    public void onPostRender(Pipeline pipeline, JGemsShaderManager shaderManager, OpenGLRenderer openGLRenderer, IRendered renderedItem, ArbitraryArguments metaData) {

    }

    @Override
    public void createResources(IRendered renderedItem) {

    }

    @Override
    public void destroyResources(IRendered renderedItem) {

    }
}
