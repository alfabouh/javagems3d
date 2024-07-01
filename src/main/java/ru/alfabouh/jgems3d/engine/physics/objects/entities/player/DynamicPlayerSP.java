package ru.alfabouh.jgems3d.engine.physics.objects.entities.player;

import org.bytedeco.bullet.BulletCollision.btBoxShape;
import org.bytedeco.bullet.BulletCollision.btBroadphaseProxy;
import org.bytedeco.bullet.BulletCollision.btCollisionWorld;
import org.bytedeco.bullet.BulletCollision.btConvexShape;
import org.bytedeco.bullet.LinearMath.btTransform;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.math.MathHelper;
import ru.alfabouh.jgems3d.engine.physics.collision.base.AbstractCollision;
import ru.alfabouh.jgems3d.engine.physics.collision.primitive.CapsuleShape;
import ru.alfabouh.jgems3d.engine.physics.objects.base.BodyGroup;
import ru.alfabouh.jgems3d.engine.physics.objects.base.PhysEntity;
import ru.alfabouh.jgems3d.engine.physics.objects.states.EntityState;
import ru.alfabouh.jgems3d.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.system.controller.dispatcher.JGemsControllerDispatcher;
import ru.alfabouh.jgems3d.engine.system.controller.objects.IController;

public class DynamicPlayerSP extends PhysEntity implements IPlayer {
    private final Vector3f cameraRotation;
    private final float speedMultiplier;
    private final float eyeHeight;
    protected boolean canJump;
    private IController controller;
    private Vector3f inputMotion;
    private boolean isOnGround;
    private int ticksBeforeCanJump;
    private float speed;

    public DynamicPlayerSP(World world, RigidBodyObject.PhysProperties properties, @NotNull Vector3f pos, @NotNull Vector3f rot) {
        super(world, "phys_playerSP", properties, new Vector3f(1.0f), pos, rot);
        this.inputMotion = new Vector3f(0.0f);
        this.cameraRotation = new Vector3f();
        this.eyeHeight = 0.425f;
        this.setSpeed(4.0f);
        this.speedMultiplier = 5.0f;
        this.canJump = false;
    }

    public void onUpdate(IWorld world) {
        super.onUpdate(world);
        if (this.isValid()) {
            if (this.isValidController()) {
                if (this.isOnGround()) {
                    this.stepCheck();
                    if (this.ticksBeforeCanJump-- > 0) {
                        this.ticksBeforeCanJump -= 1;
                    } else {
                        this.canJump = true;
                    }
                } else {
                    if (this.canJump) {
                        this.ticksBeforeCanJump = 50;
                    }
                    this.canJump = false;
                }
                this.getBulletObject().activateObject();
            }
            this.groundCheck();
            if (this.isValidController()) {
                final Vector3f motionC = this.calcControllerMotion();
                this.onStep(this.getStepVelocityVector(motionC));
                if (this.entityState().checkState(EntityState.StateType.IN_WATER)) {
                    if (motionC.y != 0.0d) {
                        this.swim(motionC.y > 0);
                    }
                } else {
                    if (motionC.y > 0) {
                        this.onJump();
                    }
                }
            }
        }
    }

    private void onStep(Vector3f Vector3f) {
        this.setVelocityVector(Vector3f);
    }

    private Vector3f getStepVelocityVector(Vector3f motion) {
        float speed = this.getObjectSpeed();
        Vector3f v1 = this.getMotionVector(motion).mul(this.getSpeed());
        if (speed > this.getSpeed() * this.getSpeedMultiplier()) {
            v1.div(speed);
        }
        if (!this.isOnGround() && !this.entityState().checkState(EntityState.StateType.IN_WATER)) {
            this.getBulletObject().setFrictionAxes(new Vector3f(0.0f));
            v1.mul(0.05f);
        } else {
            this.getBulletObject().setFrictionAxes(new Vector3f(1.0f));
        }
        return v1;
    }

