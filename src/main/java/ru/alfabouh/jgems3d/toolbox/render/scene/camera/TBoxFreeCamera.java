package ru.alfabouh.jgems3d.toolbox.render.scene.camera;

import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.world.camera.FreeCamera;
import ru.alfabouh.jgems3d.engine.system.controller.objects.IController;
import ru.alfabouh.jgems3d.toolbox.controller.TBoxControllerDispatcher;

public class TBoxFreeCamera extends FreeCamera {
    public TBoxFreeCamera(IController controller, Vector3d pos, Vector3d rot) {
        super(controller, pos, rot);
    }

    protected Vector3d moveCameraPosInput() {
        Vector3d vector3d = TBoxControllerDispatcher.getNormalizedPositionInput(this.getController());
        if (vector3d.length() != 0.0f) {
            vector3d.normalize();
        }
        return vector3d;
    }

    protected Vector2d moveCameraRotInput() {
        return TBoxControllerDispatcher.getNormalizedRotationInput(this.getController());
    }

    protected float camSpeed() {
        return 15.0f;
    }
}
