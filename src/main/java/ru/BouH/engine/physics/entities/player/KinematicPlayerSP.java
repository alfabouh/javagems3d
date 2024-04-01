package ru.BouH.engine.physics.entities.player;

import org.bytedeco.bullet.BulletCollision.*;
import org.bytedeco.bullet.BulletDynamics.btKinematicCharacterController;
import org.bytedeco.bullet.BulletDynamics.btRigidBody;
import org.bytedeco.bullet.LinearMath.btQuaternion;
import org.bytedeco.bullet.LinearMath.btTransform;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.audio.sound.data.SoundType;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.controller.binding.BindingList;
import ru.BouH.engine.game.controller.input.IController;
import ru.BouH.engine.game.controller.input.MouseKeyboardController;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.inventory.IHasInventory;
import ru.BouH.engine.inventory.Inventory;
import ru.BouH.engine.inventory.items.ItemCrowbar;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.physics.entities.BodyGroup;
import ru.BouH.engine.physics.entities.Materials;
import ru.BouH.engine.physics.entities.prop.PhysCube;
import ru.BouH.engine.physics.entities.prop.PhysLightCube;
import ru.BouH.engine.physics.entities.states.EntityState;
import ru.BouH.engine.physics.jb_objects.JBulletEntity;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.world.IWorld;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.CollidableWorldItem;
import ru.BouH.engine.physics.world.object.IWorldDynamic;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.physics.world.timer.PhysicThreadManager;
import ru.BouH.engine.render.environment.light.PointLight;
import ru.BouH.engine.render.scene.fabric.render.RenderPlayerSP;

import java.util.ArrayDeque;
import java.util.Deque;

public class KinematicPlayerSP extends WorldItem implements IPlayer, JBulletEntity, IWorldDynamic, IHasInventory {
    private final EntityState entityState;
    private final Vector3d cameraRotation;
    private final Deque<Vector3d> inputMotion;
    private final Vector3d walking;
    private final float walkingDamping;
    private final float walkingAcceleration;
    private final double stepHeight = 0.3f;
    private final double maxSlope = Math.toRadians(45.0f);
    private final float runAcceleration;
    private final Vector3d currentHitScanCoordinate;
    private IController controller;
    private btKinematicCharacterController kinematicCharacterController;
    private btConvexShape collisionShape;
    private btConvexShape collisionShape2;
    private btPairCachingGhostObject cachingGhostObject;
    private float stepSpeed;
    private float jumpHeight;
    private float jumpCd;
    private float currentAcceleration;
    private JBulletEntity currentSelectedItem;
    private JBulletEntity wantsToSelect;
    private boolean wantsToGrab;
    private Inventory inventory;
    private boolean isRunning;
    private float stamina;
    private float mind;
    private int staminaCd;

