package workbench.controller.objects;

import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.controller.base.ScanningMode;
import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.controller.base.MouseKeyboardController;
import org.joml.Vector2f;
import org.joml.Vector3f;
import toolbox.render.scene.dear_imgui.content.EditorContent;
import workbench.global.WBenchConstants;

public class WBenchMouseKeyboardController extends MouseKeyboardController {
    public WBenchMouseKeyboardController(IWindow window, BindingManager bindingManager) {
        super(window, bindingManager);
    }

    @Override
    public ScanningMode getScanningMode() {
        return ScanningMode.POS_RELATIVE;
    }

    @Override
    public float getCamSensitivity() {
        return WBenchConstants.CAM_SENS;
    }
}
