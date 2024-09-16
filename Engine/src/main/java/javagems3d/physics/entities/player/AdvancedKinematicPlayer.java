package javagems3d.physics.entities.player;

import com.jme3.bullet.collision.PhysicsSweepTestResult;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.ConvexShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.math.Transform;
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

public class AdvancedKinematicPlayer extends Player implements IInventoryOwner, IWorldTicked {
    private Inventory inventory;
    private PhysicsGhostObject physicsGhostObject;
    private boolean isOnGround;

    public AdvancedKinematicPlayer(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot) {
        super(world, pos, rot, "player_sp");
        this.createInventory();
    }

    @Override
    protected void createPlayer(PhysicsWorld world) {
        this.changeYStartPos();
        this.physicsGhostObject = new PhysicsGhostObject(this.createShape(this.capsuleSize(), 0.0f));
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

    @Override
    public final void onUpdate(IWorld iWorld) {
        this.isOnGround = this.checkIfOnGround(this.getPhysicsGhostObject());
        this.onTick(iWorld);
    }

    protected void onTick(IWorld iWorld) {
        this.walk(this.calcControllerMotion(), 0.35f);
    }

    protected void walk(Vector3f dir, float speed) {
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
        SweepResult sweepResult = SweepResult.getSimpleHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), this.getPosition(), new Vector3f(0.0f, -0.1f, 0.0f));
        return sweepResult.getCorrectedPos() != null;
    }

    private float stepDown() {
        SweepResult sweepResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), this.getPosition(), new Vector3f(0.0f, -0.35f, 0.0f), new Vector3i(0, 1, 0));
        if (sweepResult.getHitNormal() != null) {
            if (this.checkDotAngle(this.up(), sweepResult.getHitNormal(), this.stepAngle())) {
                float y = sweepResult.getCorrectedPos().y;
                if (y < this.getPosition().y) {
                    return y;
                }
            }
        }
        return 0.0f;
    }

    public float angleUnderKnees(Vector3f pos, Vector3f motion, float speed) {
        float angle = this.stepAngle();
        Vector3f upMotion = new Vector3f(motion).mul(speed);
        upMotion.y = (float) Math.sin(angle);

        SweepResult sweepResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), pos, upMotion, new Vector3i(1));
        Vector3f corrected = sweepResult.getCorrectedPos();

        SweepResult checkDown = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), this.createShape(this.capsuleSize(), 0.01f), corrected, new Vector3f(0.0f, -1.0f, 0.0f), new Vector3i(0, 1, 0));

        if (checkDown.getHitNormal() != null) {
            float dot = new Vector3f(checkDown.getHitNormal()).dot(this.up());
            return (float) Math.acos(dot);
        }

        return -1.0f;
    }

    private Vector3f translateOnSurface(Vector3f pos, float h) {
        SweepResult checkDown = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), this.createShape(this.capsuleSize(), 0.01f), pos, new Vector3f(0.0f, -h, 0.0f), new Vector3i(0, 1, 0));
        if (checkDown.getHitNormal() != null) {
            return checkDown.getCorrectedPos();
        }
        return null;
    }

    private void moveWithCollision(Vector3f motion, float speed) {
        float thresholdC = 0.01f;

        float h = this.sweepTestAxisY(motion, new Vector3i(0, 1, 0), speed, thresholdC);

        this.sweepTestAxisXZ(motion, new Vector3i(1, 0, 0), speed, thresholdC);
        this.sweepTestAxisXZ(motion, new Vector3i(0, 0, 1), speed, thresholdC);

        if (h != 0.0f) {
            Vector3f v3 = this.translateOnSurface(this.getPosition(), h);
            if (v3 != null) {
                this.setPosition(v3.add(0.0f, 0.01f, 0.0f));
            }
        }

        float yDown = this.stepDown();
        if (yDown != 0.0f && yDown < (this.getPosition().y - h)) {
             this.setPosition(new Vector3f(this.getPosition().x, yDown + 0.01f, this.getPosition().z));
        }
    }

    private void sweepTestAxisXZ(Vector3f motion, Vector3i axis, float speed, final float thresholdC) {
        Vector3f checkFrom = new Vector3f(this.getPosition());
        Vector3f checkTo = new Vector3f(motion).mul(speed);

        SweepResult sweepResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), checkFrom, checkTo, axis);
        if (sweepResult.getHitNormal() != null) {
            Vector3f slide = sweepResult.getSlideMotion();
            Vector3f threshold = new Vector3f(sweepResult.getHitNormal()).mul(thresholdC);

            Vector3f slideVec = new Vector3f(slide);
            slideVec.mul(1, 0, 1);

            if (slideVec.length() < thresholdC) {
                slideVec.normalize().mul(thresholdC);
            }

            Vector3f checkSlideFrom = new Vector3f(sweepResult.getCorrectedPos());
            Vector3f checkSlideTo = slideVec.add(threshold);

            float yUp2 = this.angleUnderKnees(new Vector3f(checkSlideFrom).add(0.0f, 0.01f, 0.0f), checkSlideTo, speed);
            if (yUp2 != -1.0f && yUp2 <= this.stepAngle()) {
                float h2 = (float) (checkSlideTo.length() * Math.sin(yUp2));
                checkSlideTo.add(0, h2, 0);
            }

            SweepResult sweepResultSlide = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), checkSlideFrom, checkSlideTo, new Vector3i(1));
            if (sweepResultSlide.getHitNormal() != null) {
                Vector3f threshold2 = new Vector3f(sweepResultSlide.getHitNormal()).mul(thresholdC);
                Vector3f newP = sweepResultSlide.getCorrectedPos();

                float x = newP.x;
                float y = Math.min(newP.y, this.getPosition().y);
                float z = newP.z;

                this.setPosition(new Vector3f(x, y, z));
            } else {
                Vector3f newP = sweepResult.getCorrectedPos();
                float x = axis.x == 0 ? this.getPosition().x : newP.x;
                float y = axis.y == 0 ? this.getPosition().y : Math.min(newP.y, this.getPosition().y);
                float z = axis.z == 0 ? this.getPosition().z : newP.z;

                float slideFactor = 1.0f;

                Vector3f slideV = new Vector3f(slideVec);
                if (!slideV.isFinite()) {
                    slideV.set(0.0f);
                }
                slideV.mul(speed).mul(slideFactor);

                this.setPosition(new Vector3f(x, y, z).add(slideV));
            }
        } else {
            this.setPosition(sweepResult.getCorrectedPos());
        }
    }

    private float sweepTestAxisY(Vector3f motion, Vector3i axis, float speed, final float thresholdC) {
        Vector3f checkFrom = new Vector3f(this.getPosition());
        Vector3f checkTo = new Vector3f(motion);

        float h = 0.0f;
        Vector3f motY = new Vector3f(motion).mul(1, 0, 1).normalize().mul(0.05f);
        float yUp = this.angleUnderKnees(this.getPosition().add(0.0f, 0.01f, 0.0f), motY, speed);
        if (yUp != -1.0f && yUp <= this.stepAngle()) {
            h = (float) (checkTo.length() * Math.sin(yUp));
            checkTo.add(0, h, 0);
        }
        checkTo.normalize().mul(speed);

        SweepResult sweepResult = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) (this.createShape(this.capsuleSize(), 0.01f)), checkFrom, checkTo, axis);
        if (sweepResult.getHitNormal() != null) {
            Vector3f slide = sweepResult.getSlideMotion();
            Vector3f threshold = new Vector3f(sweepResult.getHitNormal()).mul(thresholdC);

            Vector3f slideVec = new Vector3f(slide);
            slideVec.mul(1, 0, 1);

            if (slideVec.length() < thresholdC) {
                slideVec.normalize().mul(thresholdC);
            }

            Vector3f checkSlideFrom = new Vector3f(sweepResult.getCorrectedPos());
            Vector3f checkSlideTo = slideVec.add(threshold);

            float yUp2 = this.angleUnderKnees(new Vector3f(checkSlideFrom).add(0.0f, 0.01f, 0.0f), checkSlideTo, speed);
            if (yUp2 != -1.0f && yUp2 <= this.stepAngle()) {
                float h2 = (float) (checkSlideTo.length() * Math.sin(yUp2));
                checkSlideTo.add(0, h2, 0);
            }
            SweepResult sweepResultSlide = SweepResult.getSweepHitResult(this.getWorld().getDynamics(), this.getPhysicsGhostObject(), (ConvexShape) this.getPhysicsGhostObject().getCollisionShape(), checkSlideFrom, checkSlideTo, new Vector3i(1));
            if (sweepResultSlide.getHitNormal() != null) {
                Vector3f threshold2 = new Vector3f(sweepResultSlide.getHitNormal()).mul(thresholdC);
                Vector3f newP = sweepResultSlide.getCorrectedPos();

                float x = newP.x;
                float y = Math.min(newP.y, this.getPosition().y);
                float z = newP.z;

                this.setPosition(new Vector3f(x, y, z));
            } else {
                Vector3f newP = sweepResult.getCorrectedPos();
                float x = axis.x == 0 ? this.getPosition().x : newP.x;
                float y = axis.y == 0 ? this.getPosition().y : Math.min(newP.y, this.getPosition().y);
                float z = axis.z == 0 ? this.getPosition().z : newP.z;

                float slideFactor = 1.0f;

                if (this.checkDotAngle(this.up(), sweepResult.getHitNormal(), this.stepAngle())) {
                    slideFactor = 0.0f;
                }

                Vector3f slideV = new Vector3f(slideVec);
                if (!slideV.isFinite()) {
                    slideV.set(0.0f);
                }
                slideV.mul(speed).mul(slideFactor);

                this.setPosition(new Vector3f(x, y, z).add(slideV));
            }
        } else {
            this.setPosition(sweepResult.getCorrectedPos());
        }

        return h;
    }

    private boolean checkDotAngle(Vector3f v1, Vector3f v2, float angle) {
        float dot = new Vector3f(v1).dot(v2);
        if (dot == 1.0f) {
            return false;
        }
        return Math.acos(dot) <= angle;
    }

    public Vector3f up() {
        return new Vector3f(0.0f, 1.0f, 0.0f);
    }

    public float stepAngle() {
        return (float) Math.toRadians(55.0f);
    }

    public boolean canJump() {
        return true;
    }

    protected float walkSpeed() {
        return 0.5f;
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

        public static SweepResult getSimpleHitResult(DynamicsSystem dynamicsSystem, PhysicsGhostObject ghostObject, Vector3f posFrom, Vector3f motion) {
            Vector3f moveTo1 = new Vector3f(posFrom).add(new Vector3f(motion));
            Transform start = new Transform().setTranslation(DynamicsUtils.convertV3F_JME(posFrom));
            Transform end = new Transform().setTranslation(DynamicsUtils.convertV3F_JME(moveTo1));
            List<PhysicsSweepTestResult> sweepTestResultList = dynamicsSystem.getPhysicsSpace().sweepTest((ConvexShape) ghostObject.getCollisionShape(), start, end, new ArrayList<>(), 0.0001f);

            sweepTestResultList.removeIf(e -> e.getCollisionObject().equals(ghostObject));
            sweepTestResultList.sort(Comparator.comparingDouble(e -> {
                float distanceToHit = e.getHitFraction();
                Vector3f corrected = DynamicsUtils.lerp(posFrom, moveTo1, distanceToHit);
                return corrected.distance(posFrom);
            }));
            if (!sweepTestResultList.isEmpty()) {
                PhysicsSweepTestResult physicsSweepTestResult1 = sweepTestResultList.get(0);
                float distanceToHit = physicsSweepTestResult1.getHitFraction();
                Vector3f corrected = DynamicsUtils.lerp(posFrom, moveTo1, distanceToHit);
                return new SweepResult(corrected, null, null, physicsSweepTestResult1.getHitFraction());
            }
            return new SweepResult(null, null, null, 1.0f);
        }

        public static SweepResult getSweepHitResult(DynamicsSystem dynamicsSystem, PhysicsGhostObject ghostObject, ConvexShape convexShape, Vector3f posFrom, Vector3f motion, Vector3i axis) {
            Vector3f moveTo1 = new Vector3f(posFrom).add(new Vector3f(motion).mul(new Vector3f(axis)));
            Transform start = new Transform().setTranslation(DynamicsUtils.convertV3F_JME(posFrom));
            Transform end = new Transform().setTranslation(DynamicsUtils.convertV3F_JME(moveTo1));

            List<PhysicsSweepTestResult> sweepTestResultList = dynamicsSystem.getPhysicsSpace().sweepTest(convexShape, start, end, new ArrayList<>(), 0.0001f);

            sweepTestResultList.removeIf(e -> e.getCollisionObject().equals(ghostObject));
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
                final float c = 0.01f;
                if (normal.x > -c && normal.x < c) {
                    normal.x = 0.0f;
                }
                if (normal.y > -c && normal.y < c) {
                    normal.y = 0.0f;
                }
                if (normal.z > -c && normal.z < c) {
                    normal.z = 0.0f;
                }
                Vector3f motionAlongNormal = normal.mul(new Vector3f(motion).dot(normal), new Vector3f());
                Vector3f slideMotion = new Vector3f(motion).sub(motionAlongNormal).mul(1, 0, 1);

                return new SweepResult(corrected, normal, slideMotion, distanceToHit);
            }
            return new SweepResult(moveTo1, null, null, 1.0f);
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
