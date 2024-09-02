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

package javagems3d.system.controller.objects;

import org.joml.Vector2f;
import org.joml.Vector3f;
import javagems3d.graphics.opengl.screen.window.IWindow;
import javagems3d.system.controller.binding.BindingManager;
import javagems3d.system.inventory.IInventoryOwner;

public interface IController {
    Vector2f getNormalizedRotationInput();

    Vector3f getNormalizedPositionInput();

    BindingManager getBindingManager();

    void updateControllerState(IWindow window);

    void updateItemWithInventory(IInventoryOwner hasInventory);
}
