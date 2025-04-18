package javagems3d.graphics.rendering.scene.renderer.nodes;

import javagems3d.JGems3D;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.interfaces.IUIRenderNode;
import javagems3d.graphics.rendering.ui.dear_imgui.DearUIRenderer;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.help.JGemsHelper;
import javagems3d.system.controller.base.MouseKeyboardController;
import javagems3d.system.controller.dispatcher.JGemsControllerDispatcher;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

public final class JGemsUIRenderNode implements IUIRenderNode {
    private final OpenGLRenderer openGLRenderer;
    private final JGemsUI ui;
    private final DearUIRenderer dearUIRenderer;
    private DearUIInterface anInterface;

    public JGemsUIRenderNode(DearUIRenderer dearUIRenderer, JGemsUI ui, OpenGLRenderer openGLRenderer) {
        this.openGLRenderer = openGLRenderer;
        this.dearUIRenderer = dearUIRenderer;
        this.ui = ui;
        this.anInterface = null;
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        GL46.glDisable(GL46.GL_DEPTH_TEST);
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glBlendFunc(GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
        this.ui.renderFrame(frameTicking.getFrameDeltaTime());
        GL46.glDisable(GL46.GL_BLEND);
        GL46.glEnable(GL46.GL_DEPTH_TEST);

        JGemsControllerDispatcher controllerDispatcher = JGemsHelper.controller().getControllerDispatcher();
        if (JGems3D.DEBUG_MODE && controllerDispatcher.getCurrentController() instanceof MouseKeyboardController) {
            this.dearUIRenderer.onRender((MouseKeyboardController) controllerDispatcher.getCurrentController(), this.getAnInterface(), frameTicking);
        }
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
