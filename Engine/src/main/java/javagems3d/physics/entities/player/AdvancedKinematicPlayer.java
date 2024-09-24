package javagems3d.physics.entities.player;

import com.jme3.bullet.collision.CollisionFlag;
import com.jme3.bullet.collision.PhysicsSweepTestResult;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.ConvexShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.math.Transform;
import javagems3d.physics.entities.properties.collision.CollisionFilter;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.IWorldTicked;
import javagems3d.physics.world.thread.dynamics.DynamicsSystem;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import javagems3d.system.inventory.IInventoryOwner;
import javagems3d.system.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.sun.tools.internal.xjc.reader.Ring.add;

public class AdvancedKinematicPlayer extends Player implements IInventoryOwner, IWorldTicked {
    private Inventory inventory;
    private PhysicsGhostObject physicsGhostObject;
    protected float gravity;
    protected float localGravityVelocity;
    protected Vector3f localVelocity;
    protected boolean isOnGround;

    public AdvancedKinematicPlayer(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot) {
        super(world, pos, rot, "player_sp");
        this.createInventory();
        this.localVelocity = new Vector3f(0.0f);
        this.gravity = -10.0f;
        this.localGravityVelocity = 0.0f;
    }

    @Override
    protected void createPlayer(PhysicsWorld world) {
        this.changeYStartPos();
        this.physicsGhostObject = new PhysicsGhostObject(this.createShape(this.capsuleSize(), 0.0f));
        this.physicsGhostObject.setCollisionGroup(CollisionFilter.PLAYER.getMask());
        this.physicsGhostObject.setCollideWithGroups(CollisionFilter.ALL.getMask());
    }

    private ConvexShape createShape(Vector2f size, float threshold) {
        return new CylinderCollisionShape(size.x - threshold, size.y, 1);
    }

    protected void changeYStartPos() {
        this.startPos.y += this.height();
    }

    protected Vector2f capsuleSize() {
        return new Vector2f(0.3f, 1.8f);
    }

    protected void createInventory() {
        this.inventory = new Inventory(this, 4);
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        ((PhysicsWorld) iWorld).getDynamics().addCollisionObject(this.getPhysicsGhostObject());
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        super.onDestroy(iWorld);
        ((PhysicsWorld) iWorld).getDynamics().removeCollisionObject(this.getPhysicsGhostObject());
    }

    protected void move(Vector3f dir, Vector3f speed) {
        if (dir.length() > 0) {
            Vector3f walkDir = dir.normalize().mul(1.0f, 1.0f, 1.0f);
            this.moveWithCollision(walkDir, speed);
        }
    }

    @Override
    public Vector3f getPosition() {
        return DynamicsUtils.getObjectBodyPos(this.getPhysicsGhostObject());
    }

    @Override
    public void setPosition(Vector3f vector3d) {
        this.getPhysicsGhostObject().setPhysicsLocation(DynamicsUtils.convertV3F_JME(vector3d));
    }

