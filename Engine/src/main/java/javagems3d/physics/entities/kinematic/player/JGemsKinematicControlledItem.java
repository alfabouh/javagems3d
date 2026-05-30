package javagems3d.physics.entities.kinematic.player;

import javagems3d.physics.entities.kinematic.JGemsKinematicItem;
import javagems3d.physics.entities.properties.controller.IControllable;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.thread.JGemsPhysics;
import javagems3d.system.controller.base.IController;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class JGemsKinematicControlledItem extends JGemsKinematicItem implements IControllable {
    private final Object cameraLock = new Object();
    private final Object inputLock = new Object();

    private IController controller;
    private final Vector3f cameraRotation;
    private final Deque<Vector3f> inputMotion;

    public JGemsKinematicControlledItem(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scaling, String itemName) {
        super(world, pos, rot, scaling, itemName);
        this.cameraRotation = new Vector3f(rot);
        this.inputMotion = new ArrayDeque<>();
    }

    public JGemsKinematicControlledItem(PhysicsWorld world, Vector3f pos, Vector3f rot, String itemName) {
        this(world, pos, rot, new Vector3f(1.0f), itemName);
    }

    public JGemsKinematicControlledItem(PhysicsWorld world, Vector3f pos, String itemName) {
        this(world, pos, new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    public JGemsKinematicControlledItem(PhysicsWorld world, String itemName) {
        this(world, new Vector3f(0.0f), new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    @Override
    public IController getCurrentController() {
        return this.controller;
    }

    @Override
    public void setController(IController iController) {
        this.controller = iController;
    }

    @Override
    public void performController(Vector2f rotationInput, Vector3f xyzInput, boolean isFocused) {
        if (!isFocused) {
            synchronized (this.inputLock) {
                this.inputMotion.clear();
            }
            return;
        }

        synchronized (this.cameraLock) {
            this.cameraRotation.add(new Vector3f(rotationInput, 0.0f));
            this.clampCameraRotation();
        }

        synchronized (this.inputLock) {
            if (this.inputMotion.size() < JGemsPhysics.TICKS_PER_SECOND) {
                this.inputMotion.addFirst(new Vector3f(xyzInput));
            }
        }
    }

    protected Vector3f getControllerMoveMotion() {
        Vector3f inputMotion;

        synchronized (this.inputLock) {
            if (this.inputMotion.isEmpty()) {
                return new Vector3f(0.0f);
            }
            inputMotion = this.inputMotion.pop();
        }

        float[] motion = new float[3];
        float[] input = new float[3];

        input[0] = inputMotion.x;
        input[1] = inputMotion.y;
        input[2] = inputMotion.z;

        float rotY = this.getRotation().y;

        if (input[2] != 0) {
            motion[0] += (float) Math.sin(rotY) * -1.0f * input[2];
            motion[2] += (float) Math.cos(rotY) * input[2];
        }

        if (input[0] != 0) {
            motion[0] += (float) Math.sin(rotY - (Math.PI / 2.0f)) * -1.0f * input[0];
            motion[2] += (float) Math.cos(rotY - (Math.PI / 2.0f)) * input[0];
        }

        if (input[1] != 0) {
            motion[1] += input[1];
        }

        return new Vector3f(motion[0], motion[1], motion[2]);
    }

    @Override
    public void setRotation(Vector3f vector3f) {
        synchronized (this.cameraLock) {
            this.cameraRotation.set(vector3f);
        }
    }

    @Override
    public Vector3f getRotation() {
        synchronized (this.cameraLock) {
            return new Vector3f(this.cameraRotation);
        }
    }

    public Vector3f getCameraRotation() {
        synchronized (this.cameraLock) {
            return new Vector3f(this.cameraRotation);
        }
    }

    private void clampCameraRotation() {
        synchronized (this.cameraLock) {
            float x = this.cameraRotation.x;
            float max = (float) Math.toRadians(90.0f);
            if (x > max) {
                this.cameraRotation.x = max;
            }
            if (x < -max) {
                this.cameraRotation.x = -max;
            }
        }
    }
}