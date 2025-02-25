package workbench.graphics.scene.nodes;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.geometry.DirectGeometryRenderProcessor;
import javagems3d.graphics.rendering.scene.renderer.processors.geometry.IndirectGeometryRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.manager.helper.JGemsShadersHelper;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.CubeMapTexture;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;
import workbench.graphics.scene.nodes.templates.ITransparencyRenderNode;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.function.Consumer;

public final class TransparencyRenderNode extends IRenderNode.Template implements ITransparencyRenderNode {

    public TransparencyRenderNode(@NotNull OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
    }

    @Override
    public FBOTexture2DProgram getOutColorBuffer() {
        return null;
    }

    @Override
    public FBOTexture2DProgram getInColorBuffer() {
        return null;
    }

    @Override
    public void setIndirectDeferredRenderingObjects(@NotNull Collection<SceneObject> indirectDeferredRenderingObjects) {

    }

    @Override
    public void setDirectDeferredRenderingObjects(@NotNull Collection<SceneObject> directDeferredRenderingObjects) {

    }

    @Override
    public Collection<SceneObject> getIndirectDeferredRenderingObjects() {
        return Collections.emptyList();
    }

    @Override
    public Collection<SceneObject> getDirectDeferredRenderingObjects() {
        return Collections.emptyList();
    }

    @Override
    public void onRender(FrameTicking frameTicking) {

    }

    @Override
    public void createResources() {

    }

    @Override
    public void destroyResources() {

    }
}
