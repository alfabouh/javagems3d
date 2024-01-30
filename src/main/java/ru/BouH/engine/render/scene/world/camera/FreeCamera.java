package ru.BouH.engine.render.scene.world.camera;

import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.ControllerDispatcher;
import ru.BouH.engine.game.controller.IController;

public class FreeCamera extends Camera {
    public static final double CAM_SPEED = 0.25d;
    private IController controller;

    public FreeCamera(IController controller, Vector3d pos, Vector3d rot) {
        super(pos, rot);
        Game.getGame().getLogManager().log("Created free camera at: " + pos);
        this.controller = controller;
    }

    public void setCameraPos(Vector3d vector3d) {
        super.setCameraPos(vector3d);
    }

    public void setCameraRot(Vector3d vector3d) {
        super.setCameraRot(vector3d);
    }

    @Override
    public void updateCamera(double partialTicks) {
        if (this.getController() != null) {
            this.moveCamera(ControllerDispatcher.getOptionedXYZVec(this.getController()));
            this.moveCameraRot(ControllerDispatcher.getOptionedDisplayVec(this.getController()));
        }
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

    private void moveCameraRot(Vector2d xy) {
        this.addCameraRot(new Vector3d(xy, 0));
    }

    private void moveCamera(Vector3d direction) {
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
        this.addCameraPos(new Vector3d(motion[0], motion[1], motion[2]).mul(FreeCamera.CAM_SPEED));
    }
}
