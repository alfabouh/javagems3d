package workbench.controller.objects;

import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.controller.base.ScanningMode;
import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.controller.base.MouseKeyboardController;
import workbench.WBench;
import workbench.graphics.scene.ui.map.MapEditorInterface;

public class WBenchMouseKeyboardController extends MouseKeyboardController {
    public WBenchMouseKeyboardController(IWindow window, BindingManager bindingManager) {
        super(window, bindingManager);
    }

    @Override
    public boolean disableMouseScanning() {
        return !MapEditorInterface.isCursorInsideScene;
    }

    @Override
    public ScanningMode getScanningMode() {
        return ScanningMode.POS_RELATIVE_RMK;
    }

    @Override
    public float getCamSensitivity() {
        return WBench.get().getSettings().getCamSpeed();
    }
}
