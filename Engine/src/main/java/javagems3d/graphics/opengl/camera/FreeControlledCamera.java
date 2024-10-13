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

package javagems3d.graphics.opengl.camera;

import org.joml.Vector2f;
import org.joml.Vector3f;
import javagems3d.JGemsHelper;
import javagems3d.system.controller.dispatcher.JGemsControllerDispatcher;
import javagems3d.system.controller.objects.IController;

public class FreeControlledCamera extends FreeCamera {
    private IController controller;
    private float speed;

    public FreeControlledCamera(IController controller, Vector3f pos, Vector3f rot) {
        super(pos, rot);
        this.controller = controller;
        this.speed = this.camDefaultSpeed();
    }

    public void setCameraPosition(Vector3f vector3f) {
        super.setCameraPosition(vector3f);
    }

    public void setCameraRotation(Vector3f vector3f) {
        super.setCameraRotation(vector3f);
    }

    @Override
    public void updateCamera(float frameDeltaTime) {
        if (this.getController() != null) {
            this.moveCamera(this.moveCameraPosInput().mul(frameDeltaTime));
            this.moveCameraRot(this.moveCameraRotInput());
        }
        super.updateCamera(frameDeltaTime);
    }

    protected Vector3f moveCameraPosInput() {
        Vector3f vector3f = JGemsControllerDispatcher.getNormalizedPositionInput(this.getController());
        if (vector3f.length() != 0.0f) {
            vector3f.normalize();
        }
        return vector3f;
    }

    protected Vector2f moveCameraRotInput() {
        return JGemsControllerDispatcher.getNormalizedRotationInput(this.getController());
    }

    public IController getController() {
        return this.controller;
    }

    public void setController(IController controller) {
        this.controller = controller;
    }

    protected void moveCameraRot(Vector2f xy) {
        this.addCameraRot(new Vector3f(xy, 0));
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float camSpeed() {
        return this.speed;
    }

    protected float camDefaultSpeed() {
        return 10.0f;
    }

    protected void moveCamera(Vector3f direction) {
        float[] motion = new float[3];
        float[] input = new float[3];
        input[0] = direction.x;
        input[1] = direction.y;
        input[2] = direction.z;
        if (input[2] != 0) {
            motion[0] += (float) (Math.sin(this.getCamRotation().y) * -1.0f * input[2]);
            motion[2] += (float) (Math.cos(this.getCamRotation().y) * input[2]);
        }
        if (input[0] != 0) {
            motion[0] += (float) (Math.sin(this.getCamRotation().y - (Math.PI / 2.0f)) * -1.0f * input[0]);
            motion[2] += (float) (Math.cos(this.getCamRotation().y - (Math.PI / 2.0f)) * input[0]);
        }
        if (input[1] != 0) {
            motion[1] += input[1];
        }
        this.addCameraPos(new Vector3f(motion[0], motion[1], motion[2]).mul(this.camSpeed()));
    }
}
