package javagems3d.system.controller;

import javagems3d.help.JGemsHelper;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.camera.ControlledCamera;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.controller.base.IInventoryController;
import javagems3d.system.controller.base.MouseKeyboardController;
import javagems3d.system.controller.base.ScanningMode;
import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.inventory.InventoryBase;
import javagems3d.system.inventory.InventoryOwner;
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
        return JGemsConfig.SYSTEM.CAM_SENS;
    }

    public void updateItemWithInventory(InventoryOwner hasInventory) {
        if (JGemsHelper.camera().getCurrentCamera() instanceof ControlledCamera) {
            return;
        }
        InventoryBase inventoryBase = hasInventory.getInventory();
        if (this.getMouseAndKeyboard().isLeftKeyPressed()) {
            inventoryBase.onMouseLeftClick(hasInventory.getWorld());
        }
        if (this.getMouseAndKeyboard().isRightKeyPressed()) {
            inventoryBase.onMouseRightClick(hasInventory.getWorld());
        }
        inventoryBase.scrollInventoryToNotNullItem(this.getMouseAndKeyboard().getScrollVector());
    }
}
