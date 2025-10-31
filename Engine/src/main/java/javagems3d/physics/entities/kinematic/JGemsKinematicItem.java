package javagems3d.physics.entities.kinematic;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.CollisionFlag;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.PhysicsSweepTestResult;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.ConvexShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Transform;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.debug.DebugLinesDrawer;
import javagems3d.help.JGemsHelper;
import javagems3d.physics.entities.bullet.IJGemsBulletEntity;
import javagems3d.physics.entities.properties.collision.CollisionType;
import javagems3d.physics.entities.properties.state.EntityState;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.IWorldTicked;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.thread.JGemsPhysics;
import javagems3d.physics.world.thread.dynamics.DynamicsSystem;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.service.collections.Pair;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class JGemsKinematicItem extends WorldItem implements IWorldTicked, IJGemsBulletEntity {
    private final Vector3f bodyVelocity;
    private PhysicsRigidBody ghostBody;
    private PhysicsRigidBody physicsBody;
    protected boolean isOnGround;
    private EntityState entityState;
    private ConvexShape groundCheckShape;

    protected int jumpCooldownR;
    private int jumpCooldown;
    private float gravity;
    private float walkSpeed;
    private float jumpHeight;
    private double slopeAngle;
    private float stepHeight;
    private float linearVelDamping;

    public JGemsKinematicItem(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scaling, String itemName) {
        super(world, pos, rot, scaling, itemName);
        this.bodyVelocity = new Vector3f();
    }

    public JGemsKinematicItem(PhysicsWorld world, Vector3f pos, Vector3f rot, String itemName) {
        this(world, pos, rot, new Vector3f(1.0f), itemName);
    }

    public JGemsKinematicItem(PhysicsWorld world, Vector3f pos, String itemName) {
        this(world, pos, new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    public JGemsKinematicItem(PhysicsWorld world, String itemName) {
        this(world, new Vector3f(0.0f), new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    protected ConvexShape createGhostShape() {
        return new CapsuleCollisionShape(this.shapeSize().x, this.shapeSize().y, 1);
    }

    protected ConvexShape createPhysicsShape() {
        return new CapsuleCollisionShape(this.shapeSize().x + 0.01f, this.shapeSize().y - 0.01f, 1);
    }

    protected ConvexShape createGhostShapeShapeForGroundCheck() {
        return new CapsuleCollisionShape(this.shapeSize().x - 0.01f, this.shapeSize().y - 0.01f, 1);
    }

    public ConvexShape getGroundCheckShape() {
        return this.groundCheckShape;
    }

    protected abstract Vector2f shapeSize();
    protected abstract Vector3f getMoveVector();

    protected void createObject() {
        this.ghostBody = new PhysicsRigidBody(this.createGhostShape());
        this.physicsBody = new PhysicsRigidBody(this.createPhysicsShape());
        this.groundCheckShape = this.createGhostShapeShapeForGroundCheck();
        this.setCollisionGroup(CollisionType.PLAYER);
        this.setCollisionFilter(CollisionType.UNIVERSAL);
        this.getGhostBody().setUserObject(this);
        this.getGhostBody().setKinematic(true);

        this.getPhysicsBody().setKinematic(true);
        this.getPhysicsBody().setMass(25.0f);
        this.getGhostBody().setMass(1.0f);
    }

    protected void setDefaults() {
        this.setGravity(-9.8f);
        this.setJumpHeight(1.0f);
        this.setWalkSpeed(0.25f);
        this.setSlopeAngle(Math.toRadians(50.0f));
        this.setStepHeight(0.5f); //TODO
        this.setLinearVelDamping(0.7f);
        this.setJumpCooldown(JGemsPhysics.TICKS_PER_SECOND / 2);
    }

    protected float gravityDiv() {
        return 800.0f;
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        this.setDefaults();
        this.createObject();
        this.createEntityState();
        ((PhysicsWorld) iWorld).getDynamics().addCollisionObject(this.getGhostBody());
        ((PhysicsWorld) iWorld).getDynamics().addCollisionObject(this.getPhysicsBody());

        DynamicsUtils.transformRigidBody(this.getGhostBody(), this.startPosition, this.startRotation, this.startScaling);
    }

    protected void createEntityState() {
        this.entityState = new EntityState();
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        super.onDestroy(iWorld);
        ((PhysicsWorld) iWorld).getDynamics().removeCollisionObject(this.getGhostBody());
        ((PhysicsWorld) iWorld).getDynamics().removeCollisionObject(this.getPhysicsBody());
    }

    private boolean checkIfOnGround(PhysicsRigidBody ghostBody, Vector3f fromPosition) {
        {
            SweepResult sweepResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), ghostBody, this.getGroundCheckShape(), fromPosition, new Vector3f(0.0f, -0.1f, 0.0f), new Vector3i(0, 1, 0));
            if (sweepResult.getHitNormal() != null) {
                if (this.checkDotAngle(this.up(), sweepResult.getHitNormal(), this.getSlopeAngle(), true)) {
                    return true;
                }
            }
        }
        {
            float threshold = 0.05f;
            SweepResult sweepResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), ghostBody, this.getGroundCheckShape(), new Vector3f(fromPosition).add(0.0f, 0.01f, 0.0f), new Vector3f(0.0f, -threshold, 0.0f), new Vector3i(0, 1, 0));
            if (sweepResult.getHitNormal() != null) {
                BoundingBox boundingBox = new BoundingBox();
                this.getGhostBody().boundingBox(boundingBox);
                RayResult result = RayResult.getRayHitResult(this.getWorld().getDynamics(), ghostBody, sweepResult.getCorrectedPos(), new Vector3f(sweepResult.getCorrectedPos()).sub(0.0f, boundingBox.getYExtent() + (this.getStepHeight() + threshold), 0.0f));
                if (result.getHitNormal() != null) {
                    float y = sweepResult.getCorrectedPos().y;
                    if (JGemsConfig.DEBUG.SHOW_DEBUG_LINES) {
                        JGemsOpenGLRenderer.DebugLinesDrawer().addRequest(DebugLinesDrawer.LineRequest(sweepResult.getCorrectedPos(), new Vector3f(sweepResult.getCorrectedPos()).sub(0.0f, boundingBox.getYExtent() + (this.getStepHeight() + threshold), 0.0f), new Vector3f(1.0f, 0.0f, 0.0f), DebugLinesDrawer.noDepth(), DebugLinesDrawer.noDepth()));
                    }
                    return this.checkDotAngle(this.up(), result.getHitNormal(), this.getSlopeAngle(), true);
                }
            }
        }
        return false;
    }

    private Vector3f tryStepDown(Vector3f currPos, float height) {
        final float maxY = (float) Math.max(Math.sin(this.getSlopeAngle()), height) + 0.05f;

        BoundingBox boundingBox = new BoundingBox();
        this.getGhostBody().boundingBox(boundingBox);
        RayResult rayResult = RayResult.getRayHitResult(this.getWorld().getDynamics(), this.getGhostBody(), currPos, new Vector3f(currPos).sub(0.0f, boundingBox.getYExtent() + 1.0f, 0.0f));

        if (rayResult.getHitNormal() != null) {
            if (this.checkDotAngle(this.up(), rayResult.getHitNormal(), this.getSlopeAngle(), true)) {
                SweepResult sweepDown = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getGhostBody(), this.getGroundCheckShape(), currPos, new Vector3f(0.0f, -maxY, 0.0f), new Vector3i(0, 1, 0));
                boolean onGround = this.checkIfOnGround(this.getGhostBody(), sweepDown.getCorrectedPos());
                if (onGround) {
                    float deltaY = currPos.y - sweepDown.getCorrectedPos().y;
                    if (deltaY > 0.01f && deltaY <= maxY) {
                        return sweepDown.getCorrectedPos();
                    }
                }
            }
        }

        return null;
    }

    private Vector3f tryStepUp(Vector3f currPos, Vector3f motion, float height) {
        final float motionSpeed = motion.length();
        Vector3f rawResult = this.tryStepInterval(currPos, motion, height);

        if (rawResult == null) {
            return null;
        }

        final float yGet = (rawResult.y - currPos.y) + 0.005f;
        Vector3f normalizedMotion = new Vector3f(motion.x, yGet, motion.z).normalize(motionSpeed).mul(1.0f, 0.0f, 1.0f);
        Vector3f newAttempt = this.tryStepInterval(currPos, normalizedMotion, yGet);

        if (newAttempt != null) {
            return newAttempt;
        }

        return rawResult;
    }

    private Vector3f tryStepInterval(Vector3f currPos, Vector3f motion, float height) {
        SweepResult sweepUp = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getGhostBody(), this.getGroundCheckShape(), currPos, new Vector3f(0.0f, height, 0.0f), new Vector3i(0, 1, 0));
        Vector3f steppedUpPos = sweepUp.getCorrectedPos();

        SweepResult sweepForward = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getGhostBody(), this.getGroundCheckShape(), steppedUpPos, motion, new Vector3i(1));
        Vector3f forwardPos = sweepForward.getCorrectedPos();

        float movedDist = new Vector3f(forwardPos).sub(steppedUpPos).length();
        if (movedDist < 0.01f) {
            return null;
        }

        SweepResult sweepDown = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getGhostBody(), this.getGroundCheckShape(), forwardPos, new Vector3f(0.0f, -height, 0.0f), new Vector3i(0, 1, 0));
        Vector3f landedPos = sweepDown.getCorrectedPos();

        if (sweepDown.getHitNormal() != null && this.checkDotAngle(this.up(), sweepDown.getHitNormal(), this.getSlopeAngle(), true)) {
            boolean onGround = this.checkIfOnGround(this.getGhostBody(), landedPos);
            float deltaY = landedPos.y - currPos.y;
            if (onGround && deltaY > 0.0f && deltaY <= height) {
                return landedPos;
            }
        }

        return null;
    }

    private boolean checkDotAngle(Vector3f v1, Vector3f v2, double angle, boolean ifOrthogonal) {
        float dot = new Vector3f(v1).dot(v2);
        if (dot == 1.0f) {
            return ifOrthogonal;
        }
        return Math.acos(dot) <= angle;
    }

    private float getLiquidSwimUpFactor() {
        Vector3f contactPoint = new Vector3f(0.0f);

        BoundingBox boundingBox = new BoundingBox();
        this.getGhostBody().boundingBox(boundingBox);

        this.getWorld().getDynamics().getPhysicsSpace().contactTest(this.getGhostBody(), (e) -> {
            PhysicsCollisionObject collisionObjectB = e.getObjectB();
            if (CollisionType.LIQUID.matchMask(collisionObjectB.getCollisionGroup())) {
                com.jme3.math.Vector3f vector3f = new com.jme3.math.Vector3f();
                e.getLocalPointB(vector3f);
                contactPoint.set(DynamicsUtils.convertV3F_JOML(vector3f)).add(DynamicsUtils.getObjectBodyPos(collisionObjectB));
            }
        });

        if (contactPoint.length() <= 0.0f) {
            return -1.0f;
        }

        {
            com.jme3.math.Vector3f rayFrom = DynamicsUtils.createV3F_JME(contactPoint.x, this.getPosition().y, contactPoint.z);
            com.jme3.math.Vector3f rayTo = DynamicsUtils.createV3F_JME(contactPoint.x, this.getPosition().y - boundingBox.getYExtent(), contactPoint.z);

            List<PhysicsRayTestResult> rayTestResults = this.getWorld().getDynamics().getPhysicsSpace().rayTestRaw(rayFrom, rayTo);
            for (PhysicsRayTestResult physicsRayTestResult : rayTestResults) {
                if (CollisionType.LIQUID.matchMask(physicsRayTestResult.getCollisionObject().getCollisionGroup())) {
                    return -1.0f;
                }
            }
        }

        com.jme3.math.Vector3f rayFrom = DynamicsUtils.createV3F_JME(contactPoint.x, this.getPosition().y + boundingBox.getYExtent(), contactPoint.z);
        com.jme3.math.Vector3f rayTo = DynamicsUtils.createV3F_JME(contactPoint.x, this.getPosition().y, contactPoint.z);
        List<PhysicsRayTestResult> rayTestResults = this.getWorld().getDynamics().getPhysicsSpace().rayTestRaw(rayFrom, rayTo);
        for (PhysicsRayTestResult physicsRayTestResult : rayTestResults) {
            if (CollisionType.LIQUID.matchMask(physicsRayTestResult.getCollisionObject().getCollisionGroup())) {
                com.jme3.math.Vector3f hitP = DynamicsUtils.lerp(rayFrom, rayTo, physicsRayTestResult.getHitFraction());
                float f1 = rayFrom.distance(rayTo);
                float f2 = rayFrom.distance(hitP);
                return 1.0f - (f2 / f1);
            }
        }

        return 1.0f;
    }
    
    @Override
    public final void onUpdate(IWorld iWorld) {
        this.onTick(iWorld);
        this.getPhysicsBody().setPhysicsLocation(DynamicsUtils.convertV3F_JME(this.getPosition()));
        this.getEntityState().removeState(EntityState.Type.IN_LIQUID);
    }

    protected void onTick(IWorld iWorld) {
        if (this.getPosition().y < -50.0f) {
            this.resetWarp();
        }

        this.jumpCooldownR -= 1;
        this.isOnGround = this.checkIfOnGround(this.getGhostBody(), this.getPosition());

        final float walkSpeed = this.getWalkSpeed();
        final float jumpSpeed = this.getJumpHeight();
        final float damping = this.getLinearVelDamping();

        Vector3f motion = this.getMoveVector();

        this.linearDampingXZ(damping);
        this.walkVelocity(motion, damping, walkSpeed);
        float waterJumpFactor = this.tryToJump(this.getGravity(), motion, jumpSpeed);
        this.gravityVelocity(this.getGravity());

        JGemsHelper.math().clampVectorToZeroThreshold(this.bodyVelocity, 0.001f);

        this.move(waterJumpFactor, this.getBodyVelocity());
    }

    // return = waterSwim
    protected float tryToJump(float gravity, Vector3f motionVecController, float jumpHeight) {
        if (motionVecController.y > 0.0f) {
            if (this.isInWater()) {
                if (this.isOnGround()) {
                    this.jump(gravity, jumpHeight);
                } else {
                    float factor1 = this.getLiquidSwimUpFactor() * (this.getJumpHeight() * 0.5f);
                    if (factor1 > 0.0f) {
                        float jumpSpeedT = (float) Math.sqrt(2.0f * Math.abs(gravity / this.gravityDiv()) * factor1);
                        this.setBodyVelocity(new Vector3f(this.getBodyVelocity().x, jumpSpeedT, this.getBodyVelocity().z));
                    }
                    return factor1;
                }
            } else if (this.isOnGround()) {
                this.jump(gravity, jumpHeight);
            }
        }
        return -1.0f;
    }

    public boolean canJump() {
        return this.jumpCooldownR <= 0;
    }

    protected void jump(float gravity, float height) {
        if (!this.canJump()) {
            return;
        }
        float jumpSpeedT = (float) Math.sqrt(2.0f * Math.abs(gravity / this.gravityDiv()) * height);
        this.addBodyVelocity(new Vector3f(0.0f, jumpSpeedT, 0.0f));
        this.jumpCooldownR = this.getJumpCooldown();
    }

    protected void linearDampingXZ(float damping) {
        if (!this.isOnGround() || this.isInWater()) {
            damping *= 0.5f;
        }
        this.setBodyVelocity(new Vector3f(this.getBodyVelocity().x * damping, this.getBodyVelocity().y, this.getBodyVelocity().z * damping));
    }

    protected void walkVelocity(Vector3f walkDir, float damping, float accelerationMul) {
        Vector3f speedXZ = this.getBodyVelocity().mul(1.0f, 0.0f, 1.0f);

        Vector3f toAdd = new Vector3f(walkDir).mul(1.0f, 0.0f, 1.0f);
        if (toAdd.length() > 0.0f) {
            toAdd.normalize().mul(this.getWalkSpeed() / damping).mul(accelerationMul);

            Vector3f newSpeedXZ = new Vector3f(speedXZ).add(toAdd);
            float newSpeed = newSpeedXZ.length();

            if (newSpeed > this.getWalkSpeed()) {
                newSpeedXZ.normalize().mul(this.getWalkSpeed());
            }

            this.setBodyVelocity(new Vector3f(newSpeedXZ.x, this.getBodyVelocity().y, newSpeedXZ.z));
        }
    }

    protected void gravityVelocity(float gravity) {
        if (this.isOnGround()) {
            if (this.getBodyVelocity().y < 0.0f) {
                this.setBodyVelocity(new Vector3f(this.getBodyVelocity().x, 0.0f, this.getBodyVelocity().z));
            }
        } else {
            this.addBodyVelocity(new Vector3f(0.0f, gravity / this.gravityDiv(), 0.0f));
            if (this.isInWater()) {
                this.setBodyVelocity(new Vector3f(this.getBodyVelocity().x, this.getBodyVelocity().y * 0.875f, this.getBodyVelocity().z));
            }
        }
    }

    protected void move(float waterJumpFactor, Vector3f motion) {
        if (motion.length() > 0) {
            this.moveWithCollision(waterJumpFactor, motion);
        }
        if (motion.y < 0.0f) {
            this.motionYAlignment(0.05f);
        }
    }

    protected void motionYAlignment(float measure) {
        SweepResult sweepResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getGhostBody(), this.getGroundCheckShape(), this.getPosition(), new Vector3f(0.0f, -0.1f, 0.0f), new Vector3i(0, 1, 0));

        if (sweepResult.getHitNormal() != null) {
            if (this.checkDotAngle(this.up(), sweepResult.getHitNormal(), this.getSlopeAngle(), true)) {
                final float threshold = 0.01f;
                Vector3f newPos = sweepResult.getCorrectedPos().add(0.0f, threshold, 0.0f);
                if (newPos.y - this.getPosition().y < measure) {
                    this.setPosition(newPos);
                }
            }
        }
    }

    private void moveWithCollision(float waterJumpFactor, Vector3f motion) {
        if (this.isOnGround()) {
            Vector3f tryStepUp = this.tryStepUp(this.getPosition(), motion, this.getStepHeight());
            if (tryStepUp != null) {
                this.setPosition(new Vector3f(tryStepUp.x, tryStepUp.y + 0.01f, tryStepUp.z));
                return;
            }
        }

        if (this.moveTestXYZ(motion) && !this.isInWater()) {
            if (Math.abs(motion.y) <= 0.01f) {
                Vector3f tryStepDown = this.tryStepDown(this.getPosition(), this.getStepHeight());
                if (tryStepDown != null) {
                    this.setPosition(new Vector3f(tryStepDown.x, tryStepDown.y + 0.01f, tryStepDown.z));
                }
            }
        }
    }

    // slide?
    public boolean moveTestXYZ(Vector3f motion) {
        Vector3f correctedPos = new Vector3f(this.getPosition());
        Vector3f slideVec = null;
        final int maxBumps = 64;
        final float epsilon = 1e-6f;
        for (int i = 0; i < maxBumps; i++) {
            Vector3f inputMotion = (slideVec == null) ? new Vector3f(motion) : slideVec;
            Pair<Vector3f, Vector3f> result = this.bump(correctedPos, inputMotion);
            correctedPos = result.getFirst();
            slideVec = result.getSecond();
            if (slideVec != null) {
                float dot = slideVec.dot(motion);
                if (dot <= 0f) {
                    slideVec.set(0f, 0f, 0f);
                }
            }
            if (slideVec == null || slideVec.lengthSquared() < epsilon) {
                break;
            }
        }
        if (correctedPos != null) {
            this.setPosition(correctedPos);
        }

        return slideVec == null || Math.abs(slideVec.y) < 0.01f;
    }

    // corrected + slide(nullable)
    public Pair<@NotNull Vector3f, @Nullable Vector3f> bump(Vector3f fromPos, Vector3f motion) {
        Vector3f from = new Vector3f(fromPos);
        Vector3f checkTo = new Vector3f(from).add(motion);

        SweepResult sweepTest = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getGhostBody(), (ConvexShape) this.getGhostBody().getCollisionShape(), from, motion, new Vector3i(1), 0.01f);
        if (sweepTest.getHitNormal() == null) {
            return new Pair<>(checkTo, null);
        }

        return new Pair<>(sweepTest.getCorrectedPos(), sweepTest.getSlideMotion());
    }

    public int getCollisionGroup() {
        return this.getGhostBody().getCollisionGroup();
    }

    public void setCollisionGroup(CollisionType... collisionTypes) {
        int i = 0;
        for (CollisionType collisionType : collisionTypes) {
            i |= collisionType.getMask();
        }
        this.getGhostBody().setCollisionGroup(i);
        this.getPhysicsBody().setCollisionGroup(i);
    }

    public int getCollisionFilter() {
        return this.getGhostBody().getCollideWithGroups();
    }

    public void setCollisionFilter(CollisionType... collisionTypes) {
        int i = 0;
        for (CollisionType collisionType : collisionTypes) {
            i |= collisionType.getMask();
        }
        this.getGhostBody().setCollideWithGroups(i);
        this.getPhysicsBody().setCollideWithGroups(i);
    }

    @Override
    public boolean canBeDestroyed() {
        return false;
    }

    @Override
    public EntityState getEntityState() {
        return this.entityState;
    }

    @Override
    public void setEntityState(@NotNull EntityState state) {
        this.entityState = state;
    }

    public Vector3f up() {
        return new Vector3f(0.0f, 1.0f, 0.0f);
    }

    @Override
    public Vector3f getPosition() {
        return DynamicsUtils.getObjectBodyPos(this.getGhostBody());
    }

    @Override
    public void setPosition(Vector3f vector3d) {
        this.getGhostBody().setPhysicsLocation(DynamicsUtils.convertV3F_JME(vector3d));
    }

    public void addBodyVelocity(Vector3f bodyVelocity) {
        this.setBodyVelocity(this.getBodyVelocity().add(bodyVelocity));
    }

    public void setBodyVelocity(Vector3f bodyVelocity) {
        this.bodyVelocity.set(bodyVelocity);
    }

    public Vector3f getBodyVelocity() {
        return new Vector3f(this.bodyVelocity);
    }

    public PhysicsRigidBody getPhysicsBody() {
        return this.physicsBody;
    }

    public PhysicsRigidBody getGhostBody() {
        return this.ghostBody;
    }

    public boolean isInWater() {
        return this.getEntityState().checkState(EntityState.Type.IN_LIQUID);
    }

    public boolean isOnGround() {
        return this.isOnGround;
    }

    public float getGravity() {
        return this.gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public float getWalkSpeed() {
        if (true) {
            //return 0.5f;
        }
        return this.walkSpeed;
    }

    public void setWalkSpeed(float walkSpeed) {
        this.walkSpeed = walkSpeed;
    }

    public float getJumpHeight() {
        return this.jumpHeight;
    }

    public void setJumpHeight(float jumpHeight) {
        this.jumpHeight = jumpHeight;
    }

    public double getSlopeAngle() {
        return this.slopeAngle;
    }

    public void setSlopeAngle(double slopeAngle) {
        this.slopeAngle = slopeAngle;
    }

    public float getStepHeight() {
        return this.stepHeight;
    }

    @Deprecated
    public void setStepHeight(float stepHeight) {
        this.stepHeight = stepHeight;
    }

    public float getLinearVelDamping() {
        return this.linearVelDamping;
    }

    public void setLinearVelDamping(float linearVelDamping) {
        this.linearVelDamping = linearVelDamping;
    }

    public int getJumpCooldown() {
        return this.jumpCooldown;
    }

    public void setJumpCooldown(int jumpCooldown) {
        this.jumpCooldown = jumpCooldown;
    }

    private static class RayResult {
        private final Vector3f correctedPos;
        private final Vector3f hitNormal;
        private final float hitFraction;

        private RayResult(Vector3f correctedPos, Vector3f hitNormal, float hitFraction) {
            this.correctedPos = correctedPos;
            this.hitNormal = hitNormal;
            this.hitFraction = hitFraction;
        }

        public static JGemsKinematicItem.RayResult getRayHitResult(DynamicsSystem dynamicsSystem, PhysicsRigidBody ghostObject, Vector3f posFrom, Vector3f posTo) {
            List<PhysicsRayTestResult> rayTest = dynamicsSystem.getPhysicsSpace().rayTest(DynamicsUtils.convertV3F_JME(posFrom), DynamicsUtils.convertV3F_JME(posTo));
            rayTest.removeIf(e -> {
                if (e.getCollisionObject().equals(ghostObject)) {
                    return true;
                }
                if ((e.getCollisionObject().getCollisionGroup() & CollisionType.PLAYER.getMask()) != 0) {
                    return true;
                }
                if ((e.getCollisionObject().collisionFlags() & CollisionFlag.NO_CONTACT_RESPONSE) != 0) {
                    return true;
                }
                if ((e.getCollisionObject().getCollisionGroup() & ghostObject.getCollideWithGroups()) == 0) {
                    return true;
                }
                return false;
            });
            if (!rayTest.isEmpty()) {
                PhysicsRayTestResult physicsSweepTestResult1 = rayTest.get(0);
                float distanceToHit = physicsSweepTestResult1.getHitFraction();
                Vector3f corrected = DynamicsUtils.lerp(posFrom, posTo, 1.0f - distanceToHit);

                com.jme3.math.Vector3f normal0 = new com.jme3.math.Vector3f();
                physicsSweepTestResult1.getHitNormalLocal(normal0);
                Vector3f normal = DynamicsUtils.convertV3F_JOML(normal0);
                Vector3f inNormal = new Vector3f(normal);
                JGemsHelper.math().clampVectorToZeroThreshold(inNormal, 0.001f);
                if (inNormal.length() > 0f) {
                    inNormal.normalize();
                }

                return new JGemsKinematicItem.RayResult(corrected, inNormal, distanceToHit);
            }
            return new JGemsKinematicItem.RayResult(null, null, -1.0f);
        }

        public float getHitFraction() {
            return this.hitFraction;
        }

        public Vector3f getCorrectedPos() {
            return this.correctedPos;
        }

        public Vector3f getHitNormal() {
            return this.hitNormal;
        }
    }

    private static class SweepResult {
        private final Vector3f correctedPos;
        private final Vector3f hitNormal;
        private final Vector3f slideMotion;
        private final float hitFraction;
        private static final List<PhysicsRayTestResult> results = new ArrayList<>();

        private SweepResult(Vector3f correctedPos, Vector3f hitNormal, Vector3f slideMotion, float hitFraction) {
            this.correctedPos = correctedPos;
            this.hitNormal = hitNormal;
            this.slideMotion = slideMotion;
            this.hitFraction = hitFraction;
        }

        public static SweepResult getSweepHitResult(DynamicsSystem dynamicsSystem, PhysicsRigidBody ghostObject, ConvexShape convexShape, Vector3f posFrom, Vector3f motion, Vector3i axis, Vector3f slideNormalCorrection, float ccd) {
            if (!posFrom.isFinite() || !motion.isFinite()) {
                Log.get().error("INVALID MOTION OR POSITION: " + posFrom + " " + motion);
                return new SweepResult(posFrom, null, null, 1.0f);
            }

            Vector3f nmAxisM = new Vector3f(motion).mul(new Vector3f(axis));
            Vector3f moveTo1 = new Vector3f(posFrom).add(nmAxisM);
            Transform start = new Transform().setTranslation(DynamicsUtils.convertV3F_JME(posFrom));
            Transform end = new Transform().setTranslation(DynamicsUtils.convertV3F_JME(moveTo1));

            List<PhysicsSweepTestResult> sweepTestResultList = dynamicsSystem.getPhysicsSpace().sweepTest(convexShape, start, end, new ArrayList<>(), ccd);

            sweepTestResultList.removeIf(e -> {
                if (e.getCollisionObject().equals(ghostObject)) {
                    return true;
                }
                if ((e.getCollisionObject().getCollisionGroup() & CollisionType.PLAYER.getMask()) != 0) {
                    return true;
                }
                if ((e.getCollisionObject().collisionFlags() & CollisionFlag.NO_CONTACT_RESPONSE) != 0) {
                    return true;
                }
                if ((e.getCollisionObject().getCollisionGroup() & ghostObject.getCollideWithGroups()) == 0) {
                    return true;
                }
                return false;
            });
            sweepTestResultList.sort(Comparator.comparingDouble(PhysicsSweepTestResult::getHitFraction));
            if (!sweepTestResultList.isEmpty()) {
                PhysicsSweepTestResult physicsSweepTestResult1 = sweepTestResultList.get(0);
                float distanceToHit = physicsSweepTestResult1.getHitFraction();
                Vector3f corrected = DynamicsUtils.lerp(posFrom, moveTo1, distanceToHit);

                com.jme3.math.Vector3f normal0 = new com.jme3.math.Vector3f();
                physicsSweepTestResult1.getHitNormalLocal(normal0);
                Vector3f normal = DynamicsUtils.convertV3F_JOML(normal0);

                if (slideNormalCorrection != null) {
                    normal.mul(slideNormalCorrection);
                }

                JGemsHelper.math().clampVectorToZeroThreshold(normal, 0.001f);
                if (normal.length() > 0f) {
                    normal.normalize();
                }

                float motionLen = motion.length();
                Vector3f motionDir = motionLen > 1e-6f ? new Vector3f(motion).normalize() : new Vector3f(0f,0f,0f);
                float passedLen = distanceToHit * motionLen;
                float eps = 0.001f;
                passedLen = Math.max(0f, passedLen - eps);
                Vector3f passed = new Vector3f(motionDir).mul(passedLen);
                Vector3f remaining = new Vector3f(motion).sub(passed);

                float dotRem = remaining.dot(normal);
                Vector3f motionAlongNormal = new Vector3f(normal).mul(dotRem);
                Vector3f slideMotion = new Vector3f(remaining).sub(motionAlongNormal);

                //motionAlongNormal = new Vector3f(normal).mul(new Vector3f(motion).dot(normal));
                //slideMotion = new Vector3f(motion).sub(motionAlongNormal);

                if (!slideMotion.isFinite()) {
                    slideMotion.set(0.0f);
                }

                //float normalizedOffset = JGemsHelper.math().clamp(motionLen * 0.02f, 0.001f, 0.005f);
                corrected.add(new Vector3f(normal).mul(0.005f));

                return new SweepResult(corrected, normal, slideMotion, distanceToHit);
            }
            return new SweepResult(moveTo1, null, null, 1.0f);
        }

        public static SweepResult getSweepHitResult(DynamicsSystem dynamicsSystem, PhysicsRigidBody ghostObject, ConvexShape convexShape, Vector3f posFrom, Vector3f motion, Vector3i axis, float ccd) {
            return SweepResult.getSweepHitResult(dynamicsSystem, ghostObject, convexShape, posFrom, motion, axis, null, ccd);
        }

        public static SweepResult getSweepHitResult(DynamicsSystem dynamicsSystem, PhysicsRigidBody ghostObject, ConvexShape convexShape, Vector3f posFrom, Vector3f motion, Vector3i axis, Vector3f slideNormalCorrection) {
            return SweepResult.getSweepHitResult(dynamicsSystem, ghostObject, convexShape, posFrom, motion, axis, slideNormalCorrection, 0.001f);
        }

        public static SweepResult getSweepHitResult(DynamicsSystem dynamicsSystem, PhysicsRigidBody ghostObject, ConvexShape convexShape, Vector3f posFrom, Vector3f motion, Vector3i axis) {
            return SweepResult.getSweepHitResult(dynamicsSystem, ghostObject, convexShape, posFrom, motion, axis, null, 0.001f);
        }

        public float getHitFraction() {
            return this.hitFraction;
        }

        public Vector3f getSlideMotion() {
            return this.slideMotion;
        }

        public Vector3f getCorrectedPos() {
            return this.correctedPos;
        }

        public Vector3f getHitNormal() {
            return this.hitNormal;
        }
    }
}
