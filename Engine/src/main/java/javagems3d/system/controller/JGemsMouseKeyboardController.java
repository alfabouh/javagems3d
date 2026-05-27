package javagems3d.system.controller;

import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.controller.base.MouseKeyboardController;
import javagems3d.system.controller.base.ScanningMode;
import javagems3d.system.controller.binding.BindingManager;
import org.jetbrains.annotations.NotNull;

public class JGemsMouseKeyboardController extends MouseKeyboardController {
    public JGemsMouseKeyboardController(IWindow window, @NotNull BindingManager bindingManager) {
        super(window, bindingManager);
    }

    @Override
    public ScanningMode getScanningMode() {
        return ScanningMode.CENTER;
    }

    @Override
    public float getCamSensitivity() {
        return JGemsConfig.SYSTEM.CAM_SENS;
    }
}
