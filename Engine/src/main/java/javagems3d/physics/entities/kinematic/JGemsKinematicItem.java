package javagems3d.physics.entities.kinematic;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.CollisionFlag;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.PhysicsSweepTestResult;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.ConvexShape;
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
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class JGemsKinematicItem extends WorldItem implements IWorldTicked, IJGemsBulletEntity {
    private final Object transformLock = new Object();

    private final Vector3f bodyVelocity;
    private PhysicsRigidBody ghostBody;
    private PhysicsRigidBody physicsBody;
    protected boolean isOnGround;
    protected boolean isOnGroundPrevTick;
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

    protected ConvexShape createDefaultGhostShape() {
        return new CapsuleCollisionShape(1.0f, 2.0f, 1);
    }

    protected ConvexShape createDefaultPhysicsShape() {
        return new CapsuleCollisionShape((1.0f) + 0.01f, (2.0f) - 0.01f, 1);
    }

    protected ConvexShape createDefaultGhostShapeShapeForGroundCheck() {
        return new CapsuleCollisionShape((1.0f) - 0.01f, (2.0f) - 0.01f, 1);
    }

    public void setCapsuleShape(float radius, float height) {
        this.ghostBody.setCollisionShape(new CapsuleCollisionShape(radius, height, 1));
        this.physicsBody.setCollisionShape(new CapsuleCollisionShape(radius + 0.01f, height - 0.01f, 1));
        this.groundCheckShape = new CapsuleCollisionShape(radius - 0.01f, height - 0.01f, 1);
    }

    public void setBoxShape(float xz, float height) {
        this.ghostBody.setCollisionShape(new BoxCollisionShape(xz, height, 1));
        this.physicsBody.setCollisionShape(new BoxCollisionShape(xz + 0.01f, height - 0.01f, 1));
        this.groundCheckShape = new BoxCollisionShape(xz - 0.01f, height - 0.01f, 1);
    }

    public ConvexShape getGroundCheckShape() {
        return this.groundCheckShape;
    }

    protected abstract Vector3f getMoveVector();

    protected void createObject() {
        this.ghostBody = new PhysicsRigidBody(this.createDefaultGhostShape());
        this.physicsBody = new PhysicsRigidBody(this.createDefaultPhysicsShape());
        this.groundCheckShape = this.createDefaultGhostShapeShapeForGroundCheck();
        this.setShapeAfterInit();
        this.setCollideWithGroups(CollisionType.WORLD);
        {
            this.getPhysicsBody().setUserObject(this);
            this.getGhostBody().setUserObject(this);
            this.getPhysicsBody().setUserIndex(this.getItemId());
            this.getGhostBody().setUserIndex(this.getItemId());
        }
        this.getGhostBody().setKinematic(true);

        this.getGhostBody().ignores(this.getPhysicsBody());
        this.getPhysicsBody().ignores(this.getGhostBody());

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

    protected void setShapeAfterInit() {
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
            if (sweepResult.hitNormal() != null) {
                if (this.checkDotAngle(this.up(), sweepResult.hitNormal(), this.getSlopeAngle(), true)) {
                    return true;
                }
            }
        }
        {
            float threshold = 0.05f;
            SweepResult sweepResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), ghostBody, this.getGroundCheckShape(), new Vector3f(fromPosition).add(0.0f, 0.01f, 0.0f), new Vector3f(0.0f, -threshold, 0.0f), new Vector3i(0, 1, 0));
            if (sweepResult.hitNormal() != null) {
                BoundingBox boundingBox = new BoundingBox();
                this.getGhostBody().boundingBox(boundingBox);
                RayResult result = RayResult.getRayHitResult(this.getWorld().getDynamics(), ghostBody, sweepResult.correctedPos(), new Vector3f(sweepResult.correctedPos()).sub(0.0f, boundingBox.getYExtent() + (this.getStepHeight() + threshold), 0.0f));
                if (result.hitNormal() != null) {
                    float y = sweepResult.correctedPos().y;
                    if (JGemsConfig.DEBUG.SHOW_DEBUG_LINES) {
                        JGemsOpenGLRenderer.DebugLinesDrawer().addRequest(DebugLinesDrawer.LineRequest(sweepResult.correctedPos(), new Vector3f(sweepResult.correctedPos()).sub(0.0f, boundingBox.getYExtent() + (this.getStepHeight() + threshold), 0.0f), new Vector3f(1.0f, 0.0f, 0.0f), DebugLinesDrawer.noDepth(), DebugLinesDrawer.noDepth()));
                    }
                    return this.checkDotAngle(this.up(), result.hitNormal(), this.getSlopeAngle(), true);
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

        if (rayResult.hitNormal() != null) {
            if (this.checkDotAngle(this.up(), rayResult.hitNormal(), this.getSlopeAngle(), true)) {
                SweepResult sweepDown = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getGhostBody(), this.getGroundCheckShape(), currPos, new Vector3f(0.0f, -maxY, 0.0f), new Vector3i(0, 1, 0));
                boolean onGround = this.checkIfOnGround(this.getGhostBody(), sweepDown.correctedPos());
                if (onGround) {
                    float deltaY = currPos.y - sweepDown.correctedPos().y;
                    if (deltaY > 0.01f && deltaY <= maxY) {
                        return new Vector3f(currPos.x, sweepDown.correctedPos().y, currPos.z);
                    }
                }
            }
        }

        return null;
    }

    private Vector3f tryStepUp(Vector3f currPos, Vector3f motion, float height) {
        final float motionSpeed = motion.length();
        Vector3f rawResult = this.tryStepInterval(currPos, motion, height, false);

        if (rawResult == null) {
            return null;
        }

        final float yGet = (rawResult.y - currPos.y) + 0.005f;
        Vector3f normalizedMotion = new Vector3f(motion.x, yGet, motion.z).normalize(motionSpeed).mul(1.0f, 0.0f, 1.0f);
        Vector3f newAttempt = this.tryStepInterval(currPos, normalizedMotion, yGet, false);

        Vector3f result = rawResult;
        if (newAttempt != null) {
            result = newAttempt;
        }

        if (result.y - currPos.y <= 0.01f) {
            return null;
        }
        return result;
    }

    private Vector3f tryStepInterval(Vector3f currPos, Vector3f motion, float height, boolean correction) {
        SweepResult sweepUp = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getGhostBody(), this.getGroundCheckShape(), currPos, new Vector3f(0.0f, height, 0.0f), new Vector3i(0, 1, 0));
        Vector3f steppedUpPos = sweepUp.correctedPos();
        final float motionS = motion.length();
        //new Vector3f(motion).add(new Vector3f(0.0f, height, 0.0f)).normalize().mul(motionS);
        Vector3f bumpMotion = new Vector3f(motion);
        Vector3f bumpCorrectedPos = steppedUpPos;
        final int bumps = 12;
        for (int i = 0; i < bumps; i++) {
            if (bumpMotion == null) {
                break;
            }
            SweepResult sweepForward = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getGhostBody(), this.getGroundCheckShape(), bumpCorrectedPos, bumpMotion, new Vector3i(1));
            if (i == 0) {
                float movedDist1 = new Vector3f(sweepForward.correctedPos()).sub(steppedUpPos).length();
                float movedDist2 = steppedUpPos.distance(currPos);
                if (movedDist1 < 0.01f || movedDist2 < 0.01f) {
                    return null;
                }
            }
            bumpCorrectedPos = sweepForward.correctedPos();
            bumpMotion = sweepForward.slideMotion();
        }

        SweepResult sweepDown = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getGhostBody(), this.getGroundCheckShape(), bumpCorrectedPos, new Vector3f(0.0f, -height, 0.0f), new Vector3i(0, 1, 0));
        Vector3f landedPos = sweepDown.correctedPos();

        //final boolean stepCheck = sweepDown.hitNormal() != null && this.checkDotAngle(this.up(), sweepDown.hitNormal(), this.getSlopeAngle(), true);

       //if (stepCheck) {
            final boolean onGround = this.checkIfOnGround(this.getGhostBody(), landedPos);
            float deltaY = landedPos.y - currPos.y;
            if (onGround && deltaY > 0.0f && deltaY <= height) {
                // ???
                if (!correction) {
                    Vector3f totalDelta = new Vector3f(landedPos).sub(currPos);
                    float totalDistance = totalDelta.length();
                    float maxDistance = motion.length();
                    if (totalDistance > maxDistance + 0.05f) {
                        totalDelta.normalize().mul(maxDistance);
                        return this.tryStepInterval(currPos, new Vector3f(totalDelta.x, motion.y, totalDelta.z), height + 0.05f, true);
                    }
                }

                return landedPos;
            }
        //}

        /*
        SweepResult sweepUp = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getGhostBody(), this.getGroundCheckShape(), currPos, new Vector3f(0.0f, height, 0.0f), new Vector3i(0, 1, 0));
        Vector3f steppedUpPos = sweepUp.correctedPos();

        SweepResult sweepForward = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getGhostBody(), this.getGroundCheckShape(), steppedUpPos, motion, new Vector3i(1));
        Vector3f forwardPos = sweepForward.correctedPos();

        float movedDist = new Vector3f(forwardPos).sub(steppedUpPos).length();
        if (movedDist < 0.01f) {
            return null;
        }

        if (sweepForward.slideMotion() != null && sweepForward.slideMotion().length() > 0.01f) {
            SweepResult sweepForwardSLIDED = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getGhostBody(), this.getGroundCheckShape(), forwardPos, sweepForward.slideMotion(), new Vector3i(1));
            if (sweepForwardSLIDED.correctedPos() != null && sweepForwardSLIDED.correctedPos().distance(forwardPos) > 0.01f) {
                forwardPos = sweepForwardSLIDED.correctedPos();
            }
        }

        SweepResult sweepDown = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getGhostBody(), this.getGroundCheckShape(), forwardPos, new Vector3f(0.0f, -height, 0.0f), new Vector3i(0, 1, 0));
        Vector3f landedPos = sweepDown.correctedPos();

        if (sweepDown.hitNormal() != null && this.checkDotAngle(this.up(), sweepDown.hitNormal(), this.getSlopeAngle(), true)) {
            boolean onGround = this.checkIfOnGround(this.getGhostBody(), landedPos);
            float deltaY = landedPos.y - currPos.y;
            if (onGround && deltaY > 0.0f && deltaY <= height) {
                return landedPos;
            }
        }
*/

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
        this.isOnGroundPrevTick = this.isOnGround;
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

    public void jump(float gravity, float height) {
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

        if (sweepResult.hitNormal() != null) {
            if (this.checkDotAngle(this.up(), sweepResult.hitNormal(), this.getSlopeAngle(), true)) {
                final float threshold = 0.01f;
                Vector3f newPos = sweepResult.correctedPos().add(0.0f, threshold, 0.0f);
                if (newPos.y - this.getPosition().y < measure) {
                    this.setPosition(newPos);
                }
            }
        }
    }

    private void moveWithCollision(float waterJumpFactor, Vector3f motion) {
        final boolean checkStep = this.isOnGround();
        if (this.isOnGroundPrevTick || checkStep) {
            Vector3f tryStepUp = this.tryStepUp(this.getPosition(), motion, this.getStepHeight());
            if (tryStepUp != null) {
                this.setPosition(new Vector3f(tryStepUp.x, tryStepUp.y + 0.01f, tryStepUp.z));
               // System.out.println("F");
                return;
            }
        }

        if (this.moveTestXYZ(motion) && !this.isInWater()) {
            if (checkStep && Math.abs(motion.y) <= 0.01f) {
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
            correctedPos = result.first();
            slideVec = result.second();
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
        if (sweepTest.hitNormal() == null) {
            return new Pair<>(checkTo, null);
        }

        return new Pair<>(sweepTest.correctedPos(), sweepTest.slideMotion());
    }

    public int getCollisionGroup() {
        return this.getGhostBody().getCollisionGroup();
    }

    public void setCollisionGroup(CollisionType... collisionTypes) {
        int i = 0;
        for (CollisionType collisionType : collisionTypes) {
            i |= collisionType.mask();
        }
        this.getGhostBody().setCollisionGroup(i);
        this.getPhysicsBody().setCollisionGroup(i);
    }

    public int getCollisideWithGroups() {
        return this.getGhostBody().getCollideWithGroups();
    }

    public void setCollideWithGroups(CollisionType... collisionTypes) {
        int i = 0;
        for (CollisionType collisionType : collisionTypes) {
            i |= collisionType.mask();
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
        synchronized (transformLock) {
            return DynamicsUtils.getObjectBodyPos(this.getGhostBody());
        }
    }

    @Override
    public void setPosition(Vector3f vector3d) {
        synchronized (transformLock) {
            this.getGhostBody().setPhysicsLocation(DynamicsUtils.convertV3F_JME(vector3d));
        }
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

    private record RayResult(Vector3f correctedPos, Vector3f hitNormal, float hitFraction) {

        public static RayResult getRayHitResult(DynamicsSystem dynamicsSystem, PhysicsRigidBody ghostObject, Vector3f posFrom, Vector3f posTo) {
                List<PhysicsRayTestResult> rayTest = dynamicsSystem.getPhysicsSpace().rayTest(DynamicsUtils.convertV3F_JME(posFrom), DynamicsUtils.convertV3F_JME(posTo));
                rayTest.removeIf(e -> {
                    if (e.getCollisionObject().equals(ghostObject)) {
                        return true;
                    }
                    if ((e.getCollisionObject().getCollisionGroup() & CollisionType.PLAYER.mask()) != 0) {
                        return true;
                    }
                    if ((e.getCollisionObject().collisionFlags() & CollisionFlag.NO_CONTACT_RESPONSE) != 0) {
                        return true;
                    }
                    return (e.getCollisionObject().getCollisionGroup() & ghostObject.getCollideWithGroups()) == 0 || (e.getCollisionObject().getCollideWithGroups() & ghostObject.getCollisionGroup()) == 0;
                });
                if (!rayTest.isEmpty()) {
                    PhysicsRayTestResult physicsSweepTestResult1 = rayTest.getFirst();
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

                    return new RayResult(corrected, inNormal, distanceToHit);
                }
                return new RayResult(null, null, -1.0f);
            }
        }

    private record SweepResult(Vector3f correctedPos, Vector3f hitNormal, Vector3f slideMotion, float hitFraction) {
            private static final List<PhysicsRayTestResult> results = new ArrayList<>();

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
                    if ((e.getCollisionObject().getCollisionGroup() & CollisionType.PLAYER.mask()) != 0) {
                        return true;
                    }
                    if ((e.getCollisionObject().collisionFlags() & CollisionFlag.NO_CONTACT_RESPONSE) != 0) {
                        return true;
                    }
                    return (e.getCollisionObject().getCollisionGroup() & ghostObject.getCollideWithGroups()) == 0 || (e.getCollisionObject().getCollideWithGroups() & ghostObject.getCollisionGroup()) == 0;
                });
                sweepTestResultList.sort(Comparator.comparingDouble(PhysicsSweepTestResult::getHitFraction));
                if (!sweepTestResultList.isEmpty()) {
                    PhysicsSweepTestResult physicsSweepTestResult1 = sweepTestResultList.getFirst();
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
                    Vector3f motionDir = motionLen > 1e-6f ? new Vector3f(motion).normalize() : new Vector3f(0f, 0f, 0f);
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
        }
}
