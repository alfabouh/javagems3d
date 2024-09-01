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

package toolbox.render.scene.camera;

import org.joml.Vector2f;
import org.joml.Vector3f;
import javagems3d.engine.graphics.opengl.camera.FreeCamera;
import javagems3d.engine.system.controller.objects.IController;
import toolbox.controller.TBoxControllerDispatcher;

public class TBoxFreeCamera extends FreeCamera {
    public TBoxFreeCamera(IController controller, Vector3f pos, Vector3f rot) {
        super(controller, pos, rot);
    }

    protected Vector3f moveCameraPosInput() {
        Vector3f vector3f = TBoxControllerDispatcher.getNormalizedPositionInput(this.getController());
        if (vector3f.length() != 0.0f) {
            vector3f.normalize();
        }
        return vector3f;
    }

    protected Vector2f moveCameraRotInput() {
        return TBoxControllerDispatcher.getNormalizedRotationInput(this.getController());
    }

    protected float camDefaultSpeed() {
        return 15.0f;
    }
}
