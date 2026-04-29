package javagems3d.graphics.rendering.ui.dear_imgui;

import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import org.jetbrains.annotations.Nullable;

public interface IDearUIImp {
    void openUIInterface(@Nullable DearUIInterface dearUIInterface);
    DearUIRenderer getDearUIRenderer();
}
