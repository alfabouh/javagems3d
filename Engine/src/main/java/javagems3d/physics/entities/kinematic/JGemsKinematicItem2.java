package javagems3d.physics.entities.kinematic;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.CollisionFlag;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.PhysicsSweepTestResult;
import com.jme3.bullet.collision.shapes.ConvexShape;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.bullet.joints.JointEnd;
import com.jme3.bullet.joints.Point2PointJoint;
import com.jme3.bullet.joints.SliderJoint;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Transform;
import javagems3d.JGemsHelper;
import javagems3d.physics.entities.IBtEntity;
import javagems3d.physics.entities.properties.collision.CollisionFilter;
import javagems3d.physics.entities.properties.state.EntityState;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.IWorldTicked;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.physics.world.thread.PhysicsThread;
import javagems3d.physics.world.thread.dynamics.DynamicsSystem;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class JGemsKinematicItem2 extends WorldItem implements IWorldTicked, IBtEntity {
    private final Vector3f bodyVelocity;
    private PhysicsRigidBody physicsRigidBody;
    protected boolean isOnGround;
    private EntityState entityState;

    protected int jumpCooldownR;
    private int jumpCooldown;
    private float gravity;
    private float walkSpeed;
    private float jumpHeight;
    private double slopeAngle;
    private float stepHeight;
    private float linearVelDamping;

    public JGemsKinematicItem2(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scaling, String itemName) {
        super(world, pos, rot, scaling, itemName);
        this.bodyVelocity = new Vector3f();
    }

    public JGemsKinematicItem2(PhysicsWorld world, Vector3f pos, Vector3f rot, String itemName) {
        this(world, pos, rot, new Vector3f(1.0f), itemName);
    }

    public JGemsKinematicItem2(PhysicsWorld world, Vector3f pos, String itemName) {
        this(world, pos, new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    public JGemsKinematicItem2(PhysicsWorld world, String itemName) {
        this(world, new Vector3f(0.0f), new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    protected abstract ConvexShape createShape();
    protected abstract Vector3f getMoveVector();

    protected void createGhostObject() {
        this.physicsRigidBody = new PhysicsRigidBody(this.createShape());
        this.setCollisionGroup(CollisionFilter.PLAYER);
        this.setCollisionFilter(CollisionFilter.ALL);
        this.physicsRigidBody.setUserObject(this);
    }

    protected void setDefaults() {
        this.setGravity(-9.8f);
        this.setJumpHeight(1.0f);
        this.setWalkSpeed(0.25f);
        this.setSlopeAngle(Math.toRadians(10.0f));
        this.setStepHeight(0.25f);
        this.setLinearVelDamping(0.7f);
        this.setJumpCooldown(PhysicsThread.TICKS_PER_SECOND / 2);
    }

    protected float gravityDiv() {
        return 800.0f;
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        this.setDefaults();
        this.createGhostObject();
        this.createEntityState();

        this.getPhysicsRigidBody().setProtectGravity(true);
        this.getPhysicsRigidBody().setMass(0.5f);
        this.getPhysicsRigidBody().setFriction(0.0f);
        this.getPhysicsRigidBody().setLinearDamping(0.95f);

        this.getPhysicsRigidBody().setAngularFactor(0.0f);

        ((PhysicsWorld) iWorld).getDynamics().addCollisionObject(this.getPhysicsRigidBody());
    }

    protected void createEntityState() {
        this.entityState = new EntityState();
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        super.onDestroy(iWorld);
        ((PhysicsWorld) iWorld).getDynamics().removeCollisionObject(this.getPhysicsRigidBody());
    }

    private boolean checkIfOnGround(PhysicsRigidBody PhysicsRigidBody) {
        SweepResult sweepResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), PhysicsRigidBody, (ConvexShape) this.getPhysicsRigidBody().getCollisionShape(), this.getPosition().add(0.0f, 0.01f, 0.0f), new Vector3f(0.0f, -0.1f, 0.0f), new Vector3i(0, 1, 0));
        if (sweepResult.getHitNormal() != null) {
            return this.checkDotAngle(this.up(), sweepResult.getHitNormal(), this.getSlopeAngle(), true);
        }
        //SweepResult sweepResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), PhysicsRigidBody, (ConvexShape) this.getPhysicsRigidBody().getCollisionShape(), this.getPosition().add(0.0f, 0.01f, 0.0f), new Vector3f(0.0f, -0.1f, 0.0f), new Vector3i(0, 1, 0));
        //if (sweepResult.getHitNormal() != null) {
        //    BoundingBox boundingBox = new BoundingBox();
        //    this.getPhysicsRigidBody().boundingBox(boundingBox);
        //    RayResult result = RayResult.getRayHitResult(this.getWorld().getDynamics(), PhysicsRigidBody, sweepResult.getCorrectedPos(), new Vector3f(sweepResult.getCorrectedPos()).sub(0.0f, boundingBox.getYExtent() * 2.0f + this.getStepHeight(), 0.0f));
        //    if (result.getHitNormal() != null) {
        //        return this.checkDotAngle(this.up(), result.getHitNormal(), this.getSlopeAngle(), true);
        //    }
        //}
        return false;
    }

    private boolean checkAngleOnPos(PhysicsRigidBody PhysicsRigidBody, double toCheckAngle, Vector3f pos, Vector3f vector, boolean ifNoHit) {
        SweepResult sweepResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), PhysicsRigidBody, (ConvexShape) this.getPhysicsRigidBody().getCollisionShape(), pos, vector, new Vector3i(0, 1, 0));
        if (sweepResult.getHitNormal() != null) {
            float dot = sweepResult.getHitNormal().dot(this.up());
            if (dot >= 0.999f) {
                return ifNoHit;
            }
            return Math.acos(dot) <= toCheckAngle;
        }
        return ifNoHit;
    }

    //private float getStepDownY() {
    //    float maxY = (float) Math.max(Math.sin(this.getSlopeAngle()), this.getStepHeight());
    //    SweepResult sweepResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsRigidBody(), (ConvexShape) this.getPhysicsRigidBody().getCollisionShape(), this.getPosition(), new Vector3f(0.0f, -maxY, 0.0f), new Vector3i(0, 1, 0));
    //    if (sweepResult.getHitNormal() != null) {
    //        if (this.checkDotAngle(this.up(), sweepResult.getHitNormal(), this.getSlopeAngle(), true)) {
    //            float y = sweepResult.getCorrectedPos().y;
    //            //System.out.println(this.getPosition().y - y);
    //            if (y < (this.getPosition().y - 0.01f)) {
    //                return y;
    //            }
    //        }
    //    }
    //    return 0.0f;
    //}

    private boolean checkDotAngle(Vector3f v1, Vector3f v2, double angle, boolean ifOrthogonal) {
        float dot = new Vector3f(v1).dot(v2);
        if (dot == 1.0f) {
            return ifOrthogonal;
        }
        return Math.acos(dot) <= angle;
    }

    //private Vector3f tryStepUp(Vector3f currPos, Vector3f motion, float height) {
    //    SweepResult sweepResultUp = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsRigidBody(), (ConvexShape) this.getPhysicsRigidBody().getCollisionShape(), currPos, new Vector3f(0.0f, height, 0.0f), new Vector3i(0, 1, 0));
    //    Vector3f getHitUp = sweepResultUp.getCorrectedPos();
//
    //    SweepResult sweepResultStep = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsRigidBody(), (ConvexShape) this.getPhysicsRigidBody().getCollisionShape(), getHitUp, motion, new Vector3i(1));
    //    Vector3f getHitStep = sweepResultStep.getCorrectedPos();
//
    //    BoundingBox boundingBox = new BoundingBox();
    //    this.getPhysicsRigidBody().boundingBox(boundingBox);
//
    //    //RayResult result = RayResult.getRayHitResult(this.getWorld().getDynamics(), this.getPhysicsRigidBody(), getHitStep, new Vector3f(getHitStep).sub(0.0f, boundingBox.getYExtent() * 2.0f + height, 0.0f));
    //    //if (result.getHitNormal() != null) {
    //    //    if (this.checkDotAngle(this.up(), result.getHitNormal(), this.getSlopeAngle(), true)) {
    //    //        SweepResult sweepResultDown = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsRigidBody(), (ConvexShape) this.getPhysicsRigidBody().getCollisionShape(), getHitStep, new Vector3f(0.0f, -height, 0.0f), new Vector3i(0, 1, 0));
    //    //        if (sweepResultDown.getCorrectedPos().y > currPos.y) {
    //    //        return sweepResultDown.getCorrectedPos();
    //    //        }
    //    //    }
    //    //}
//
    //    SweepResult sweepResultDown = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsRigidBody(), (ConvexShape) this.getPhysicsRigidBody().getCollisionShape(), getHitStep, new Vector3f(0.0f, -height, 0.0f), new Vector3i(0, 1, 0));
    //    if (sweepResultDown.getHitNormal() != null && sweepResultDown.getCorrectedPos().y > currPos.y) {
    //        if (this.checkDotAngle(this.up(), sweepResultDown.getHitNormal(), this.getSlopeAngle(), true)) {
    //            return sweepResultDown.getCorrectedPos();
    //        }
    //    }
//
    //    return null;
    //}
//
    //private float checkSlopeY(Vector3f checkFrom, Vector3f motion) {
    //    SweepResult sweepResult0 = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsRigidBody(), (ConvexShape) this.getPhysicsRigidBody().getCollisionShape(), checkFrom, motion, new Vector3i(1));
//
    //    if (sweepResult0.getSlideMotion() != null) {
    //        float y = sweepResult0.getSlideMotion().y;
    //        if (y > 0.0f) {
    //            boolean flag = this.checkAngleOnPos(this.getPhysicsRigidBody(), this.getSlopeAngle(), sweepResult0.getCorrectedPos().add(sweepResult0.getSlideMotion().mul(0.01f, 1.0f, 0.01f)), new Vector3f(0.0f, -1.0f, 0.0f), false);
    //            if (flag) {
    //                return y;
    //            }
    //        }
    //    }
//
    //    return -1.0f;
    //}

    private float getLiquidSwimUpFactor() {
        Vector3f contactPoint = new Vector3f(0.0f);

        BoundingBox boundingBox = new BoundingBox();
        this.getPhysicsRigidBody().boundingBox(boundingBox);

        this.getWorld().getDynamics().getPhysicsSpace().contactTest(this.getPhysicsRigidBody(), (e) -> {
            PhysicsCollisionObject collisionObjectB = e.getObjectB();
            if (CollisionFilter.LIQUID.matchMask(collisionObjectB.getCollisionGroup())) {
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
                if (CollisionFilter.LIQUID.matchMask(physicsRayTestResult.getCollisionObject().getCollisionGroup())) {
                    return -1.0f;
                }
            }
        }

        com.jme3.math.Vector3f rayFrom = DynamicsUtils.createV3F_JME(contactPoint.x, this.getPosition().y + boundingBox.getYExtent(), contactPoint.z);
        com.jme3.math.Vector3f rayTo = DynamicsUtils.createV3F_JME(contactPoint.x, this.getPosition().y, contactPoint.z);
        List<PhysicsRayTestResult> rayTestResults = this.getWorld().getDynamics().getPhysicsSpace().rayTestRaw(rayFrom, rayTo);
        for (PhysicsRayTestResult physicsRayTestResult : rayTestResults) {
            if (CollisionFilter.LIQUID.matchMask(physicsRayTestResult.getCollisionObject().getCollisionGroup())) {
                com.jme3.math.Vector3f hitP = DynamicsUtils.lerp(rayFrom, rayTo, physicsRayTestResult.getHitFraction());
                float f1 = rayFrom.distance(rayTo);
                float f2 = rayFrom.distance(hitP);
                //Math.pow(1.0f - (f2 / f1), 0.5f)
                return 1.0f - (f2 / f1);
            }
        }

        return 1.0f;
    }
    
    @Override
    public final void onUpdate(IWorld iWorld) {
        this.onTick(iWorld);
        this.getEntityState().removeState(EntityState.Type.IN_LIQUID);

        this.getPhysicsRigidBody().setKinematic(false);
        this.getPhysicsRigidBody().setLinearDamping(1.0f - 1.0e-4f);
        this.getPhysicsRigidBody().setContactDamping(0.1f);

       // this.getPhysicsRigidBody().setContactProcessingThreshold(1.0e+1f);
       // this.getPhysicsRigidBody().setContactStiffness(1.0e+30f);
//
       // this.getPhysicsRigidBody().setCcdMotionThreshold(0.00001f);
       // this.getPhysicsRigidBody().setCcdSweptSphereRadius(0.01f);
       // this.getPhysicsRigidBody().setContactDamping(0.0f);
       // this.getPhysicsRigidBody().getCollisionShape().setMargin(0.1f);

        //this.getPhysicsRigidBody().setContactStiffness(1.0e+30f);
       // this.getPhysicsRigidBody().setContactProcessingThreshold(1.0e-10f);
       this.getPhysicsRigidBody().getCollisionShape().setMargin(0.1f);
       // this.getPhysicsRigidBody().setCcdMotionThreshold(1.0e-3f);
       // this.getPhysicsRigidBody().setCcdSweptSphereRadius(0.1f);

        // this.getPhysicsRigidBody().setFriction(0.0f);
        this.getPhysicsRigidBody().setMass(0.1f);

        System.out.println(DynamicsUtils.convertV3F_JOML(this.getPhysicsRigidBody().getLinearVelocity(new com.jme3.math.Vector3f())).length() / 40.0f);

        //this.getPhysicsRigidBody().setRestitution(0.0f);
        //this.getPhysicsRigidBody().getCollisionShape().setMargin(0.05f);

        this.getPhysicsRigidBody().setCcdMotionThreshold(0.01f);
        this.getPhysicsRigidBody().setCcdSweptSphereRadius(0.01f);
    }

    protected void onTick(IWorld iWorld) {
        if (this.getPosition().y < -50.0f) {
            this.setPosition(this.startPos);
        }

        this.jumpCooldownR -= 1;
        this.isOnGround = this.checkIfOnGround(this.getPhysicsRigidBody());

        final float walkSpeed = this.getWalkSpeed();
        final float jumpSpeed = this.getJumpHeight();
        final float damping = this.getLinearVelDamping();

        Vector3f motion = this.getMoveVector();

        this.linearDampingXZ(damping);
        this.walkVelocity(motion, damping, walkSpeed);
        this.tryToJump(this.getGravity(), motion, jumpSpeed);
        this.gravityVelocity(this.getGravity());

        JGemsHelper.UTILS.clampVectorToZeroThreshold(this.bodyVelocity, 0.001f);

        this.move(this.getBodyVelocity());
        //this.move(new Vector3f(this.getMoveVector()).mul(0.25f));
    }

    protected void tryToJump(float gravity, Vector3f motionVecController, float jumpHeight) {
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
                }
            } else if (this.isOnGround()) {
                this.jump(gravity, jumpHeight);
            }
        }
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

    protected void move(Vector3f motion) {
        this.getPhysicsRigidBody().applyCentralImpulse(new com.jme3.math.Vector3f(motion.x, motion.y, motion.z).mult(1.0f));
        if (motion.length() > 0) {
            //this.moveWithCollision(motion);
        }
    }

    //private void moveWithCollision(Vector3f motion) {
    //    float ySlope = this.checkSlopeY(this.getPosition(), motion);
    //    if (ySlope <= 0.0f) {
    //        if (this.isOnGround() && motion.y == 0.0f) {
    //            Vector3f tryStepUp = this.tryStepUp(this.getPosition(), motion, this.getStepHeight());
    //            if (tryStepUp != null) {
    //               this.setPosition(tryStepUp);
    //               return;
    //            }
    //        }
    //    }
