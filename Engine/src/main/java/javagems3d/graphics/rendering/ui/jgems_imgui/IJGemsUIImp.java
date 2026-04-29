package javagems3d.graphics.rendering.ui.jgems_imgui;

import javagems3d.graphics.rendering.ui.jgems_imgui.panels.base.PanelUI;
import org.jetbrains.annotations.Nullable;

public interface IJGemsUIImp {
    void openUIPanel(@Nullable PanelUI panelUI);
    JGemsUI getJGemsUI();
}
