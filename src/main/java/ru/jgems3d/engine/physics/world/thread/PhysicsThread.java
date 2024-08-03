package ru.jgems3d.engine.physics.world.thread;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.physics.world.thread.timer.PhysicsTimer;
import ru.jgems3d.engine.JGemsHelper;

import java.util.concurrent.*;

public class PhysicsThread {
    public static final Object locker = new Object();
    public static final int TICKS_PER_SECOND = 60;
    private final PhysicsTimer physicsTimer;
    private final int tps;
    private final ExecutorService executor;
    public boolean badExit;

    @SuppressWarnings("all")
    public PhysicsThread(int tps) {
        this.tps = tps;
        this.physicsTimer = new PhysicsTimer();
        this.executor = Executors.newSingleThreadExecutor(new NamedThreadFactory("physics"));
        this.badExit = false;
    }

    public static double getFrameTime() {
        return 1.0d / PhysicsThread.TICKS_PER_SECOND;
    }

    public void initService() {
        this.getExecutor().execute(() -> {
            try {
                this.getPhysicsTimer().updateTimer(this.getTps());
            } catch (Exception e) {
                JGemsHelper.getLogger().exception(e);
                this.badExit = true;
            } finally {
                JGems3D.get().destroyGame();
                this.getExecutor().shutdown();
            }
        });
    }

    public boolean waitForFullTermination() throws InterruptedException {
        return this.getExecutor().awaitTermination(10000, TimeUnit.MILLISECONDS);
    }

    public ExecutorService getExecutor() {
        return this.executor;
    }

    public int getTps() {
        return this.tps;
    }

    public final PhysicsTimer getPhysicsTimer() {
        return this.physicsTimer;
    }

    private static class NamedThreadFactory implements ThreadFactory {
        private final String baseName;

        public NamedThreadFactory(String baseName) {
            this.baseName = baseName;
        }

        @Override
        public Thread newThread(@NotNull Runnable r) {
            Thread t = new Thread(r);
            t.setName(baseName);
            return t;
        }
    }
}
