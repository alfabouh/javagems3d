package ru.alfabouh.jgems3d.engine.render.opengl.scene.world.camera;

import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.alfabouh.jgems3d.engine.system.controller.dispatcher.JGemsControllerDispatcher;
import ru.alfabouh.jgems3d.engine.system.controller.objects.IController;
import ru.alfabouh.jgems3d.proxy.logger.SystemLogging;
import ru.alfabouh.jgems3d.toolbox.controller.TBoxControllerDispatcher;

public class FreeCamera extends Camera {
    private IController controller;
    private float speed;

    public FreeCamera(IController controller, Vector3d pos, Vector3d rot) {
        super(pos, rot);
        SystemLogging.get().getLogManager().log("Created free camera at: " + pos);
        this.controller = controller;
        this.speed = this.camDefaultSpeed();
    }

    public void setCameraPos(Vector3d vector3d) {
        super.setCameraPos(vector3d);
    }

    public void setCameraRot(Vector3d vector3d) {
        super.setCameraRot(vector3d);
    }

    @Override
    public void updateCamera(double deltaTicks) {
        if (this.getController() != null) {
            this.moveCamera(this.moveCameraPosInput().mul(deltaTicks));
            this.moveCameraRot(this.moveCameraRotInput());
        }
        if (this.camRotation.x > 90) {
            this.camRotation.x = 90;
        }
        if (this.camRotation.x < -90) {
            this.camRotation.x = -90;
        }
    }

    protected Vector3d moveCameraPosInput() {
        Vector3d vector3d = JGemsControllerDispatcher.getNormalizedPositionInput(this.getController());
        if (vector3d.length() != 0.0f) {
            vector3d.normalize();
        }
        return vector3d;
    }

    protected Vector2d moveCameraRotInput() {
        return JGemsControllerDispatcher.getNormalizedRotationInput(this.getController());
    }

    public void addCameraPos(Vector3d vector3d) {
        super.setCameraPos(this.getCamPosition().add(vector3d));
    }

    public void addCameraRot(Vector3d vector3d) {
        super.setCameraRot(this.getCamRotation().add(vector3d));
    }

    public IController getController() {
        return this.controller;
    }

    public void setController(IController controller) {
        this.controller = controller;
    }

    protected void moveCameraRot(Vector2d xy) {
        this.addCameraRot(new Vector3d(xy, 0));
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float camSpeed() {
        return this.speed;
    }

    protected float camDefaultSpeed() {
        return 5.0f;
    }

    protected void moveCamera(Vector3d direction) {
        double[] motion = new double[3];
        double[] input = new double[3];
        input[0] = direction.x;
        input[1] = direction.y;
        input[2] = direction.z;
        if (input[2] != 0) {
            motion[0] += Math.sin(Math.toRadians(this.getCamRotation().y)) * -1.0f * input[2];
            motion[2] += Math.cos(Math.toRadians(this.getCamRotation().y)) * input[2];
        }
        if (input[0] != 0) {
            motion[0] += Math.sin(Math.toRadians(this.getCamRotation().y - 90)) * -1.0f * input[0];
            motion[2] += Math.cos(Math.toRadians(this.getCamRotation().y - 90)) * input[0];
        }
        if (input[1] != 0) {
            motion[1] += input[1];
        }
        this.addCameraPos(new Vector3d(motion[0], motion[1], motion[2]).mul(this.camSpeed()));
    }
}