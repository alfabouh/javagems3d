package javagems3d.graphics.rendering.scene.renderer;

import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.rendering.scene.ISceneRenderer;
import javagems3d.graphics.rendering.programs.indirect.base.IndirectBufferProgram;
import javagems3d.graphics.rendering.scene.culling.ISceneCulling;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.NodeID;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.physics.world.IWorld;
import javagems3d.system.resources.managing.resources.data.cache.MeshBuffersDataCache;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

import java.util.Map;

public abstract class OpenGLRenderer implements ISceneRenderer, IResourceInit{
    private final IWindow window;
    private final IWorld world;

    public OpenGLRenderer(@NotNull IWindow window, @NotNull IWorld world) {
        this.window = window;
        this.world = world;
    }

    public final Vector2i getWindowSize() {
        return this.getWindow().getWindowSize();
    }

    public abstract @NotNull Vector2i getRenderingResolution();

    public abstract IndirectBufferProgram getSceneIndirectBuffer();
    public abstract Map<NodeID, IRenderNode> getConveyorNodes();
    public abstract ISceneCulling getSceneCulling();

    public abstract void initSceneIndirectRenderBuffer(MeshBuffersDataCache meshBuffersDataCache);
    public abstract void destroySceneIndirectRenderBuffer();
    public abstract ICamera getCamera();

    @Override
    public @NotNull IWorld getWorld() {
        return this.world;
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
            Log.get().error("GL ERROR: " + error);
        }
    }
}
