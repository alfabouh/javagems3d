package javagems3d.physics.entities.kinematic.player;

import javagems3d.physics.entities.kinematic.JGemsKinematicItem;
import javagems3d.physics.entities.properties.controller.IControllable;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.thread.PhysicsThread;
import javagems3d.system.controller.objects.IController;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class JGemsKinematicControlledItem extends JGemsKinematicItem implements IControllable {
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
            this.inputMotion.clear();
            return;
        }
        this.getCameraRotation().add(new Vector3f(rotationInput, 0.0f));
        if (this.inputMotion.size() < PhysicsThread.TICKS_PER_SECOND) {
            this.inputMotion.addFirst(new Vector3f(xyzInput));
        }
        this.clampCameraRotation();
    }

    protected Vector3f getControllerMoveMotion() {
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

    @Override
    public void setRotation(Vector3f vector3d) {
        this.cameraRotation.set(vector3d);
    }

    @Override
    public Vector3f getRotation() {
        return this.getCameraRotation();
    }

    public Vector3f getCameraRotation() {
        return this.cameraRotation;
    }

    private void clampCameraRotation() {
        if (this.getRotation().x > Math.toRadians(90.0f)) {
            this.getCameraRotation().set(new Vector3d(Math.toRadians(90.0f), this.getRotation().y, this.getRotation().z));
        }
        if (this.getRotation().x < -Math.toRadians(90.0f)) {
            this.getCameraRotation().set(new Vector3d(-Math.toRadians(90.0f), this.getRotation().y, this.getRotation().z));
        }
    }
}
