package javagems3d.graphics.rendering.ui.dear_imgui.interfaces;

import javagems3d.system.controller.base.MouseKeyboardController;
import org.joml.Vector2i;

public interface DearUIInterface {
    void drawGui(Vector2i windowSize, MouseKeyboardController mouseKeyboardController);
}
