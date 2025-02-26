package javagems3d.help;

import javagems3d.JGems3D;
import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;

public abstract class JGemsUIHelper {
    public static void closeUIPanel() {
        JGems3D.get().closeUIPanel();
    }

    public static void openUIPanel(PanelUI ui) {
        JGems3D.get().openUIPanel(ui);
    }
}