    public KinematicPlayerSP(World world, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, pos, rot, "player_sp");
        this.cameraRotation = new Vector3d();
        this.inputMotion = new ArrayDeque<>();
        this.walking = new Vector3d();
        this.currentHitScanCoordinate = new Vector3d(0.0d);
        this.stepSpeed = 0.008f;
        this.jumpHeight = 7.5f;
        this.walkingDamping = 0.865f;
        this.currentAcceleration = 1.0f;
        this.walkingAcceleration = 0.075f;
        this.currentSelectedItem = null;
        this.wantsToSelect = null;
        this.wantsToGrab = false;
        this.entityState = new EntityState();
        this.runAcceleration = 1.25f;
        this.isRunning = false;
        this.stamina = 1.0f;
        this.mind = 1.0f;
        this.staminaCd = 0;
        this.inventory = new Inventory(this, 4);
    }

    public btKinematicCharacterController getKinematicCharacterController() {
        return this.kinematicCharacterController;
    }

    public void createPlayer() {
        this.cachingGhostObject = new btPairCachingGhostObject();
        this.collisionShape = new btCapsuleShape(0.4d, 0.6d);
        this.collisionShape2 = new btCapsuleShape(0.25d, 0.6d);
        this.cachingGhostObject.setCollisionShape(this.collisionShape2);
        this.cachingGhostObject.setCollisionFlags(btCollisionObject.CF_CHARACTER_OBJECT | btCollisionObject.CF_CUSTOM_MATERIAL_CALLBACK | btCollisionObject.CF_HAS_CONTACT_STIFFNESS_DAMPING);
        this.cachingGhostObject.setContactStiffnessAndDamping(RigidBodyObject.STIFFNESS, RigidBodyObject.DAMPING);
        this.kinematicCharacterController = new btKinematicCharacterController(this.cachingGhostObject, this.collisionShape, this.stepHeight, new btVector3(0.0f, 1.0f, 0.0f));
        this.getKinematicCharacterController().setMaxSlope(this.maxSlope);
        this.getKinematicCharacterController().setMaxPenetrationDepth(0.0d);
        this.getKinematicCharacterController().setWalkDirection(new btVector3(0, 0, 0));
        this.getBulletObject().setUserIndex2(this.getItemId());
        this.setCollisionTranslation(position);
        this.setCollisionRotation(rotation);
    }

    public float getScalarSpeed() {
        return (float) this.getKinematicCharacterController().getLinearVelocity().length();
    }

    public float getWalkingDamping() {
        return this.walkingDamping;
    }

    public float getWalkingAcceleration() {
        return this.walkingAcceleration;
    }

    public Vector3d getCollisionTranslation() {
        try (btTransform transform = this.getBulletObject().getWorldTransform()) {
            return new Vector3d(transform.getOrigin().getX(), transform.getOrigin().getY(), transform.getOrigin().getZ());
        }
    }

    public void setCollisionTranslation(Vector3d pos) {
        try (btTransform transform = this.getBulletObject().getWorldTransform()) {
            transform.setOrigin(new btVector3(pos.x, pos.y, pos.z));
            this.getBulletObject().setWorldTransform(transform);
            this.getBulletObject().activate();
        }
        this.getWorld().getDynamicsWorld().synchronizeMotionStates();
    }

    public void setCollisionRotation(Vector3d angles) {
        try (btTransform transform = this.getBulletObject().getWorldTransform()) {
            btQuaternion quaternion = new btQuaternion();
            quaternion.setEulerZYX(angles.z, angles.y, angles.x);
            transform.setRotation(quaternion);
            this.getBulletObject().setWorldTransform(transform);
            this.getBulletObject().activate();
        }
        this.getWorld().getDynamicsWorld().synchronizeMotionStates();
    }

    @Override
    public btPairCachingGhostObject getBulletObject() {
        return this.cachingGhostObject;
    }

    public BodyGroup getBodyIndex() {
        return BodyGroup.PLAYER;
    }

    public EntityState entityState() {
        return this.entityState;
    }

    public void onDestroy(IWorld iWorld) {
        super.onDestroy(iWorld);
        World world = (World) iWorld;
        world.getBulletTimer().removeCollisionObjectFromWorld(this.getBulletObject());
        world.getBulletTimer().removeActionObjectFromWorld(this.getKinematicCharacterController());
    }

    public Vector3d getPosition() {
        return new Vector3d(this.getCollisionTranslation());
    }

    public Vector3d getRotation() {
        return this.getCameraRotation();
    }

    public boolean canBeDestroyed() {
        return false;
    }

    public float getStepSpeed() {
        return this.stepSpeed;
    }

    public void setStepSpeed(float stepSpeed) {
        this.stepSpeed = stepSpeed;
    }

    public float getJumpCd() {
        return this.jumpCd;
    }

    public void setJumpCd(float jumpCd) {
        this.jumpCd = jumpCd;
    }

    public float getJumpHeight() {
        return this.jumpHeight;
    }

    public void setJumpHeight(float jumpHeight) {
        this.jumpHeight = jumpHeight;
    }

    public float getMind() {
        return this.mind;
    }

    public float getStamina() {
        return this.stamina;
    }

    private void playStepSound() {
        Game.getGame().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.pl_step[Game.random.nextInt(4)], SoundType.BACKGROUND_SOUND, 1.0f, 0.5f);
    }

    private btVector3 getWalkingSpeed(Vector3d motion) {
        float step = this.getStepSpeed() * ((this.isRunning()) ? this.runAcceleration : 1.0f);
        step *= !this.kinematicCharacterController.onGround() ? 0.75f : 1.0f;
        if (new Vector3d(motion.x, 0, motion.z).length() > 0) {
            if (this.kinematicCharacterController.onGround() && this.getTicksExisted() % (this.isRunning ? 15 : 20) == 0) {
                this.playStepSound();
            }
            this.walking.set(motion);
            this.walking.normalize();
            this.walking.mul(step, 0.0f, step);
            this.walking.mul(Math.min(this.currentAcceleration, 1.0f));
            this.currentAcceleration += this.getWalkingAcceleration();
        } else {
            this.currentAcceleration = 0;
        }
        if (motion.x == 0) {
            this.walking.mul(this.getWalkingDamping(), 1.0f, 1.0f);
        }
        if (motion.z == 0) {
            this.walking.mul(1.0f, 1.0f, this.getWalkingDamping());
        }
        return MathHelper.convert(this.walking);
    }

    private boolean groundCheck() {
        btTransform transform_m = this.getBulletObject().getWorldTransform();
        btTransform transform1 = new btTransform(transform_m);
        btTransform transform2 = new btTransform(transform_m);

        transform1.setOrigin(new btVector3(this.getPosition().x, transform1.getOrigin().y(), this.getPosition().z));
        transform2.setOrigin(new btVector3(this.getPosition().x, transform1.getOrigin().y() + 0.6f, this.getPosition().z));

        btConvexShape convexShape = new btCapsuleShape(this.getBulletObject().getCollisionShape());
        btCollisionWorld.ClosestConvexResultCallback closestConvexResultCallback = new btCollisionWorld.ClosestConvexResultCallback(transform1.getOrigin(), transform2.getOrigin());
        closestConvexResultCallback.m_collisionFilterMask(btBroadphaseProxy.DefaultFilter & ~BodyGroup.GhostFilter);
        this.getWorld().getDynamicsWorld().convexSweepTest(convexShape, transform1, transform2, closestConvexResultCallback);

        convexShape.deallocate();
        transform1.deallocate();
        transform2.deallocate();

        boolean flag = closestConvexResultCallback.hasHit();
        closestConvexResultCallback.deallocate();
        return flag;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        this.inventory().updateInventory(iWorld);
        float staminaDelta = 0.0025f;
        this.staminaCd -= 1;
        if (this.isRunning()) {
            this.stamina -= staminaDelta;
            if (this.stamina <= 0.0f) {
                this.staminaCd = 80;
            }
        } else {
            this.stamina = Math.min(this.stamina + staminaDelta * 0.5f, 1.0f);
        }
        boolean flag = !this.groundCheck();
        Vector3d look = MathHelper.calcLookVector(this.getRotation());
        this.hitScan(look);
        this.getKinematicCharacterController().setStepHeight(this.kinematicCharacterController.onGround() ? this.stepHeight : 0.01f);
        this.getKinematicCharacterController().setMaxSlope(flag ? this.maxSlope : 0.01d);
        if (this.getPosition().y <= -50 || this.getPosition().y >= 500) {
            this.setCollisionTranslation(new Vector3d(0, 5, 0));
        }
        Vector3d cVector = this.calcControllerMotion();
        btVector3 walking = this.getWalkingSpeed(cVector);
        this.grabTest(look);
        if (this.currentSelectedItem != null) {
            if (this.currentSelectedItem != this.wantsToSelect) {
                this.currentSelectedItem.entityState().removeState(EntityState.StateType.IS_SELECTED_BY_PLAYER);
                this.currentSelectedItem = null;
            } else if (this.wantsToGrab && (this.inventory().getCurrentItem() instanceof ItemCrowbar)) {
                CollidableWorldItem collidableWorldItem = (CollidableWorldItem) this.currentSelectedItem;
                collidableWorldItem.entityState().setCanBeSelectedByPlayer(false);
                collidableWorldItem.getBulletObject().makeDynamic();
                btRigidBody rigidBody = btRigidBody.upcast(this.currentSelectedItem.getBulletObject());
                rigidBody.activate();
                Vector3d impulse = new Vector3d(look).normalize().mul(6.0f + Game.random.nextFloat());
                rigidBody.applyCentralImpulse(MathHelper.convert(impulse));
            }
        } else if (this.wantsToSelect != null) {
            this.currentSelectedItem = this.wantsToSelect;
            this.currentSelectedItem.entityState().setState(EntityState.StateType.IS_SELECTED_BY_PLAYER);
        }
        if (this.entityState().checkState(EntityState.StateType.IN_WATER)) {
            this.getKinematicCharacterController().setGravity(new btVector3(0, -4.0f * 3.0f, 0));
            walking.multiplyPut(0.65d);
            if (cVector.y > 0) {
                btVector3 v1 = new btVector3(this.getPosition().x, this.getPosition().y + 1.0f, this.getPosition().z);
                btVector3 v2 = new btVector3(this.getPosition().x, this.getPosition().y - 0.5f, this.getPosition().z);

                btCollisionWorld.ClosestRayResultCallback rayResultCallback = new btCollisionWorld.ClosestRayResultCallback(v1, v2);
                rayResultCallback.m_collisionFilterMask(BodyGroup.LiquidFilter);
                this.getWorld().getDynamicsWorld().rayTest(v1, v2, rayResultCallback);

                if (this.jumpCd-- <= 0) {
                    if (this.kinematicCharacterController.onGround()) {
                        this.getKinematicCharacterController().jump(new btVector3(0, this.getJumpHeight() / 2.0f, 0));
                        this.jumpCd = 10;
                    } else {
                        if (rayResultCallback.hasHit()) {
                            this.getKinematicCharacterController().applyImpulse(new btVector3(0, 2.5d, 0));
                        } else {
                            this.jumpCd = 8;
                        }
                    }
                }

                v1.deallocate();
                v2.deallocate();
                rayResultCallback.deallocate();
            }
        } else {
            this.getKinematicCharacterController().setGravity(new btVector3(0, -9.8f * 2.0f, 0));
            if (this.jumpCd-- <= 0 && flag) {
                if (cVector.y > 0 && this.kinematicCharacterController.onGround()) {
                    this.playStepSound();
                    this.getKinematicCharacterController().jump(new btVector3(0, this.getJumpHeight(), 0));
                    this.jumpCd = 40;
                }
            }
        }
        if (!this.isValidController()) {
            walking.multiplyPut(0.0d);
        }
        this.getKinematicCharacterController().setWalkDirection(walking);
        walking.deallocate();
    }

    public Vector3d getCurrentHitScanCoordinate() {
        return this.currentHitScanCoordinate;
    }

    private void hitScan(Vector3d look) {
        look.normalize().mul(30.0f);
        btVector3 va1 = new btVector3();
        btVector3 va2 = new btVector3();
        this.getBulletObject().getCollisionShape().getAabb(this.getBulletObject().getWorldTransform(), va1, va2);
        btVector3 v1 = new btVector3(this.getPosition().x, this.getPosition().y + this.getEyeHeight(), this.getPosition().z);
        btVector3 v2 = new btVector3(this.getPosition().x + look.x, this.getPosition().y + this.getEyeHeight() + look.y, this.getPosition().z + look.z);
        btCollisionWorld.ClosestRayResultCallback rayResultCallback = new btCollisionWorld.ClosestRayResultCallback(v1, v2);
        rayResultCallback.m_collisionFilterMask(btBroadphaseProxy.DefaultFilter & ~BodyGroup.PlayerFilter & ~BodyGroup.LiquidFilter & ~BodyGroup.GhostFilter);
        this.getWorld().getDynamicsWorld().rayTest(v1, v2, rayResultCallback);
        if (rayResultCallback.hasHit()) {
            btVector3 btVector3 = rayResultCallback.m_hitPointWorld();
            this.currentHitScanCoordinate.set(btVector3.x(), btVector3.y(), btVector3.z());
        } else {
            this.currentHitScanCoordinate.set(0.0d);
        }
        va1.deallocate();
        va2.deallocate();
        v1.deallocate();
        v2.deallocate();
        rayResultCallback.deallocate();
    }

    private void grabTest(Vector3d look) {
        this.wantsToSelect = null;
        look.normalize().mul(1.0f);
        btVector3 va1 = new btVector3();
        btVector3 va2 = new btVector3();
        this.getBulletObject().getCollisionShape().getAabb(this.getBulletObject().getWorldTransform(), va1, va2);
        btVector3 v1 = new btVector3(this.getPosition().x, this.getPosition().y + this.getEyeHeight(), this.getPosition().z);
        btVector3 v2 = new btVector3(this.getPosition().x + look.x, this.getPosition().y + this.getEyeHeight() + look.y, this.getPosition().z + look.z);
        btCollisionWorld.ClosestRayResultCallback rayResultCallback = new btCollisionWorld.ClosestRayResultCallback(v1, v2);
        rayResultCallback.m_collisionFilterMask(btBroadphaseProxy.DefaultFilter & ~BodyGroup.PlayerFilter & ~BodyGroup.LiquidFilter & ~BodyGroup.GhostFilter);
        this.getWorld().getDynamicsWorld().rayTest(v1, v2, rayResultCallback);
        if (rayResultCallback.hasHit()) {
            int id = rayResultCallback.m_collisionObject().getUserIndex2();
            WorldItem worldItem = this.getWorld().getItemByID(id);
            if (worldItem instanceof JBulletEntity) {
                JBulletEntity jBulletEntity = (JBulletEntity) worldItem;
                if (jBulletEntity.entityState().isCanBeSelectedByPlayer()) {
                    this.wantsToSelect = jBulletEntity;
                }
            }
        }
        va1.deallocate();
        va2.deallocate();
        v1.deallocate();
        v2.deallocate();
        rayResultCallback.deallocate();
    }

    public JBulletEntity getCurrentSelectedItem() {
        return this.currentSelectedItem;
    }

    @Override
    public IController currentController() {
        return this.controller;
    }

    public void setController(IController iController) {
        this.controller = iController;
    }

    @Override
    public void performController(Vector2d rotationInput, Vector3d xyzInput, boolean isFocused) {
        if (this.currentController() instanceof MouseKeyboardController) {
            MouseKeyboardController mouseKeyboardController = (MouseKeyboardController) this.currentController();
            if (mouseKeyboardController.getMouse().isLeftKeyPressed()) {
                this.inventory().onMouseLeftClick(this.getWorld());
            }
            if (mouseKeyboardController.getMouse().isRightKeyPressed()) {
                this.inventory().onMouseRightClick(this.getWorld());
            }
            this.inventory().scrollInventoryToNotNullItem(mouseKeyboardController.getMouse().scrollVector);
        }
        this.wantsToGrab = BindingList.instance.keySelection.isPressed();
        if (BindingList.instance.keyBlock1.isClicked()) {
            PhysLightCube entityPropInfo = new PhysLightCube(this.getWorld(), RigidBodyObject.PhysProperties.createProperties(Materials.brickCube, false, 50.0d), new Vector3d(1.0d), 1.25d, this.getPosition().add(this.getLookVector().mul(2.0f)), new Vector3d(0.0d));
            Game.getGame().getProxy().addItemInWorlds(entityPropInfo, ResourceManager.renderDataAssets.entityCube);
            entityPropInfo.setObjectVelocity(this.getLookVector().mul(30.0f));
        }
        if (BindingList.instance.keyBlock2.isClicked()) {
            PhysCube entityPropInfo = new PhysLightCube(this.getWorld(), RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, false, 1.0d), new Vector3d(1.0d), 0.25d, this.getPosition().add(this.getLookVector().mul(2.0f)), new Vector3d(0.0d));
            int a = Game.random.nextInt(3);
            Game.getGame().getProxy().addItemInWorlds(entityPropInfo, ResourceManager.renderDataAssets.entityLamp);
            PointLight pointLight = (PointLight) new PointLight().setLightColor(new Vector3d(a == 0 ? 1.0d : Game.random.nextFloat(), a == 1 ? 1.0d : Game.random.nextFloat(), a == 2 ? 1.0d : Game.random.nextFloat()));
            pointLight.setBrightness(8.0f);
            Game.getGame().getProxy().addLight(entityPropInfo, pointLight);
            entityPropInfo.setObjectVelocity(this.getLookVector().mul(20.0f));
        }
        if (BindingList.instance.keyBlock3.isClicked()) {
            PhysCube entityPropInfo = new PhysCube(this.getWorld(), RigidBodyObject.PhysProperties.createProperties(Materials.brickCube, true, 50.0d), new Vector3d(1.0d), 1.0d, this.getPosition().add(this.getLookVector().mul(2.0f)), new Vector3d(0.0d));
            Game.getGame().getProxy().addItemInWorlds(entityPropInfo, ResourceManager.renderDataAssets.entityCube);
            entityPropInfo.setObjectVelocity(this.getLookVector().mul(50.0f));
        }
        if (BindingList.instance.keyClear.isClicked()) {
            this.getWorld().clearAllItems();
        }
        this.getCameraRotation().add(new Vector3d(rotationInput, 0.0d));
        if (this.inputMotion.size() < PhysicThreadManager.TICKS_PER_SECOND * 10) {
            this.setRunning(false);
            if (xyzInput.y < 0) {
                this.setRunning(true);
                xyzInput.y = 0;
            }
            this.inputMotion.addFirst(new Vector3d(xyzInput));
        }
        this.clampCameraRotation();
    }

    @Override
    public double getEyeHeight() {
        return 0.45d + Math.sin(RenderPlayerSP.stepBobbing * 0.2f) * 0.1f;
    }

    private Vector3d calcControllerMotion() {
        if (this.inputMotion.isEmpty()) {
            return new Vector3d(0.0d);
        }
        double[] motion = new double[3];
        double[] input = new double[3];
        Vector3d inputMotion = this.inputMotion.pop();
        input[0] = inputMotion.x;
        input[1] = inputMotion.y;
        input[2] = inputMotion.z;
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

    public boolean canRun() {
        return this.staminaCd <= 0 && this.stamina > 0;
    }

    public boolean isRunning() {
        return this.isRunning && this.canRun();
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public Vector3d getCameraRotation() {
        return this.cameraRotation;
    }

    private void clampCameraRotation() {
        if (this.getRotation().x > 90) {
            this.getCameraRotation().set(new Vector3d(90, this.getRotation().y, this.getRotation().z));
        }
        if (this.getRotation().x < -90) {
            this.getCameraRotation().set(new Vector3d(-90, this.getRotation().y, this.getRotation().z));
        }
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public synchronized Inventory inventory() {
        return this.inventory;
    }
}
