package workbench.controller.objects;

import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.controller.base.ScanningMode;
import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.controller.base.MouseKeyboardController;
import workbench.global.WBenchConstants;
import workbench.graphics.scene.ui.EditorInterface;

public class WBenchMouseKeyboardController extends MouseKeyboardController {
    public WBenchMouseKeyboardController(IWindow window, BindingManager bindingManager) {
        super(window, bindingManager);
    }

    @Override
    public boolean disableMouseScanning() {
        return !EditorInterface.isCursorInsideScene;
    }

    @Override
    public ScanningMode getScanningMode() {
        return ScanningMode.POS_RELATIVE_RMK;
    }

    @Override
    public float getCamSensitivity() {
        return WBenchConstants.CAM_SENS;
    }
}
