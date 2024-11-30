package javagems3d.graphics.rendering.ui.dear_imgui.interfaces;

import javagems3d.system.controller.objects.MouseKeyboardController;
import org.joml.Vector2i;

public interface DIMInterface {
    void drawGui(Vector2i windowSize, MouseKeyboardController mouseKeyboardController);
}
