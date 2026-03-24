package api.scripting.coding.env.internal.util.ui;

import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.graphics.rendering.ui.jgems_imgui.JGemsUI;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import javagems3d.graphics.screen.window.IWindow;
import org.jetbrains.annotations.Nullable;

@JSHideFromDoc
public class JSUIPanelWrapper implements PanelUI {
    private @Nullable Runnable onConstruct;
    private @Nullable Runnable onDestruct;
    private @Nullable Runnable drawPanel;
    private @Nullable Runnable onWindowResize;

    public JSUIPanelWrapper setOnConstruct(@Nullable Runnable onConstruct) {
        this.onConstruct = onConstruct;
        return this;
    }

    public JSUIPanelWrapper setOnDestruct(@Nullable Runnable onDestruct) {
        this.onDestruct = onDestruct;
        return this;
    }

    public JSUIPanelWrapper setDrawPanel(@Nullable Runnable drawPanel) {
        this.drawPanel = drawPanel;
        return this;
    }

    public JSUIPanelWrapper setOnWindowResize(@Nullable Runnable onWindowResize) {
        this.onWindowResize = onWindowResize;
        return this;
    }

    @Override
    public void onConstruct(JGemsUI ui) {
        if (this.onConstruct != null) {
            this.onConstruct.run();
        }
    }

    @Override
    public void onDestruct(JGemsUI ui) {
        if (this.onDestruct != null) {
            this.onDestruct.run();
        }
    }

    @Override
    public void drawPanel(JGemsUI ui, float frameDeltaTicks) {
        if (this.drawPanel != null) {
            this.drawPanel.run();
        }
    }

    @Override
    public void onWindowResize(IWindow window) {
        if (this.onWindowResize != null) {
            this.onWindowResize.run();
        }
    }
}
