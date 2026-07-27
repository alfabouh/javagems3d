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
