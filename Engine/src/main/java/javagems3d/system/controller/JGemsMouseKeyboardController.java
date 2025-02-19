/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.system.controller;

import javagems3d.JGemsHelper;
import javagems3d.global.JGemsGlobalConfiguration;
import javagems3d.graphics.camera.ControlledCamera;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.controller.base.IInventoryController;
import javagems3d.system.controller.base.MouseKeyboardController;
import javagems3d.system.controller.base.ScanningMode;
import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.inventory.IInventoryOwner;
import javagems3d.system.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class JGemsMouseKeyboardController extends MouseKeyboardController implements IInventoryController {
    public JGemsMouseKeyboardController(IWindow window, @NotNull BindingManager bindingManager) {
        super(window, bindingManager);
    }

    @Override
    public ScanningMode getScanningMode() {
        return ScanningMode.CENTER;
    }

    @Override
    public float getCamSensitivity() {
        return JGemsGlobalConfiguration.CAM_SENS;
    }

    public void updateItemWithInventory(IInventoryOwner hasInventory) {
        if (JGemsHelper.CAMERA.getCurrentCamera() instanceof ControlledCamera) {
            return;
        }
        Inventory inventory = hasInventory.getInventory();
        if (this.getMouseAndKeyboard().isLeftKeyPressed()) {
            inventory.onMouseLeftClick(hasInventory.getWorld());
        }
        if (this.getMouseAndKeyboard().isRightKeyPressed()) {
            inventory.onMouseRightClick(hasInventory.getWorld());
        }
        inventory.scrollInventoryToNotNullItem(this.getMouseAndKeyboard().getScrollVector());
    }
}
