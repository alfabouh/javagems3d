package workbench.graphics.scene.nodes.templates;

import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;

public interface WIUIRenderNode extends IRenderNode {
    void setAnInterface(DearUIInterface dearUIInterface);
}
