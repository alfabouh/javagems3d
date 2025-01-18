package javagems3d.graphics.rendering.scene.renderer.processors.predefined;

import javagems3d.JGemsHelper;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.indirect.IndirectObjectsRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformation;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.CubeMapTexture;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

import java.util.Collection;

public class DirectGeometryRenderProcessor extends IRenderProcessor.Template {
    private Collection<SceneObject> sceneObjects;

    public DirectGeometryRenderProcessor(@NotNull OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
    }

    @Override
    public void createResources() {
    }

    @Override
    public void destroyResources() {
    }

    @Override
    public void onRender(FrameTicking frameTicking) {

    }

    public void setDirectMeshObjects(@NotNull Collection<SceneObject> sceneObjects) {
        this.sceneObjects = sceneObjects;
    }
}