    private boolean checkIfOnGround(PhysicsGhostObject physicsGhostObject) {
        SweepResult sweepResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), physicsGhostObject, (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), this.getPosition(), new Vector3f(0.0f, -0.1f, 0.0f), new Vector3i(0, 1, 0));
        if (sweepResult.getHitNormal() != null) {
            return this.checkDotAngle(this.up(), sweepResult.getHitNormal(), this.stepAngle(), true);
        }
        return false;
    }

    private boolean checkAngleOnPos(PhysicsGhostObject physicsGhostObject, float toCheckAngle, Vector3f pos, Vector3f vector, boolean ifNoHit) {
        SweepResult sweepResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), physicsGhostObject, (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), pos, vector, new Vector3i(0, 1, 0));
        if (sweepResult.getHitNormal() != null) {
            float dot = sweepResult.getHitNormal().dot(this.up());
            if (dot >= 0.999f) {
                return ifNoHit;
            }
            return (float) Math.acos(dot) <= toCheckAngle;
        }
        return ifNoHit;
    }

    private float getSlopeDownY() {
        SweepResult sweepResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), this.getPosition(), new Vector3f(0.0f, (float) -Math.sin(this.stepAngle()), 0.0f), new Vector3i(0, 1, 0));
        if (sweepResult.getHitNormal() != null) {
            if (this.checkDotAngle(this.up(), sweepResult.getHitNormal(), this.stepAngle(), true)) {
                float y = sweepResult.getCorrectedPos().y;
                if (y < this.getPosition().y) {
                    return y;
                }
            }
        }
        return 0.0f;
    }

    private boolean checkDotAngle(Vector3f v1, Vector3f v2, float angle, boolean ifOrthogonal) {
        float dot = new Vector3f(v1).dot(v2);
        if (dot == 1.0f) {
            return ifOrthogonal;
        }
        return Math.acos(dot) <= angle;
    }

    private Vector3f tryStepUp(Vector3f currPos, Vector3f motion, Vector3f speed, float height) {
        SweepResult sweepResultUp = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), currPos, new Vector3f(0.0f, height, 0.0f), new Vector3i(0, 1, 0));
        Vector3f getHitUp = sweepResultUp.getCorrectedPos();

        Vector3f motVec = new Vector3f(motion).mul(speed);
        SweepResult sweepResultStep = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), getHitUp, motVec, new Vector3i(1));
        Vector3f getHitStep = sweepResultStep.getCorrectedPos();

        SweepResult sweepResultDown = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), getHitStep, new Vector3f(0.0f, -height, 0.0f), new Vector3i(0, 1, 0));
        if (sweepResultDown.getHitNormal() != null && sweepResultDown.getCorrectedPos().y > currPos.y) {
            float Deg5 = (float) Math.toRadians(5.0f);
            if (this.checkDotAngle(this.up(), sweepResultDown.getHitNormal(), Deg5, true)) {
                return sweepResultDown.getCorrectedPos();
            }
        }

        return null;
    }

    private Vector3f tryStepDown(Vector3f currPos, Vector3f motion, Vector3f speed, float height) {
        Vector3f motVec = new Vector3f(motion).mul(speed);

        SweepResult sweepResultUp = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), currPos, motVec, new Vector3i(1));
        Vector3f getHitStep = sweepResultUp.getCorrectedPos();

        SweepResult sweepResultDown = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), getHitStep, new Vector3f(0.0f, -height, 0.0f), new Vector3i(0, 1, 0));
        if (sweepResultDown.getHitNormal() != null && sweepResultDown.getCorrectedPos().y > currPos.y) {
            float Deg5 = (float) Math.toRadians(5.0f);
            if (this.checkDotAngle(this.up(), sweepResultDown.getHitNormal(), Deg5, true)) {
                return sweepResultDown.getCorrectedPos();
            }
        }

        return null;
    }

    private float checkSlopeY(Vector3f checkFrom, Vector3f motion, Vector3f speed) {
        SweepResult sweepResult0 = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), checkFrom, new Vector3f(motion).mul(speed), new Vector3i(1));
        if (motion.length() != 0.0f) {
            motion.normalize().mul(speed);
        }

        if (sweepResult0.getSlideMotion() != null) {
            float y = sweepResult0.getSlideMotion().y;
            if (y > 0.0f) {
                if (this.checkAngleOnPos(physicsGhostObject, this.stepAngle(), sweepResult0.getCorrectedPos().add(sweepResult0.getSlideMotion()), new Vector3f(0.0f, -1.0f, 0.0f), false)) {
                    return y;
                }
            }
        }

        return -1.0f;
    }

    @Override
    public final void onUpdate(IWorld iWorld) {
        this.onTick(iWorld);
    }

    protected void onTick(IWorld iWorld) {
        boolean oldGrValue = this.isOnGround;
        this.isOnGround = this.checkIfOnGround(this.getPhysicsGhostObject());

        final float speed = this.getWalkSpeed();
        final float jumpSpeed = this.getJumpSpeed();

        Vector3f motion = this.calcControllerMotion();

        if (this.isOnGround()) {
            this.localGravityVelocity = 0.0f;
            if (motion.y > 0) {
                this.localGravityVelocity = jumpSpeed;
            }
        } else {
            this.localGravityVelocity += this.gravity / 800.0f;
        }

        this.localVelocity = new Vector3f(0.0f, this.localGravityVelocity, 0.0f);
        Vector3f localVelocityMotion = this.localVelocity.length() == 0.0f ? new Vector3f(0.0f) : new Vector3f(this.localVelocity).normalize();

        Vector3f finalVelocityMotion = motion.mul(1, 0, 1).add(localVelocityMotion);
        Vector3f finalSpeed = new Vector3f(speed, 0.0f, speed).add(this.localVelocity.absolute());

        this.move(finalVelocityMotion, finalSpeed);
        //this.move(new Vector3f(this.calcControllerMotion()), new Vector3f(speed, speed, speed));
    }

    private void moveWithCollision(Vector3f motion, Vector3f speed) {
        Vector3f motXZ = new Vector3f(motion).mul(1, 0, 1);
        if (motXZ.length() > 0.0f) {
            motXZ.normalize();
        }

       if (this.isOnGround() && motion.y <= 0.0f) {
           Vector3f tryStepUp = this.tryStepUp(this.getPosition(), motion, speed, this.getStepHeight());
           if (tryStepUp != null) {
               this.setPosition(tryStepUp);
               return;
           }

           Vector3f tryStepDown = this.tryStepDown(this.getPosition(), motion, speed, this.getStepHeight());
           if (tryStepDown != null) {
              this.setPosition(tryStepDown);
              return;
           }
       }

        float ySlope = this.checkSlopeY(this.getPosition(), new Vector3f(motion), speed);
        //this.sweepTestAxisY(motion, new Vector3i(0, 1, 0), speed, thresholdC, ySlope);

        //this.sweepTestAxisY(motion, new Vector3i(0, 1, 0), speed, thresholdC, ySlope);
        //this.sweepTestAxisXZ(motion, new Vector3i(1, 0, 0), speed, thresholdC, ySlope);
        //this.sweepTestAxisXZ(motion, new Vector3i(0, 0, 1), speed, thresholdC, ySlope);

        this.moveTestVerticalY(motion, speed, ySlope);
        this.moveTestHorizontalXZ(motXZ, speed, ySlope);

       if (motion.y <= 0.0f) {
           float yDown = this.getSlopeDownY();
           if (this.isOnGround() && yDown != 0.0f && yDown < this.getPosition().y) {
               this.setPosition(new Vector3f(this.getPosition().x, yDown + 0.01f, this.getPosition().z));
           }
       }
    }

    public void moveTestVerticalY(Vector3f motion, Vector3f speed, final float ySlope) {
        Vector3f slide = new Vector3f(0.0f);
        Vector3f checkTo = new Vector3f(motion).mul(0.0f, 1.0f, 0.0f);
        if (checkTo.length() > 0.0f) {
            checkTo.normalize().mul(speed);
        }

        if (ySlope > 0.0f) {
            checkTo.y += ySlope;
        }

        SweepResult slideResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), this.getPosition(), checkTo, new Vector3i(0, 1, 0));
        if (slideResult.getSlideMotion() != null) {
            slide = slideResult.getSlideMotion();
        }

        if (slide.length() > 0.0f && slide.length() < 0.001f) {
            slide.normalize().mul(0.001f);
        }

        SweepResult slideResult2 = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), this.getPosition(), new Vector3f(slide), new Vector3i(1));
        if (slideResult2.getHitNormal() != null) {
            Vector3f corrPosSlide = new Vector3f(slideResult2.getCorrectedPos());
            this.setPosition(new Vector3f(corrPosSlide.x, corrPosSlide.y, corrPosSlide.z));

            SweepResult slideResultInner = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), this.getPosition(), new Vector3f(slideResult2.getSlideMotion().x, 0.0f, slideResult2.getSlideMotion().z), new Vector3i(1));
            this.setPosition(slideResultInner.getCorrectedPos());
        } else {
            Vector3f corrPosSweep = new Vector3f(slideResult.getCorrectedPos());
            float x0 = corrPosSweep.x;
            float y0 = corrPosSweep.y;
            float z0 = corrPosSweep.z;

            Vector3f corrected = new Vector3f(x0, y0, z0);
            float lenCor = speed.y == 0.0f ? 1.0f : 1.0f - Math.min(corrected.distance(this.getPosition()) / Math.abs(speed.y), 1.0f);
            slide.mul(lenCor);
            corrected.add(slide);

            this.setPosition(corrected);
        }
    }

    public void moveTestHorizontalXZ(Vector3f motion, Vector3f speed, final float ySlope) {
        Vector3f slide = new Vector3f(0.0f);
        Vector3f checkTo = new Vector3f(motion).mul(1.0f, 0.0f, 1.0f);
        if (checkTo.length() > 0.0f) {
            checkTo.normalize().mul(speed);
        }

        SweepResult slideResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), this.getPosition(), checkTo, new Vector3i(1, 0, 1), ySlope > 0.0f ? null : new Vector3f(1.0f, 0.0f, 1.0f));
        if (slideResult.getSlideMotion() != null) {
            slide = slideResult.getSlideMotion().mul(1.0f, 0.0f, 1.0f);
        }

        if (slide.length() > 0.0f && slide.length() < 0.001f) {
            slide.normalize().mul(0.001f);
        }

        Vector3f corrPosSweep = new Vector3f(slideResult.getCorrectedPos());
        float x0 = corrPosSweep.x;
        float y0 = Math.min(this.getPosition().y, corrPosSweep.y);
        float z0 = corrPosSweep.z;
        Vector3f corrected = new Vector3f(x0, y0, z0);
        float lenCor = 1.0f - Math.min(corrected.distance(this.getPosition()) / this.getWalkSpeed(), 1.0f);
        slide.mul(lenCor);

        SweepResult slideResult2 = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), corrected, new Vector3f(slide), new Vector3i(1, 0, 1));
        if (slideResult2.getHitNormal() != null) {
            Vector3f corrPosSlide = new Vector3f(slideResult2.getCorrectedPos());

           if (slideResult2.getSlideMotion().y > 0.0f) {
               if (this.checkAngleOnPos(this.getPhysicsGhostObject(), this.stepAngle(), new Vector3f(corrPosSlide).add(0.0f, slideResult2.getSlideMotion().y, 0.0f), new Vector3f(0.0f, -0.1f, 0.0f), false)) {
                   SweepResult slideResultInner = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), new Vector3f(corrPosSlide), new Vector3f(0.0f, slideResult2.getSlideMotion().y, 0.0f), new Vector3i(1));
                   corrPosSlide.y = slideResultInner.getCorrectedPos().y;
               }
           }

           this.setPosition(corrPosSlide);
        } else {
            this.setPosition(corrected.add(slide));
        }
    }

    /*
        Vector3f threshold = new Vector3f(0.0f);
        if (slideResult.getHitNormal() != null) {
            threshold = new Vector3f(slideResult.getHitNormal()).mul(thresholdC);
        }

              Vector3f threshold2 = new Vector3f(0.0f);
            if (slideResult.getHitNormal() != null) {
                threshold2 = new Vector3f(slideResult2.getHitNormal()).mul(thresholdC);
            }
     */


    private void sweepTestAxisXZ(Vector3f motion, Vector3i axis, Vector3f speed, final float thresholdC, float ySlope) {
        Vector3f checkFrom = new Vector3f(this.getPosition());
        Vector3f checkTo = new Vector3f(motion).mul(speed);

        SweepResult sweepResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), checkFrom, checkTo, axis, ySlope <= 0.0f ? new Vector3f(1.0f, 0.0f, 1.0f) : null);
        Vector3f threshold = new Vector3f(0.0f);
        if (sweepResult.getHitNormal() != null) {
            threshold = new Vector3f(sweepResult.getHitNormal()).mul(thresholdC);
        }

        this.setPosition(sweepResult.getCorrectedPos().add(threshold));
    }

    private void sweepTestAxisY(Vector3f motion, Vector3i axis, Vector3f speed, final float thresholdC, float ySlope) {
        Vector3f checkFrom = new Vector3f(this.getPosition());
        Vector3f checkTo = new Vector3f(motion).mul(speed);

        SweepResult sweepResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), this.createShape(this.capsuleSize(), 0.01f), checkFrom, checkTo, axis);
        Vector3f threshold = new Vector3f(0.0f);
        if (sweepResult.getHitNormal() != null) {
            threshold = new Vector3f(sweepResult.getHitNormal()).mul(thresholdC);
        }

        this.setPosition(sweepResult.getCorrectedPos().add(threshold));
    }

    /*
        private void sweepTestAxisXZ(Vector3f motion, Vector3i axis, Vector3f speed, final float thresholdC, float ySlope) {
        Vector3f checkFrom = new Vector3f(this.getPosition());
        Vector3f checkTo = new Vector3f(motion).mul(speed);

        SweepResult sweepResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), checkFrom, checkTo, axis, ySlope <= 0.0f ? new Vector3f(1.0f, 0.0f, 1.0f) : null);
        if (sweepResult.getHitNormal() != null) {
            Vector3f slide = sweepResult.getSlideMotion();
            Vector3f threshold = new Vector3f(sweepResult.getHitNormal()).mul(thresholdC);
            Vector3f slideVec = new Vector3f(slide).mul(1, 0, 1);

            if (slideVec.length() > 0.0f && slideVec.length() < 0.001f) {
                slideVec.normalize().mul(0.001f);
            }

            Vector3f checkSlideFrom = new Vector3f(sweepResult.getCorrectedPos());
            Vector3f checkSlideTo = slideVec.add(threshold);

            SweepResult sweepResultSlide = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), checkSlideFrom, checkSlideTo, new Vector3i(1));
            if (sweepResultSlide.getHitNormal() != null) {
                Vector3f newP = sweepResultSlide.getCorrectedPos();
                float ySlide = sweepResultSlide.getSlideMotion().y;

                float x = newP.x;
                float y = Math.min(newP.y, this.getPosition().y);
                float z = newP.z;

                Vector3f corrected = new Vector3f(x, y, z);
                if (ySlide > 0.0f) {
                    if (this.checkAngleOnPos(physicsGhostObject, this.stepAngle(), new Vector3f(corrected).add(0.0f, ySlide, 0.0f), new Vector3f(0.0f, -1.0f, 0.0f), false)) {
                        corrected.y += ySlide;
                        SweepResult sweepCorrectedSlideResultSlide = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), checkSlideFrom, new Vector3f(corrected).sub(checkSlideFrom), new Vector3i(1));
                        if (sweepCorrectedSlideResultSlide.getHitNormal() != null) {
                            corrected.set(sweepCorrectedSlideResultSlide.getCorrectedPos());
                        }
                    }
                }

                this.setPosition(corrected);
            } else {
                Vector3f newP = sweepResult.getCorrectedPos();
                float x = axis.x == 0 ? this.getPosition().x : newP.x;
                float y = this.getPosition().y;
                float z = axis.z == 0 ? this.getPosition().z : newP.z;

                System.out.println(new Vector3f(motion).mul(new Vector3f(axis)).normalize().dot(sweepResult.getHitNormal()));
                slideVec.mul(speed);

                float slideFactor = 1.0f;

                if (new Vector3f(motion).mul(new Vector3f(axis)).normalize().dot(sweepResult.getHitNormal()) <= -0.99f) {
                    slideFactor = 0.0f;
                }

                slideVec.mul(slideFactor);

                this.setPosition(new Vector3f(x, y, z).add(slideVec));
            }

        } else {
            this.setPosition(sweepResult.getCorrectedPos());
        }
    }

    private void sweepTestAxisY(Vector3f motion, Vector3i axis, Vector3f speed, final float thresholdC, float ySlope) {
        Vector3f checkFrom = new Vector3f(this.getPosition());
        Vector3f checkTo = new Vector3f(motion).mul(speed);

        if (!this.checkAngleOnPos(physicsGhostObject, this.stepAngle(), checkFrom, new Vector3f(0.0f, -0.1f, 0.0f), false)) {
            checkTo.mul(0, 1, 0);
        }

        if (ySlope > 0.0f) {
            checkTo.y += ySlope;
        }

        SweepResult sweepResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), this.createShape(this.capsuleSize(), 0.01f), checkFrom, checkTo, axis);
        if (sweepResult.getHitNormal() != null) {
            Vector3f slide = sweepResult.getSlideMotion();
            Vector3f threshold = new Vector3f(sweepResult.getHitNormal()).mul(thresholdC);

            Vector3f slideVec = new Vector3f(slide).mul(1, 0, 1);

            if (slideVec.length() > 0.0f && slideVec.length() < thresholdC) {
                slideVec.normalize().mul(thresholdC);
            }

            Vector3f checkSlideFrom = new Vector3f(sweepResult.getCorrectedPos());
            Vector3f checkSlideTo = slideVec.add(threshold);

            SweepResult sweepResultSlide = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), this.createShape(this.capsuleSize(), 0.01f), checkSlideFrom, checkSlideTo, new Vector3i(1));
            if (sweepResultSlide.getHitNormal() != null) {
                Vector3f newP = sweepResultSlide.getCorrectedPos();

                float x = newP.x;
                float y = newP.y;//Math.min(newP.y, this.getPosition().y);
                float z = newP.z;

                this.setPosition(new Vector3f(x, y, z));
            } else {
                Vector3f newP = sweepResult.getCorrectedPos();
                float x = this.getPosition().x;
                float y = Math.min(newP.y, this.getPosition().y);
                float z = this.getPosition().z;

                float slideFactor = 1.0f;

                if (this.checkDotAngle(new Vector3f(motion).mul(new Vector3f(axis)), sweepResult.getHitNormal(), this.stepAngle(), true)) {
                    slideFactor = 0.0f;
                }

                slideVec.mul(slideFactor);

                this.setPosition(new Vector3f(x, y, z).add(slideVec));
            }
        } else {
            this.setPosition(sweepResult.getCorrectedPos());
        }
    }
     */

    public float getJumpSpeed() {
        return 0.25f;
    }

    public float getStepHeight() {
        return 0.3f;
    }

    public Vector3f up() {
        return new Vector3f(0.0f, 1.0f, 0.0f);
    }

    public float stepAngle() {
        return (float) Math.toRadians(46.0f);
    }

    public boolean isOnGround() {
        return this.isOnGround;
    }

    public boolean canJump() {
        return true;
    }

    protected float getWalkSpeed() {
        return 0.25f;
    }

    @Override
    public float getEyeHeight() {
        return 0.45f;
    }

    public PhysicsGhostObject getPhysicsGhostObject() {
        return this.physicsGhostObject;
    }

    @Override
    public boolean canBeDestroyed() {
        return false;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory inventory() {
        return this.inventory;
    }

    @Override
    public float getScalarSpeed() {
        return 0;
    }

    @Override
    public float height() {
        return 1.0f;
    }

    private static class SweepResult {
        private final Vector3f correctedPos;
        private final Vector3f hitNormal;
        private final Vector3f slideMotion;
        private final float hitFract;

        private SweepResult(Vector3f correctedPos, Vector3f hitNormal, Vector3f slideMotion, float hitFract) {
            this.correctedPos = correctedPos;
            this.hitNormal = hitNormal;
            this.slideMotion = slideMotion;
            this.hitFract = hitFract;
        }

        public static SweepResult getSweepHitResult(DynamicsSystem dynamicsSystem, PhysicsGhostObject ghostObject, ConvexShape convexShape, Vector3f posFrom, Vector3f motion, Vector3i axis, Vector3f slideNormalCorrection) {
            Vector3f nmAxisM = new Vector3f(motion).mul(new Vector3f(axis));
            Vector3f moveTo1 = new Vector3f(posFrom).add(nmAxisM);
            Transform start = new Transform().setTranslation(DynamicsUtils.convertV3F_JME(posFrom));
            Transform end = new Transform().setTranslation(DynamicsUtils.convertV3F_JME(moveTo1));

            List<PhysicsSweepTestResult> sweepTestResultList = dynamicsSystem.getPhysicsSpace().sweepTest(convexShape, start, end, new ArrayList<>(), 0.0001f);

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
            sweepTestResultList.sort(Comparator.comparingDouble(e -> {
                float distanceToHit = e.getHitFraction();
                Vector3f corrected = DynamicsUtils.lerp(posFrom, moveTo1, distanceToHit);
                return corrected.distance(posFrom);
            }));
            if (!sweepTestResultList.isEmpty()) {
                PhysicsSweepTestResult physicsSweepTestResult1 = sweepTestResultList.get(0);
                float distanceToHit = physicsSweepTestResult1.getHitFraction();
                Vector3f corrected = DynamicsUtils.lerp(posFrom, moveTo1, distanceToHit);

                com.jme3.math.Vector3f normal0 = new com.jme3.math.Vector3f();
                physicsSweepTestResult1.getHitNormalLocal(normal0);
                Vector3f normal = DynamicsUtils.convertV3F_JOML(normal0);

                Vector3f inNormal = new Vector3f(normal);
                if (slideNormalCorrection != null) {
                    inNormal.mul(slideNormalCorrection);
                    if (inNormal.length() > 0f) {
                        inNormal.normalize();
                    }
                }

                final float c = 0.01f;
                if (inNormal.x > -c && inNormal.x < c) {
                    inNormal.x = 0.0f;
                }
                if (inNormal.y > -c && inNormal.y < c) {
                    inNormal.y = 0.0f;
                }
                if (inNormal.z > -c && inNormal.z < c) {
                    inNormal.z = 0.0f;
                }

                Vector3f motionAlongNormal = new Vector3f(inNormal).mul(new Vector3f(motion).dot(inNormal));
                Vector3f slideMotion = new Vector3f(motion).sub(motionAlongNormal);

                float dotAsMN = new Vector3f(nmAxisM).normalize().dot(inNormal);
                boolean isOrthogonal = (dotAsMN >= -1.0f && dotAsMN <= -0.99f);

                if (!slideMotion.isFinite()) {
                    slideMotion.set(0.0f);
                }

                return new SweepResult(corrected.add(new Vector3f(normal).mul(0.01f)), normal, slideMotion, distanceToHit);
            }
            return new SweepResult(moveTo1, null, null, 1.0f);
        }

        public static SweepResult getSweepHitResult(DynamicsSystem dynamicsSystem, PhysicsGhostObject ghostObject, ConvexShape convexShape, Vector3f posFrom, Vector3f motion, Vector3i axis) {
            return SweepResult.getSweepHitResult(dynamicsSystem, ghostObject, convexShape, posFrom, motion, axis, null);
        }

        public float getHitFract() {
            return this.hitFract;
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
