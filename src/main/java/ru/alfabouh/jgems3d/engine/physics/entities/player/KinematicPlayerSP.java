package ru.alfabouh.jgems3d.engine.physics.entities.player;

import org.bytedeco.bullet.BulletCollision.*;
import org.bytedeco.bullet.BulletDynamics.btKinematicCharacterController;
import org.bytedeco.bullet.BulletDynamics.btRigidBody;
import org.bytedeco.bullet.LinearMath.btQuaternion;
import org.bytedeco.bullet.LinearMath.btTransform;
import org.bytedeco.bullet.LinearMath.btVector3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.audio.sound.GameSound;
import ru.alfabouh.jgems3d.engine.audio.sound.data.SoundType;
import ru.alfabouh.jgems3d.engine.inventory.IHasInventory;
import ru.alfabouh.jgems3d.engine.inventory.Inventory;
import ru.alfabouh.jgems3d.engine.inventory.items.ItemCrowbar;
import ru.alfabouh.jgems3d.engine.inventory.items.ItemRadio;
import ru.alfabouh.jgems3d.engine.math.MathHelper;
import ru.alfabouh.jgems3d.engine.physics.entities.BodyGroup;
import ru.alfabouh.jgems3d.engine.physics.entities.Materials;
import ru.alfabouh.jgems3d.engine.physics.entities.prop.PhysCube;
import ru.alfabouh.jgems3d.engine.physics.entities.prop.PhysLightCube;
import ru.alfabouh.jgems3d.engine.physics.entities.states.EntityState;
import ru.alfabouh.jgems3d.engine.physics.jb_objects.JBulletEntity;
import ru.alfabouh.jgems3d.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.physics.world.object.CollidableWorldItem;
import ru.alfabouh.jgems3d.engine.physics.world.object.IWorldDynamic;
import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;
import ru.alfabouh.jgems3d.engine.physics.world.timer.PhysicThreadManager;
import ru.alfabouh.jgems3d.engine.render.opengl.environment.light.PointLight;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.RenderPlayerSP;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.immediate_gui.panels.MainMenuPanel;
import ru.alfabouh.jgems3d.engine.system.controller.dispatcher.JGemsControllerDispatcher;
import ru.alfabouh.jgems3d.engine.system.controller.objects.IController;
import ru.alfabouh.jgems3d.engine.system.controller.objects.MouseKeyboardController;
import ru.alfabouh.jgems3d.engine.system.map.Map01;
import ru.alfabouh.jgems3d.engine.system.resources.ResourceManager;

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
    private final GameSound noiseSound;
    private final int maxCds;
    private final int maxCassettes;
    private boolean canPlayerJump;
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
    private boolean hasSoda;
    private Inventory inventory;
    private boolean isRunning;
    private float stamina;
    private float mind;
    private int mindCd;
    private int staminaCd;
    private int pickedCds;
    private int prickedCassettes;

    private boolean killed;
    private int killedCd;
    private boolean victory;
    private int victoryCd;

    public KinematicPlayerSP(World world, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, pos, rot, "player_sp");
        this.cameraRotation = new Vector3d();
        this.inputMotion = new ArrayDeque<>();
        this.walking = new Vector3d();
        this.currentHitScanCoordinate = new Vector3d(0.0d);
        this.stepSpeed = 0.005f;
        this.jumpHeight = 7.5f;
        this.walkingDamping = 0.865f;
        this.currentAcceleration = 1.0f;
        this.walkingAcceleration = 0.075f;
        this.currentSelectedItem = null;
        this.wantsToSelect = null;
        this.wantsToGrab = false;
        this.entityState = new EntityState();
        this.canPlayerJump = true;
        this.runAcceleration = 1.5f;
        this.isRunning = false;
        this.hasSoda = false;
        this.stamina = 1.0f;
        this.mind = 1.0f;
        this.noiseSound = JGems.get().getSoundManager().createSound(ResourceManager.soundAssetsLoader.horror, SoundType.BACKGROUND_AMBIENT_SOUND, 1.5f, 0.0f, 1.0f);
        this.mindCd = 0;
        this.staminaCd = 0;
        this.inventory = new Inventory(this, 4);

        this.pickedCds = 0;
        this.prickedCassettes = 0;

        this.maxCds = 3;
        this.maxCassettes = 7;

        this.killed = false;
        this.killedCd = 0;
        this.victoryCd = 0;
    }

    public boolean isKilled() {
        return this.killed;
    }

    public boolean isVictory() {
        return this.victory;
    }

    public int getPrickedCassettes() {
        return this.prickedCassettes;
    }

    public void setPrickedCassettes(int prickedCassettes) {
        this.prickedCassettes = prickedCassettes;
    }

    public int getPickedCds() {
        return this.pickedCds;
    }

    public void setPickedCds(int pickedCds) {
        this.pickedCds = pickedCds;
    }

    public int getMaxCds() {
        return this.maxCds;
    }

    public int getMaxCassettes() {
        return this.maxCassettes;
    }

    public boolean isCanPlayerJump() {
        return this.canPlayerJump;
    }

    public void setCanPlayerJump(boolean canPlayerJump) {
        this.canPlayerJump = canPlayerJump;
    }

    public btKinematicCharacterController getKinematicCharacterController() {
        return this.kinematicCharacterController;
    }

    public void createPlayer() {
        this.cachingGhostObject = new btPairCachingGhostObject();
        this.collisionShape = new btCapsuleShape(0.4d, 0.6d);
        this.collisionShape2 = this.collisionShape;
        this.cachingGhostObject.setCollisionShape(this.collisionShape2);
        this.cachingGhostObject.setCollisionFlags(btCollisionObject.CF_CHARACTER_OBJECT | btCollisionObject.CF_CUSTOM_MATERIAL_CALLBACK | btCollisionObject.CF_HAS_CONTACT_STIFFNESS_DAMPING);
        this.cachingGhostObject.setContactStiffnessAndDamping(RigidBodyObject.STIFFNESS, RigidBodyObject.DAMPING);
        this.kinematicCharacterController = new btKinematicCharacterController(this.cachingGhostObject, this.collisionShape, this.stepHeight, new btVector3(0.0f, 1.0f, 0.0f));
        this.getKinematicCharacterController().setMaxSlope(this.maxSlope);
        this.getKinematicCharacterController().setMaxPenetrationDepth(0.2d);
        this.getKinematicCharacterController().setWalkDirection(new btVector3(0, 0, 0));
        this.getBulletObject().setUserIndex2(this.getItemId());
        this.setCollisionTranslation(position);
        this.setCollisionRotation(rotation);
        this.noiseSound.playSound();
    }

    @Override
    public void onSpawn(IWorld world) {
        super.onSpawn(world);
        JGems.get().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.horror2, SoundType.BACKGROUND_AMBIENT_SOUND, 1.5f, 1.0f);
        JGems.get().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.crackling, SoundType.BACKGROUND_SOUND, 1.0f, 1.0f);
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

    @Override
    public void setRotation(Vector3d vector3d) {
        this.cameraRotation.set(vector3d);
    }

    public Vector3d getRotation() {
        return this.getCameraRotation();
    }

    public boolean canBeDestroyed() {
        return false;
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
        if (this.entityState().checkState(EntityState.StateType.IN_WATER)) {
            JGems.get().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.pl_slosh[JGems.random.nextInt(4)], SoundType.BACKGROUND_SOUND, 1.0f, 0.25f);
        }
        JGems.get().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.pl_step[JGems.random.nextInt(4)], SoundType.BACKGROUND_SOUND, 1.25f, 0.5f);
    }

    private btVector3 getWalkingSpeed(Vector3d motion) {
        float step = this.getStepSpeed() * ((this.isRunning()) ? this.runAcceleration : 1.0f);
        step *= !this.kinematicCharacterController.onGround() ? 0.75f : 1.0f;
        if (new Vector3d(motion.x, 0, motion.z).length() > 0) {
            if (this.kinematicCharacterController.onGround() && this.getTicksExisted() % (this.isRunning ? 15 : 20) == 0) {
                this.playStepSound();
            }
            this.walking.set(new Vector3d(motion).mul(1, 0, 1));
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

    private boolean canPlayerSeeEnemy() {
        btVector3 va1 = new btVector3(Map01.entityManiac.getPosition().x, Map01.entityManiac.getPosition().y, Map01.entityManiac.getPosition().z);
        btVector3 va2 = new btVector3(this.getPosition().x, this.getPosition().y + this.getEyeHeight(), this.getPosition().z);

        boolean flag = true;

        btCollisionWorld.ClosestRayResultCallback rayResultCallback = new btCollisionWorld.ClosestRayResultCallback(va1, va2);
        rayResultCallback.m_collisionFilterMask(btBroadphaseProxy.DefaultFilter & ~BodyGroup.PlayerFilter & ~BodyGroup.LiquidFilter & ~BodyGroup.GhostFilter);
        this.getWorld().getDynamicsWorld().rayTest(va1, va2, rayResultCallback);

        if (rayResultCallback.hasHit()) {
            flag = false;
        }

        va1.deallocate();
        va2.deallocate();
        rayResultCallback.deallocate();

        return flag;
    }

    private void checkEnemy() {
        if (Map01.entityManiac == null) {
            return;
        }
        boolean isRadioActive = this.inventory().getCurrentItem() instanceof ItemRadio && ((ItemRadio) this.inventory().getCurrentItem()).isOpened();
        boolean canSee = this.canPlayerSeeEnemy();
        double dist = this.getPosition().distance(Map01.entityManiac.getPosition());
        int maxDist = 52;
        int level = (int) (((maxDist - Math.min(dist, maxDist)) / maxDist) * 6);
        if (level > 0) {
            float max = 1.0f;
            float delta = 0.0f;
            switch (level) {
                case 1: {
                    max = 0.9f;
                    delta = 0.0001f;
                    break;
                }
                case 2: {
                    max = 0.75f;
                    delta = 0.0003f;
                    break;
                }
                case 3: {
                    max = 0.5f;
                    delta = 0.00075f;
                    break;
                }
                case 4: {
                    max = 0.25f;
                    delta = 0.0025f;
                    break;
                }
                case 5: {
                    max = 0.0f;
                    delta = 0.005f;
                    break;
                }
            }
            this.mindCd = 100;
            if (!canSee) {
                max = max + (1.0f - max) / 2.0f;
                delta *= 0.5f;
            }
            if (isRadioActive) {
                max = Math.max(max, 0.4f);
                delta *= 0.4f;
            }
            if (Math.abs(Map01.entityManiac.getPosition().y - this.getPosition().y) >= 3.0f) {
                max = Math.min(max * 1.3f, 1.0f);
                delta *= 0.5f;
            }
            if (this.mind > max) {
                this.mind = Math.max(this.mind - delta, 0.0f);
            } else {
                this.mind = Math.min(this.mind + (isRadioActive ? 0.0016f : 0.0008f), max);
            }
        } else {
            if (this.mindCd-- <= 0) {
                this.mind = Math.min(this.mind + (isRadioActive ? 0.005f : 0.002f), 1.0f);
            }
        }
        this.noiseSound.setGain((0.7f - this.mind));
    }

    private void kill() {
        if (!this.isKilled()) {
            JGems.get().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.meat, SoundType.BACKGROUND_SOUND, 2.0f, 1.0f);
            this.killed = true;
        }
    }

    private void victory() {
        if (!this.isVictory()) {
            Map01.entityManiac.destroy();
            JGems.get().getScreen().zeroRenderTick();
            JGems.get().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.victory, SoundType.BACKGROUND_SOUND, 1.0f, 1.0f);
            this.victory = true;
        }
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        if (this.getPickedCds() + this.getPrickedCassettes() >= this.getMaxCds() + this.getMaxCassettes()) {
            this.victory();
        }

        if (this.isVictory() && this.victoryCd++ >= 400) {
            MainMenuPanel.showBlood = false;
            MainMenuPanel.victory = true;
            JGems.get().destroyMap();
        }

        if (this.isKilled() && this.killedCd++ >= 150) {
            MainMenuPanel.showBlood = true;
            MainMenuPanel.victory = false;
            JGems.get().destroyMap();
        }
        if (Map01.entityManiac != null && this.getPosition().distance(Map01.entityManiac.getPosition()) <= 1.5d) {
            this.kill();
        }
        this.checkEnemy();
        float staminaDelta = 0.0025f;
        this.staminaCd -= 1;
        if (this.isRunning()) {
            this.stamina -= staminaDelta;
            if (this.stamina <= 0.0f) {
                this.staminaCd = 160;
            }
        } else {
            this.stamina = Math.min(this.stamina + staminaDelta * 0.7f, 1.0f);
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
                rigidBody.getBroadphaseProxy().m_collisionFilterMask(rigidBody.getBroadphaseProxy().m_collisionFilterMask() & ~BodyGroup.PlayerFilter);
                rigidBody.activate();
                Vector3d impulse = new Vector3d(look).normalize().mul(4.0f + JGems.random.nextFloat() * 3.0f);
                rigidBody.applyImpulse(MathHelper.convert(impulse), MathHelper.convert(this.getCurrentHitScanCoordinate().normalize().mul(0.05d + JGems.random.nextFloat() * 0.05f)));
                JGems.get().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.wood_break, SoundType.BACKGROUND_SOUND, 1.0f, 1.0f);
            }
        } else if (this.wantsToSelect != null) {
            this.currentSelectedItem = this.wantsToSelect;
            this.currentSelectedItem.entityState().setState(EntityState.StateType.IS_SELECTED_BY_PLAYER);
        }
        if (this.entityState().checkState(EntityState.StateType.IN_WATER)) {
            this.getKinematicCharacterController().setGravity(new btVector3(0, -4.0f * 3.0f, 0));
            walking.multiplyPut(0.785d);
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
        }
        if (!this.isValidController()) {
            walking.multiplyPut(0.0d);
        }
        this.getKinematicCharacterController().setWalkDirection(walking);
        walking.deallocate();
    }

    public Vector3d getCurrentHitScanCoordinate() {
        return new Vector3d(this.currentHitScanCoordinate);
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
        look.normalize().mul(1.5f);
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

    public boolean isHasSoda() {
        return this.hasSoda;
    }

    public void setHasSoda(boolean hasSoda) {
        this.hasSoda = hasSoda;
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
        if (this.isKilled() || this.isVictory() || !isFocused) {
            this.inputMotion.clear();
            return;
        }
        if (this.currentController() instanceof MouseKeyboardController) {
            MouseKeyboardController mouseKeyboardController = (MouseKeyboardController) this.currentController();
            if (mouseKeyboardController.getMouseAndKeyboard().isLeftKeyPressed()) {
                this.inventory().onMouseLeftClick(this.getWorld());
            }
            if (mouseKeyboardController.getMouseAndKeyboard().isRightKeyPressed()) {
                double x = Math.floor(this.getCurrentHitScanCoordinate().x * 100) / 100f;
                double y = Math.floor(this.getCurrentHitScanCoordinate().y * 100) / 100f + 0.5d;
                double z = Math.floor(this.getCurrentHitScanCoordinate().z * 100) / 100f;
                //System.out.println("news Vector3d(" + x + ", " + y + ", " + z + ")");
                this.inventory().onMouseRightClick(this.getWorld());
            }
            this.inventory().scrollInventoryToNotNullItem(mouseKeyboardController.getMouseAndKeyboard().getScrollVector());
        }
        this.wantsToGrab = JGemsControllerDispatcher.bindingManager().keySelection.isPressed();
        if (JGemsControllerDispatcher.bindingManager().keyX.isClicked() && this.isHasSoda()) {
            if (this.mind < 0.9f || this.stamina < 0.9f) {
                JGems.get().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.soda, SoundType.BACKGROUND_SOUND, 2.0f, 1.0f);
                this.stamina = 1.0f;
                this.mind = Math.min(this.mind + 0.15f, 1.0f);
                this.staminaCd = 0;
                this.setHasSoda(false);
            }
        }
        if (JGems.DEBUG_MODE) {
            if (JGemsControllerDispatcher.bindingManager().keyBlock1.isClicked()) {
                PhysLightCube entityPropInfo = new PhysLightCube(this.getWorld(), RigidBodyObject.PhysProperties.createProperties(Materials.brickCube, false, 50.0d), new Vector3d(1.0d), 1.25d, this.getPosition().add(this.getLookVector().mul(2.0f)), new Vector3d(0.0d));
                JGems.get().getProxy().addItemInWorlds(entityPropInfo, ResourceManager.renderDataAssets.entityCube);
                entityPropInfo.setObjectVelocity(this.getLookVector().mul(30.0f));
            }
            if (JGemsControllerDispatcher.bindingManager().keyBlock2.isClicked()) {
                PhysCube entityPropInfo = new PhysLightCube(this.getWorld(), RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, false, 1.0d), new Vector3d(1.0d), 0.25d, this.getPosition().add(this.getLookVector().mul(2.0f)), new Vector3d(0.0d));
                int a = JGems.random.nextInt(3);
                JGems.get().getProxy().addItemInWorlds(entityPropInfo, ResourceManager.renderDataAssets.entityLamp);
                PointLight pointLight = (PointLight) new PointLight().setLightColor(new Vector3d(a == 0 ? 1.0d : JGems.random.nextFloat(), a == 1 ? 1.0d : JGems.random.nextFloat(), a == 2 ? 1.0d : JGems.random.nextFloat()));
                pointLight.setBrightness(8.0f);
                JGems.get().getProxy().addLight(entityPropInfo, pointLight);
                entityPropInfo.setObjectVelocity(this.getLookVector().mul(20.0f));
            }
            if (JGemsControllerDispatcher.bindingManager().keyBlock3.isPressed()) {
                PhysCube entityPropInfo = new PhysCube(this.getWorld(), RigidBodyObject.PhysProperties.createProperties(Materials.brickCube, true, 50.0d), new Vector3d(1.0d), 1.0d, this.getPosition().add(this.getLookVector().mul(2.0f)), new Vector3d(0.0d));
                JGems.get().getProxy().addItemInWorlds(entityPropInfo, ResourceManager.renderDataAssets.entityCube);
                entityPropInfo.setObjectVelocity(this.getLookVector().mul(50.0f));
            }
        }
        this.getCameraRotation().add(new Vector3d(rotationInput, 0.0d));
        if (this.inputMotion.size() < PhysicThreadManager.TICKS_PER_SECOND * 10) {
            this.setRunning(false);
            if (new Vector3d(xyzInput).mul(1, 0, 1).length() > 0 && xyzInput.y < 0) {
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