    public double getSpeedMultiplier() {
        return this.speedMultiplier;
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    protected void stepCheck() {
        if (new Vector3f(this.getBulletObject().getObjectLinearVelocity()).mul(1, 0, 1).length() > 0.05d) {
            Vector3f vel = MathHelper.convert(this.getBulletObject().getLinearVelocity()).normalize();
            btVector3 v1 = new btVector3(this.getPosition().x + vel.x(), this.getPosition().y + 32, this.getPosition().z + vel.z());
            btVector3 v2 = new btVector3(this.getPosition().x + vel.x(), this.getPosition().y - 32, this.getPosition().z + vel.z());

            btCollisionWorld.ClosestRayResultCallback rayResultCallback = new btCollisionWorld.ClosestRayResultCallback(v1, v2);
            rayResultCallback.m_collisionFilterMask(btBroadphaseProxy.DefaultFilter & ~BodyGroup.PlayerFilter);
            this.getWorld().getDynamicsWorld().rayTest(v1, v2, rayResultCallback);

            if (rayResultCallback.hasHit()) {
                btVector3 va1 = new btVector3();
                btVector3 va2 = new btVector3();
                this.getBulletObject().getAabb(va1, va2);

                Vector3f hitPoint = MathHelper.convert(rayResultCallback.m_hitPointWorld());
                float dist = (float) (hitPoint.y - va1.y());
                btVector3 bv = new btVector3(0, 1, 0);
                float dot = (float) bv.dot(rayResultCallback.m_hitNormalWorld());
                float speed = 1.0f - (0.25f * (1.0f - dot));
                if (dist > 0.01d && dist < 0.55d) {
                    this.setPosition(new Vector3f(this.getPosition().x + vel.x() * 0.1f, this.getPosition().y + (1.0f - dist), this.getPosition().z + vel.z() * 0.1f));
                }
                bv.deallocate();
                va1.deallocate();
                va2.deallocate();
            }

            v1.deallocate();
            v2.deallocate();
        }
    }

    protected void groundCheck() {
        btVector3 v1 = new btVector3();
        btVector3 v2 = new btVector3();
        this.getBulletObject().getAabb(v1, v2);

        btTransform transform_m = this.getBulletObject().getWorldTransform();
        btTransform transform1 = new btTransform(transform_m);
        btTransform transform2 = new btTransform(transform_m);
        final double f1 = Math.min(v2.getY() - v1.getY(), 0.03f);

        btVector3 v3 = new btVector3(Math.abs(v2.getX() - v1.getX()) * 0.5f - 0.01f, f1, Math.abs(v2.getZ() - v1.getZ()) * 0.5f - 0.01f);

        transform1.setOrigin(new btVector3(this.getPosition().x, v1.getY() + f1 * 2, this.getPosition().z));
        transform2.setOrigin(new btVector3(this.getPosition().x, v1.getY(), this.getPosition().z));

        btConvexShape convexShape = new btBoxShape(v3);
        btCollisionWorld.ClosestConvexResultCallback closestConvexResultCallback = new btCollisionWorld.ClosestConvexResultCallback(transform1.getOrigin(), transform2.getOrigin());
        closestConvexResultCallback.m_collisionFilterMask(btBroadphaseProxy.DefaultFilter & ~BodyGroup.GhostFilter);
        this.getWorld().getDynamicsWorld().convexSweepTest(convexShape, transform1, transform2, closestConvexResultCallback);

        v1.deallocate();
        v2.deallocate();
        v3.deallocate();
        convexShape.deallocate();
        transform1.deallocate();
        transform2.deallocate();

        this.isOnGround = closestConvexResultCallback.hasHit();
        closestConvexResultCallback.deallocate();
    }

    public void swim(boolean up) {
        this.getBulletObject().setFrictionAxes(new Vector3f(0.0f));
        this.addObjectVelocity(new Vector3f(0.0f, up ? 0.5f : -0.3f, 0.0f));
    }

    public void onJump() {
        if (this.canJump) {
            this.getBulletObject().setFrictionAxes(new Vector3f(0.0f));
            this.addObjectVelocity(new Vector3f(0.0f, 8.0f, 0.0f));
            this.ticksBeforeCanJump = 20;
            this.canJump = false;
        }
    }

    public boolean isOnGround() {
        return this.isOnGround;
    }

    protected Vector3f getMotionVector(Vector3f Vector3f) {
        Vector3f Vector3f1 = new Vector3f(Vector3f).mul(new Vector3f(1, 0, 1));
        if (Vector3f1.length() > 0) {
            Vector3f1.normalize();
        }
        return Vector3f1;
    }

    private Vector3f calcControllerMotion() {
        float[] motion = new float[3];
        float[] input = new float[3];
        input[0] = this.inputMotion.x;
        input[1] = this.inputMotion.y;
        input[2] = this.inputMotion.z;
        if (input[2] != 0) {
            motion[0] += (float) (Math.sin(Math.toRadians(this.getRotation().y)) * -1.0f * input[2]);
            motion[2] += (float) (Math.cos(Math.toRadians(this.getRotation().y)) * input[2]);
        }
        if (input[0] != 0) {
            motion[0] += (float) (Math.sin(Math.toRadians(this.getRotation().y - 90)) * -1.0f * input[0]);
            motion[2] += (float) (Math.cos(Math.toRadians(this.getRotation().y - 90)) * input[0]);
        }
        if (input[1] != 0) {
            motion[1] += input[1];
        }
        return new Vector3f(motion[0], motion[1], motion[2]);
    }

    public Vector3f getCameraRotation() {
        return this.cameraRotation;
    }

    public boolean canBeDestroyed() {
        return false;
    }

    @Override
    protected AbstractCollision constructCollision() {
        return new CapsuleShape(1.0f);
    }

    public Vector3f getRotation() {
        return this.isValidController() ? this.getCameraRotation() : super.getRotation();
    }

    protected void afterRigidBodyCreated(RigidBodyObject rigidBodyObject) {
    }

    @Override
    public BodyGroup getBodyIndex() {
        return BodyGroup.PLAYER;
    }

    @Override
    public IController currentController() {
        return this.controller;
    }

    public void setController(IController iController) {
        this.controller = iController;
    }

    @Override
    public void performController(Vector2f rotationInput, Vector3f xyzInput, boolean isFocused) {
        if (JGemsControllerDispatcher.bindingManager().keyClear.isClicked()) {
            this.getWorld().clearAllItems();
        }
        this.getCameraRotation().add(new Vector3f(rotationInput, 0.0f));
        this.inputMotion = new Vector3f(xyzInput);
        this.clampCameraRotation();
    }

    public float getEyeHeight() {
        return this.eyeHeight;
    }

    private void clampCameraRotation() {
        if (this.getRotation().x > 90) {
            this.getCameraRotation().set(new Vector3f(90, this.getRotation().y, this.getRotation().z));
        }
        if (this.getRotation().x < -90) {
            this.getCameraRotation().set(new Vector3f(-90, this.getRotation().y, this.getRotation().z));
        }
    }
}
