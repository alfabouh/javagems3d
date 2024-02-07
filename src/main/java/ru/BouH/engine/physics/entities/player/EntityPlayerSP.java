package ru.BouH.engine.physics.entities.player;

import org.bytedeco.bullet.BulletCollision.btBoxShape;
import org.bytedeco.bullet.BulletCollision.btBroadphaseProxy;
import org.bytedeco.bullet.BulletCollision.btCollisionWorld;
import org.bytedeco.bullet.BulletCollision.btConvexShape;
import org.bytedeco.bullet.LinearMath.btTransform;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.IController;
import ru.BouH.engine.game.controller.binding.BindingList;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.physics.collision.AbstractCollision;
import ru.BouH.engine.physics.collision.OBB;
import ru.BouH.engine.physics.entities.BodyGroup;
import ru.BouH.engine.physics.entities.IRemoteController;
import ru.BouH.engine.physics.entities.Materials;
import ru.BouH.engine.physics.entities.PhysEntity;
import ru.BouH.engine.physics.entities.prop.PhysEntityCube;
import ru.BouH.engine.physics.entities.prop.PhysLightCube;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.proxy.IWorld;
import ru.BouH.engine.render.environment.light.Light;
import ru.BouH.engine.render.environment.light.PointLight;

public class EntityPlayerSP extends PhysEntity implements IRemoteController {
    private final Vector3d cameraRotation;
    private final double speedMultiplier;
    private final double eyeHeight;
    protected boolean canJump;
    private IController controller;
    private Vector3d inputMotion;
    private boolean isOnGround;
    private int ticksBeforeCanJump;
    private double speed;

