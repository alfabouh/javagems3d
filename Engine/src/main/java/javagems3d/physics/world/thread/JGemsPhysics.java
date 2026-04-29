package javagems3d.physics.world.thread;

import javagems3d.physics.world.PhysicsWorld;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import javagems3d.JGems3D;
import javagems3d.physics.world.thread.timer.PhysicsProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class JGemsPhysics {
    public static final Object locker = new Object();
    public static final int TICKS_PER_SECOND = 40;
    private final PhysicsProcessor physicsProcessor;
    private final int tps;
    private final ExecutorService executor;

    @SuppressWarnings("all")
    public JGemsPhysics(int tps) {
        this.tps = tps;
        this.physicsProcessor = new PhysicsProcessor();
        this.executor = Executors.newSingleThreadExecutor(new NamedThreadFactory("physics"));
    }

    public static double getFrameTime() {
        return 1.0d / JGemsPhysics.TICKS_PER_SECOND;
    }

    public void initService() {
        this.getExecutor().execute(() -> {
            try {
                this.getPhysicsProcessor().updateTimer(this.getTps());
            } catch (Exception e) {
                Log.get().exception(e);
                JGems3D.close(e);
            } finally {
                this.getExecutor().shutdown();
            }
        });
    }

    public boolean waitForFullTermination() throws InterruptedException {
        return this.getExecutor().awaitTermination(5000, TimeUnit.MILLISECONDS);
    }

    private ExecutorService getExecutor() {
        return this.executor;
    }

    public int getTps() {
        return this.tps;
    }

    public PhysicsWorld getPhysicsWorld() {
        return this.getPhysicsProcessor().getPhysicsWorld();
    }

    public final PhysicsProcessor getPhysicsProcessor() {
        return this.physicsProcessor;
    }

    private record NamedThreadFactory(String baseName) implements ThreadFactory {

        @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread t = new Thread(r);
                t.setName(baseName);
                return t;
            }
        }
}
