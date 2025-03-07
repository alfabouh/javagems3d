package workbench.graphics.scene.nodes;

import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.ui.dear_imgui.DearUIRenderer;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.controller.base.MouseKeyboardController;
import org.jetbrains.annotations.NotNull;
import workbench.WBench;
import workbench.graphics.scene.nodes.templates.WIUIRenderNode;

public final class WUIRenderNode implements WIUIRenderNode {
    private final OpenGLRenderer openGLRenderer;
    private final DearUIRenderer dearUIRenderer;
    private DearUIInterface anInterface;

    public WUIRenderNode(DearUIRenderer dearUIRenderer, OpenGLRenderer openGLRenderer) {
        this.openGLRenderer = openGLRenderer;
        this.dearUIRenderer = dearUIRenderer;
        this.anInterface = null;
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        MouseKeyboardController mouseKeyboardController = WBench.get().getControllerDispatcher().getCurrentController();
        this.dearUIRenderer.onRender(mouseKeyboardController, this.getAnInterface(), frameTicking);
    }

    public void setAnInterface(DearUIInterface anInterface) {
        this.anInterface = anInterface;
    }

    public DearUIInterface getAnInterface() {
        return this.anInterface;
    }

    @Override
    public @NotNull OpenGLRenderer getOpenGLRenderer() {
        return this.openGLRenderer;
    }

    @Override
    public void createResources() {
    }

    @Override
    public void destroyResources() {
    }
}