    public EntityPlayerSP(World world, RigidBodyObject.PhysProperties properties, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, "phys_playerSP", properties, 1.0d, pos, rot);
        this.inputMotion = new Vector3d(0.0d);
        this.cameraRotation = new Vector3d();
        this.eyeHeight = 0.425d;
        this.setSpeed(4.0f);
        this.speedMultiplier = 5.0d;
        this.canJump = false;
    }

    public void onUpdate(IWorld world) {
        super.onUpdate(world);
        if (this.isValid()) {
            if (this.isValidController()) {
                if (this.isOnGround()) {
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
                this.getRigidBodyObject().activateObject();
            }
            this.groundCheck();
            if (this.isValidController()) {
                final Vector3d motionC = this.calcControllerMotion();
                this.onStep(this.getStepVelocityVector(motionC));
                if (this.isInWater()) {
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

    private void onStep(Vector3d vector3d) {
        this.setVelocityVector(vector3d);
    }

    private Vector3d getStepVelocityVector(Vector3d motion) {
        double speed = this.getObjectSpeed();
        Vector3d v1 = this.getMotionVector(motion).mul(this.getSpeed());
        if (speed > this.getSpeed() * this.getSpeedMultiplier()) {
            v1.div(speed);
        }
        if (!this.isOnGround() && !this.isInWater()) {
            this.getRigidBodyObject().setFrictionAxes(new Vector3d(0.0d));
            v1.mul(0.05d);
        } else {
            this.getRigidBodyObject().setFrictionAxes(new Vector3d(1.0d));
        }
        return v1;
    }

    public double getSpeedMultiplier() {
        return this.speedMultiplier;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    protected void groundCheck() {
        btVector3 v1 = new btVector3();
        btVector3 v2 = new btVector3();
        this.getRigidBodyObject().getAabb(v1, v2);

        btTransform transform_m = this.getRigidBodyObject().getWorldTransform();
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
        this.getRigidBodyObject().setFrictionAxes(new Vector3d(0.0d));
        this.addObjectVelocity(new Vector3d(0.0d, up ? 0.5d : -0.3d, 0.0d));
    }

    public void onJump() {
        if (this.canJump) {
            this.getRigidBodyObject().setFrictionAxes(new Vector3d(0.0d));
            this.addObjectVelocity(new Vector3d(0.0d, 8.0d, 0.0d));
            this.ticksBeforeCanJump = 20;
            this.canJump = false;
        }
    }

    public boolean isOnGround() {
        return this.isOnGround;
    }

    protected Vector3d getMotionVector(Vector3d vector3d) {
        Vector3d vector3d1 = new Vector3d(vector3d).mul(new Vector3d(1, 0, 1));
        if (vector3d1.length() > 0) {
            vector3d1.normalize();
        }
        return vector3d1;
    }

    private Vector3d calcControllerMotion() {
        double[] motion = new double[3];
        double[] input = new double[3];
        input[0] = this.inputMotion.x;
        input[1] = this.inputMotion.y;
        input[2] = this.inputMotion.z;
        if (input[2] != 0) {
            motion[0] += Math.sin(Math.toRadians(this.getRotation().y)) * -1.0f * input[2];
            motion[2] += Math.cos(Math.toRadians(this.getRotation().y)) * input[2];
        }
        if (input[0] != 0) {
            motion[0] += Math.sin(Math.toRadians(this.getRotation().y - 90)) * -1.0f * input[0];
            motion[2] += Math.cos(Math.toRadians(this.getRotation().y - 90)) * input[0];
        }
        if (input[1] != 0) {
            motion[1] += input[1];
        }
        return new Vector3d(motion[0], motion[1], motion[2]);
    }

    public Vector3d getCameraRotation() {
        return this.cameraRotation;
    }

    public boolean canBeDestroyed() {
        return false;
    }

    public double getEyeHeight() {
        return this.eyeHeight;
    }

    @Override
    protected AbstractCollision constructCollision() {
        return new OBB(new Vector3d(0.75d, 1.5d, 0.75d));
    }

    public Vector3d getRotation() {
        return this.isValidController() ? this.getCameraRotation() : super.getRotation();
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
    public void performController(Vector2d rotationInput, Vector3d xyzInput) {
        if (BindingList.instance.keyBlock1.isClicked()) {
            PhysEntityCube entityPropInfo = new PhysEntityCube(this.getWorld(), RigidBodyObject.PhysProperties.createProperties(Materials.brickCube, false, 50.0d), new Vector3d(1.0d), 1.5d, this.getPosition().add(this.getLookVector().mul(2.0f)), new Vector3d(0.0d));
            Game.getGame().getProxy().addItemInWorlds(entityPropInfo, ResourceManager.renderDataAssets.entityCube);
            entityPropInfo.setObjectVelocity(this.getLookVector().mul(30.0f));
        }
        if (BindingList.instance.keyBlock2.isClicked()) {
            PhysEntityCube entityPropInfo = new PhysLightCube(this.getWorld(), RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, false, 1.0d), new Vector3d(1.0d), 0.25d, this.getPosition().add(this.getLookVector().mul(2.0f)), new Vector3d(0.0d));
            int a = Game.random.nextInt(3);
            Game.getGame().getProxy().addItemInWorlds(entityPropInfo, ResourceManager.renderDataAssets.entityLamp);
            PointLight pointLight = (PointLight) new PointLight().setLightColor(new Vector3d(a == 0 ? 1.0d : Game.random.nextFloat(), a == 1 ? 1.0d : Game.random.nextFloat(), a == 2 ? 1.0d : Game.random.nextFloat()));
            pointLight.setBrightness(8.0f);
            Game.getGame().getProxy().addLight(entityPropInfo, pointLight);
            entityPropInfo.setObjectVelocity(this.getLookVector().mul(20.0f));
        }
        if (BindingList.instance.keyBlock3.isClicked()) {
            PhysEntityCube entityPropInfo = new PhysEntityCube(this.getWorld(), RigidBodyObject.PhysProperties.createProperties(Materials.brickCube, true, 50.0d), new Vector3d(1.0d), 1.3d, this.getPosition().add(this.getLookVector().mul(2.0f)), new Vector3d(0.0d));
            Game.getGame().getProxy().addItemInWorlds(entityPropInfo, ResourceManager.renderDataAssets.entityCube2);
            entityPropInfo.setObjectVelocity(this.getLookVector().mul(50.0f));
        }
        if (BindingList.instance.keyClear.isClicked()) {
            this.getWorld().clearAllItems();
        }
        this.getCameraRotation().add(new Vector3d(rotationInput, 0.0d));
        this.inputMotion = new Vector3d(xyzInput);
        this.clampCameraRotation();
    }

    private void clampCameraRotation() {
        if (this.getRotation().x > 90) {
            this.getCameraRotation().set(new Vector3d(90, this.getRotation().y, this.getRotation().z));
        }
        if (this.getRotation().x < -90) {
            this.getCameraRotation().set(new Vector3d(-90, this.getRotation().y, this.getRotation().z));
        }
    }
}
