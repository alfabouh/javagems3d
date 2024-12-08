package javagems3d.graphics.rendering.scene.renderer;

import javagems3d.graphics.rendering.scene.ISceneRenderer;
import javagems3d.graphics.rendering.scene.buffers.IndirectRenderBuffer;
import javagems3d.graphics.rendering.scene.renderer.nodes.IRenderNode;
import javagems3d.graphics.rendering.scene.renderer.nodes.Nodes;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.transformation.Transformation;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.map.IMapActionsCallback;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.Map;

public abstract class OpenGLRenderer implements ISceneRenderer, IResourceInit, IMapActionsCallback {
    private final IWindow window;
    private final Transformation transformation;
    private final SceneWorld sceneWorld;

    public OpenGLRenderer(IWindow window, SceneWorld sceneWorld, Transformation transformation) {
        this.window = window;
        this.transformation = transformation;
        this.sceneWorld = sceneWorld;
    }

    public final Vector2i getWindowSize() {
        return this.getWindow().getWindowSize();
    }

    public abstract IndirectRenderBuffer getSceneIndirectBuffer();
    public abstract Map<Nodes, IRenderNode> getConveyorNodes();

    @Override
    public @NotNull SceneWorld getSceneWorld() {
        return this.sceneWorld;
    }

    @Override
    public @NotNull Transformation getTransformationManager() {
        return this.transformation;
    }

    @Override
    public @NotNull IWindow getWindow() {
        return this.window;
    }
}
