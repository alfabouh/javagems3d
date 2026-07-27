/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

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
        return ProjectUIUtils.ctrlS() || !MapEditorInterface.isCursorInsideSceneAndFocused && !GameEditorInterface.isCursorInsideScene;
    }

    @Override
    public boolean disableKeyboardScanning() {
        return ProjectUIUtils.ctrlSPress();
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
        return WBench.get().getSettings().getCamSens();
    }
}
