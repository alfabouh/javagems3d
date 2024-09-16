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

package javagems3d.physics.entities.player;

import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.thread.PhysicsThread;
import javagems3d.system.controller.objects.IController;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import javagems3d.physics.entities.properties.controller.IControllable;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.WorldItem;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class Player extends WorldItem implements IControllable {
    private final Vector3f cameraRotation;
    private final Deque<Vector3f> inputMotion;
    private IController controller;

    public Player(PhysicsWorld world, Vector3f pos, Vector3f rot, String itemName) {
        super(world, pos, rot, itemName);
        this.cameraRotation = new Vector3f(rot);
        this.inputMotion = new ArrayDeque<>();
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        this.createPlayer((PhysicsWorld) iWorld);
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        super.onDestroy(iWorld);
    }

    public void setController(IController iController) {
        this.controller = iController;
    }

    @Override
    public void performController(Vector2f rotationInput, Vector3f xyzInput, boolean isFocused) {
        if (!isFocused) {
            this.inputMotion.clear();
            return;
        }
        this.getCameraRotation().add(new Vector3f(rotationInput, 0.0f));
        if (this.inputMotion.size() < PhysicsThread.TICKS_PER_SECOND * 10) {
            this.inputMotion.addFirst(new Vector3f(xyzInput));
        }
        this.clampCameraRotation();
    }

    @Override
    public void setRotation(Vector3f vector3d) {
        this.cameraRotation.set(vector3d);
    }

    protected Vector3f calcControllerMotion() {
        if (this.inputMotion.isEmpty()) {
            return new Vector3f(0.0f);
        }
        float[] motion = new float[3];
        float[] input = new float[3];
        Vector3f inputMotion = this.inputMotion.pop();
        input[0] = inputMotion.x;
        input[1] = inputMotion.y;
        input[2] = inputMotion.z;
        if (input[2] != 0) {
            motion[0] += (float) Math.sin(this.getRotation().y) * -1.0f * input[2];
            motion[2] += (float) Math.cos(this.getRotation().y) * input[2];
        }
        if (input[0] != 0) {
            motion[0] += (float) Math.sin(this.getRotation().y - (Math.PI / 2.0f)) * -1.0f * input[0];
            motion[2] += (float) Math.cos(this.getRotation().y - (Math.PI / 2.0f)) * input[0];
        }
        if (input[1] != 0) {
            motion[1] += input[1];
        }
        return new Vector3f(motion[0], motion[1], motion[2]);
    }

    private void clampCameraRotation() {
        if (this.getRotation().x > Math.toRadians(90.0f)) {
            this.getCameraRotation().set(new Vector3d(Math.toRadians(90.0f), this.getRotation().y, this.getRotation().z));
        }
        if (this.getRotation().x < -Math.toRadians(90.0f)) {
            this.getCameraRotation().set(new Vector3d(-Math.toRadians(90.0f), this.getRotation().y, this.getRotation().z));
        }
    }

    @Override
    public Vector3f getRotation() {
        return this.getCameraRotation();
    }

    @Override
    public IController currentController() {
        return this.controller;
    }

    public Vector3f getCameraRotation() {
        return this.cameraRotation;
    }

    protected abstract void createPlayer(PhysicsWorld world);
    public abstract float getScalarSpeed();
    public abstract float height();
}
