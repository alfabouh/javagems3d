package javagems3d.graphics.rendering.scene.renderer;

import javagems3d.JGemsHelper;
import javagems3d.graphics.rendering.scene.ISceneRenderer;
import javagems3d.graphics.rendering.programs.indirect.IndirectRenderBufferProgram;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.Nodes;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.map.IMapActionsCallback;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

import java.util.Map;

public abstract class OpenGLRenderer implements ISceneRenderer, IResourceInit, IMapActionsCallback {
    private final IWindow window;
    private final SceneWorld sceneWorld;

    public OpenGLRenderer(IWindow window, SceneWorld sceneWorld) {
        this.window = window;
        this.sceneWorld = sceneWorld;
    }

    public final Vector2i getWindowSize() {
        return this.getWindow().getWindowSize();
    }

    public abstract @NotNull Vector2i getRenderingResolution();

    public abstract IndirectRenderBufferProgram getSceneIndirectBuffer();
    public abstract Map<Nodes, IRenderNode> getConveyorNodes();

    @Override
    public @NotNull SceneWorld getSceneWorld() {
        return this.sceneWorld;
    }

    @Override
    public @NotNull IWindow getWindow() {
        return this.window;
    }

    public static void setViewPort(Vector2i resolution) {
        GL46.glViewport(0, 0, resolution.x, resolution.y);
    }

    public static void catchGLContextExceptions() {
        int errorCode;
        while ((errorCode = GL46.glGetError()) != GL46.GL_NO_ERROR) {
            String error;
            switch (errorCode) {
                case GL46.GL_INVALID_ENUM:
                    error = "INVALID_ENUM";
                    break;
                case GL46.GL_INVALID_VALUE:
                    error = "INVALID_VALUE";
                    break;
                case GL46.GL_INVALID_OPERATION:
                    error = "INVALID_OPERATION";
                    break;
                case GL46.GL_STACK_OVERFLOW:
                    error = "STACK_OVERFLOW";
                    break;
                case GL46.GL_STACK_UNDERFLOW:
                    error = "STACK_UNDERFLOW";
                    break;
                case GL46.GL_OUT_OF_MEMORY:
                    error = "OUT_OF_MEMORY";
                    break;
                case GL46.GL_INVALID_FRAMEBUFFER_OPERATION:
                    error = "INVALID_FRAMEBUFFER_OPERATION";
                    break;
                default:
                    error = "UNKNOWN";
                    break;
            }
            JGemsHelper.getLogger().error("GL ERROR: " + error);
        }
    }
}