//
    //    boolean f1 = !this.moveTestVerticalY(motion, ySlope);
    //    boolean f2 = !this.moveTestHorizontalXZ(motion, ySlope);
    //    if (f1 && f2) {
    //        if (motion.y <= 0.0f) {
    //            float yDown = this.getStepDownY();
    //            if (this.isOnGround() && yDown != 0.0f) {
    //                this.setPosition(new Vector3f(this.getPosition().x, yDown + 0.01f, this.getPosition().z));
    //            }
    //        }
    //    }
    //}

   //public boolean moveTestVerticalY(Vector3f motion, final float ySlope) {
   //    Vector3f slide = new Vector3f(0.0f);
   //    Vector3f checkTo = new Vector3f(motion).mul(0.0f, 1.0f, 0.0f);

   //    if (ySlope > 0.0f) {
   //        checkTo.y += ySlope;
   //    }

   //    SweepResult slideResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsRigidBody(), (ConvexShape) this.getPhysicsRigidBody().getCollisionShape(), this.getPosition(), checkTo, new Vector3i(0, 1, 0));
   //    if (slideResult.getSlideMotion() != null) {
   //        slide = slideResult.getSlideMotion();
   //    }

   //    if (slide.length() > 0.0f && slide.length() < 0.001f) {
   //        slide.normalize().mul(0.001f);
   //    }

   //    SweepResult slideResult2 = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsRigidBody(), (ConvexShape) this.getPhysicsRigidBody().getCollisionShape(), this.getPosition(), new Vector3f(slide), new Vector3i(1));
   //    if (slideResult2.getHitNormal() != null) {
   //        Vector3f corrPosSlide = new Vector3f(slideResult2.getCorrectedPos());
   //        this.setPosition(new Vector3f(corrPosSlide.x, corrPosSlide.y, corrPosSlide.z));

   //        SweepResult slideResultInner = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsRigidBody(), (ConvexShape) this.getPhysicsRigidBody().getCollisionShape(), this.getPosition(), new Vector3f(slideResult2.getSlideMotion().x, 0.0f, slideResult2.getSlideMotion().z), new Vector3i(1));
   //        this.setPosition(slideResultInner.getCorrectedPos());
   //    } else {
   //        Vector3f corrPosSweep = new Vector3f(slideResult.getCorrectedPos());
   //        float ySpeed = checkTo.length();
   //        float lenCor = ySpeed == 0.0f ? 1.0f : 1.0f - Math.min(corrPosSweep.distance(this.getPosition()) / Math.abs(ySpeed), 1.0f);
   //        slide.mul(lenCor);
   //        corrPosSweep.add(slide);
   //        this.setPosition(corrPosSweep);
   //    }

   //    return ySlope > 0.0f;
   //}

   //public boolean moveTestHorizontalXZ(Vector3f motion, final float ySlope) {
   //    boolean flag = false;
   //    Vector3f slide = new Vector3f(0.0f);
   //    Vector3f checkTo = new Vector3f(motion).mul(1.0f, 0.0f, 1.0f);

   //    SweepResult slideResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsRigidBody(), (ConvexShape) this.getPhysicsRigidBody().getCollisionShape(), this.getPosition(), checkTo, new Vector3i(1, 0, 1), ySlope > 0.0f ? null : new Vector3f(1.0f, 0.0f, 1.0f));
   //    if (slideResult.getSlideMotion() != null) {
   //        slide = slideResult.getSlideMotion().mul(1.0f, 0.0f, 1.0f);
   //    }

   //    if (slide.length() > 0.0f && slide.length() < 0.001f) {
   //        slide.normalize().mul(0.001f);
   //    }

   //    float xzSpeed = Math.max(checkTo.length(), this.getWalkSpeed());
   //    Vector3f corrPosSweep = new Vector3f(slideResult.getCorrectedPos());
   //    float x0 = corrPosSweep.x;
   //    float y0 = Math.min(this.getPosition().y, corrPosSweep.y);
   //    float z0 = corrPosSweep.z;
   //    Vector3f corrected = new Vector3f(x0, y0, z0);
   //    float lenCor = xzSpeed == 0.0f ? 1.0f : 1.0f - Math.min(corrected.distance(this.getPosition()) / xzSpeed, 1.0f);
   //    slide.mul(lenCor);

   //    SweepResult slideResult2 = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsRigidBody(), (ConvexShape) this.getPhysicsRigidBody().getCollisionShape(), new Vector3f(corrected), new Vector3f(slide), new Vector3i(1, 0, 1));
   //    if (slide.length() > 0.0f && slideResult2.getHitNormal() != null) {
   //        Vector3f corrPosSlide = new Vector3f(slideResult2.getCorrectedPos().x, Math.min(slideResult2.getCorrectedPos().y, this.getPosition().y), slideResult2.getCorrectedPos().z);

   //        if (slideResult2.getSlideMotion().y > 0.0f) {
   //            if (this.checkAngleOnPos(this.getPhysicsRigidBody(), this.getSlopeAngle(), new Vector3f(corrPosSlide).add(0.0f, slideResult2.getSlideMotion().y, 0.0f), new Vector3f(0.0f, -0.1f, 0.0f), false)) {
   //                SweepResult slideResultInner = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsRigidBody(), (ConvexShape) this.getPhysicsRigidBody().getCollisionShape(), new Vector3f(corrPosSlide), new Vector3f(0.0f, slideResult2.getSlideMotion().y, 0.0f), new Vector3i(1));
   //                corrPosSlide = slideResultInner.getCorrectedPos();
   //                flag = true;
   //            }
   //        }

   //        this.setPosition(corrPosSlide);
   //    } else {
   //        this.setPosition(new Vector3f(corrected).add(slide));
   //    }

   //    return flag;
   //}

    public int getCollisionGroup() {
        return this.getPhysicsRigidBody().getCollisionGroup();
    }

    public void setCollisionGroup(CollisionFilter... collisionFilters) {
        int i = 0;
        for (CollisionFilter collisionFilter : collisionFilters) {
            i |= collisionFilter.getMask();
        }
        this.getPhysicsRigidBody().setCollisionGroup(i);
    }

    public int getCollisionFilter() {
        return this.getPhysicsRigidBody().getCollideWithGroups();
    }

    public void setCollisionFilter(CollisionFilter... collisionFilters) {
        int i = 0;
        for (CollisionFilter collisionFilter : collisionFilters) {
            i |= collisionFilter.getMask();
        }
        this.getPhysicsRigidBody().setCollideWithGroups(i);
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
        return DynamicsUtils.getObjectBodyPos(this.getPhysicsRigidBody());
    }

    @Override
    public void setPosition(Vector3f vector3d) {
        this.getPhysicsRigidBody().setPhysicsLocation(DynamicsUtils.convertV3F_JME(vector3d));
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

    public PhysicsRigidBody getPhysicsRigidBody() {
        return this.physicsRigidBody;
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

        public static JGemsKinematicItem2.RayResult getRayHitResult(DynamicsSystem dynamicsSystem, PhysicsRigidBody ghostObject, Vector3f posFrom, Vector3f posTo) {
            List<PhysicsRayTestResult> rayTest = dynamicsSystem.getPhysicsSpace().rayTest(DynamicsUtils.convertV3F_JME(posFrom), DynamicsUtils.convertV3F_JME(posTo));
            rayTest.removeIf(e -> {
                if (e.getCollisionObject().equals(ghostObject)) {
                    return true;
                }
                if ((e.getCollisionObject().collisionFlags() & CollisionFlag.NO_CONTACT_RESPONSE) != 0) {
                    return true;
                }
                if ((ghostObject.getCollisionGroup() & e.getCollisionObject().getCollideWithGroups()) == 0) {
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
                JGemsHelper.UTILS.clampVectorToZeroThreshold(inNormal, 0.001f);
                if (inNormal.length() > 0f) {
                    inNormal.normalize();
                }

                return new JGemsKinematicItem2.RayResult(corrected, inNormal, distanceToHit);
            }
            return new JGemsKinematicItem2.RayResult(null, null, -1.0f);
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

        private SweepResult(Vector3f correctedPos, Vector3f hitNormal, Vector3f slideMotion, float hitFraction) {
            this.correctedPos = correctedPos;
            this.hitNormal = hitNormal;
            this.slideMotion = slideMotion;
            this.hitFraction = hitFraction;
        }

        public static SweepResult getSweepHitResult(DynamicsSystem dynamicsSystem, PhysicsRigidBody ghostObject, ConvexShape convexShape, Vector3f posFrom, Vector3f motion, Vector3i axis, Vector3f slideNormalCorrection, float ccd) {
           // motion.mul(0.15f, 1f, 0.15f);
            Vector3f nmAxisM = new Vector3f(motion).mul(new Vector3f(axis));
            Vector3f moveTo1 = new Vector3f(posFrom).add(nmAxisM);
            Transform start = new Transform().setTranslation(DynamicsUtils.convertV3F_JME(posFrom));
            Transform end = new Transform().setTranslation(DynamicsUtils.convertV3F_JME(moveTo1));

            List<PhysicsSweepTestResult> sweepTestResultList = dynamicsSystem.getPhysicsSpace().sweepTest(convexShape, start, end, new ArrayList<>(), ccd);

            sweepTestResultList.removeIf(e -> {
                if (e.getCollisionObject().equals(ghostObject)) {
                    return true;
                }
                if ((e.getCollisionObject().collisionFlags() & CollisionFlag.NO_CONTACT_RESPONSE) != 0) {
                    return true;
                }
                if ((ghostObject.getCollisionGroup() & e.getCollisionObject().getCollideWithGroups()) == 0) {
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

                JGemsHelper.UTILS.clampVectorToZeroThreshold(normal, 0.001f);
                if (normal.length() > 0f) {
                    normal.normalize();
                }

                Vector3f motionAlongNormal = new Vector3f(normal).mul(new Vector3f(motion).dot(normal));
                Vector3f slideMotion = new Vector3f(motion).sub(motionAlongNormal);

                if (!slideMotion.isFinite()) {
                    slideMotion.set(0.0f);
                }

                float offset = Math.min(nmAxisM.length() * 0.1f, 0.01f);
                corrected.add(new Vector3f(normal).mul(offset));

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
