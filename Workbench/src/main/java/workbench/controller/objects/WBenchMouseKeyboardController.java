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
