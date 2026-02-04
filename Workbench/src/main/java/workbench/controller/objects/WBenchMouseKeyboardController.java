package workbench.controller.objects;

import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.controller.base.ScanningMode;
import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.controller.base.MouseKeyboardController;
import workbench.WBench;
import workbench.graphics.scene.ui.ProjectUIUtils;
import workbench.graphics.scene.ui.game.GameEditorInterface;
import workbench.graphics.scene.ui.map.MapEditorInterface;

public class WBenchMouseKeyboardController extends MouseKeyboardController {
    public WBenchMouseKeyboardController(IWindow window, BindingManager bindingManager) {
        super(window, bindingManager);
    }

    public static boolean blockKeyboardMouseCamTransformInput() {
        return ProjectUIUtils.ctrlS() || !MapEditorInterface.isCursorInsideScene && !GameEditorInterface.isCursorInsideScene;
    }

    @Override
    public boolean disableKeyboardScanning() {
        return WBenchMouseKeyboardController.blockKeyboardMouseCamTransformInput();
    }

    @Override
    public boolean disableMouseScanning() {
        return WBenchMouseKeyboardController.blockKeyboardMouseCamTransformInput();
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
