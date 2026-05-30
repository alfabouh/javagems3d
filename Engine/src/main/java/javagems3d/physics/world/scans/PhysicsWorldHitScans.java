package javagems3d.physics.world.scans;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.PhysicsSweepTestResult;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.ConvexShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.math.Transform;
import javagems3d.JGems3D;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public final class PhysicsWorldHitScans {
    public static final PhysicsWorldHitScans INSTANCE = new PhysicsWorldHitScans();

    private PhysicsWorldHitScans() {
    }

    private static PhysicsSpace physicsSpace() {return JGems3D.get().getPhysics().getPhysicsProcessor().getDynamicsSystem().getPhysicsSpace();
    }

    public record RayHitResult(PhysicsCollisionObject collisionObject, Vector3f hitPosition, Vector3f hitNormal, float hitFraction) {
    }

    public record SweepHitResult(PhysicsCollisionObject collisionObject, Vector3f hitPosition, Vector3f hitNormal, float hitFraction) {
    }

    @SafeVarargs
    public final Optional<RayHitResult> rayTestFirst(Vector3f from, Vector3f to, @Nullable ScanFilter<PhysicsRayTestResult>... filters) {
        List<PhysicsRayTestResult> results = PhysicsWorldHitScans.physicsSpace().rayTest(DynamicsUtils.convertV3F_JME(from), DynamicsUtils.convertV3F_JME(to));
        if (results.isEmpty()) {
            return Optional.empty();
        }
        PhysicsRayTestResult closest = null;
        float minFraction = Float.MAX_VALUE;
        if (filters != null && filters.length > 0) {
            results.removeIf(e -> {
                for (ScanFilter<PhysicsRayTestResult> filter : filters) {
                    if (Objects.requireNonNull(filter).getTester().test(e)) {
                        filter.getRejected().add(e);
                        return true;
                    }
                }
                return false;
            });
        }
        if (results.isEmpty()) {
            return Optional.empty();
        }
        for (PhysicsRayTestResult result : results) {
            if (result.getHitFraction() < minFraction) {
                minFraction = result.getHitFraction();
                closest = result;
            }
        }
        if (closest == null) {
            return Optional.empty();
        }
        com.jme3.math.Vector3f hitNormalLocal = closest.getHitNormalLocal(new com.jme3.math.Vector3f());
        Vector3f fromJoml = new Vector3f(from);
        Vector3f toJoml = new Vector3f(to);
        Vector3f hitPosition = new Vector3f(fromJoml).lerp(toJoml, closest.getHitFraction());
        return Optional.of(new RayHitResult(closest.getCollisionObject(), hitPosition, DynamicsUtils.convertV3F_JOML(hitNormalLocal), closest.getHitFraction()));
    }

    @SafeVarargs
    public final List<RayHitResult> rayTestAll(Vector3f from, Vector3f to, @Nullable ScanFilter<PhysicsRayTestResult>... filters) {
        List<PhysicsRayTestResult> results = PhysicsWorldHitScans.physicsSpace().rayTest(DynamicsUtils.convertV3F_JME(from), DynamicsUtils.convertV3F_JME(to));
        List<RayHitResult> out = new ArrayList<>(results.size());
        Vector3f fromJoml = new Vector3f(from);
        Vector3f toJoml = new Vector3f(to);
        if (filters != null && filters.length > 0) {
            results.removeIf(e -> {
                for (ScanFilter<PhysicsRayTestResult> filter : filters) {
                    if (Objects.requireNonNull(filter).getTester().test(e)) {
                        filter.getRejected().add(e);
                        return true;
                    }
                }
                return false;
            });
        }
        if (results.isEmpty()) {
            return new ArrayList<>();
        }
        for (PhysicsRayTestResult result : results) {
            Vector3f hitPosition = new Vector3f(fromJoml).lerp(toJoml, result.getHitFraction());
            out.add(new RayHitResult(result.getCollisionObject(), hitPosition, DynamicsUtils.convertV3F_JOML(result.getHitNormalLocal(new com.jme3.math.Vector3f())), result.getHitFraction()));
        }
        out.sort(Comparator.comparing(RayHitResult::hitFraction));
        return out;
    }

    @SafeVarargs
    public final Optional<SweepHitResult> boxSweepFirst(float xHalf, float yHalf, float zHalf, Vector3f from, Vector3f to, @Nullable ScanFilter<PhysicsSweepTestResult>... filters) {
        return this.SweepFirst(new BoxCollisionShape(xHalf, yHalf, zHalf), from, to);
    }

    @SafeVarargs
    public final Optional<SweepHitResult> sphereSweepFirst(float radius, Vector3f from, Vector3f to, @Nullable ScanFilter<PhysicsSweepTestResult>... filters) {
        return this.SweepFirst(new SphereCollisionShape(radius), from, to);
    }

    @SafeVarargs
    public final Optional<SweepHitResult> SweepFirst(ConvexShape collisionShape, Vector3f from, Vector3f to, @Nullable ScanFilter<PhysicsSweepTestResult>... filters) {
        Transform start = new Transform();
        start.setTranslation(DynamicsUtils.convertV3F_JME(from));
        Transform end = new Transform();
        end.setTranslation(DynamicsUtils.convertV3F_JME(to));
        List<PhysicsSweepTestResult> results = new ArrayList<>();
        PhysicsWorldHitScans.physicsSpace().sweepTest(collisionShape, start, end, results, 0.05f);
        if (filters != null && filters.length > 0) {
            results.removeIf(e -> {
                for (ScanFilter<PhysicsSweepTestResult> filter : filters) {
                    if (Objects.requireNonNull(filter).getTester().test(e)) {
                        filter.getRejected().add(e);
                        return true;
                    }
                }
                return false;
            });
        }
        if (results.isEmpty()) {
            return Optional.empty();
        }
        PhysicsSweepTestResult closest = null;
        float minFraction = Float.MAX_VALUE;
        for (PhysicsSweepTestResult result : results) {
            if (result.getHitFraction() < minFraction) {
                minFraction = result.getHitFraction();
                closest = result;
            }
        }
        if (closest == null) {
            return Optional.empty();
        }
        Vector3f hitPosition = new Vector3f(from).lerp(to, closest.getHitFraction());
        return Optional.of(new SweepHitResult(closest.getCollisionObject(), hitPosition, DynamicsUtils.convertV3F_JOML(closest.getHitNormalLocal(new com.jme3.math.Vector3f())), closest.getHitFraction()));
    }

    public static class ScanFilter<T> {
        private final Predicate<T> tester;
        private final List<T> rejected;

        public ScanFilter(@NotNull Predicate<T> tester) {
            this.tester = tester;
            this.rejected = new ArrayList<>();
        }

        public @NotNull Predicate<T> getTester() {
            return this.tester;
        }

        public List<T> getRejected() {
            return this.rejected;
        }
    }
}