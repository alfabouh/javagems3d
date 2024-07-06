package ru.alfabouh.jgems3d.toolbox.render.scene.camera;

import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.world.camera.FreeCamera;
import ru.alfabouh.jgems3d.engine.system.controller.objects.IController;
import ru.alfabouh.jgems3d.toolbox.controller.TBoxControllerDispatcher;

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
