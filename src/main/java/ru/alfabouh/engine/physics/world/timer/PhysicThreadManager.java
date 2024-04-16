package ru.alfabouh.engine.physics.world.timer;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.logger.GameLogging;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

public class PhysicThreadManager {
    public static final Object locker = new Object();
    public static final int TICKS_PER_SECOND = 50;
    public static final int PHYS_THREADS = 1;
    private final ExecutorService executorService;
    private final PhysicsTimer physicsTimer;
    private final int tps;

    public PhysicThreadManager(int tps) {
        this.tps = tps;
        this.executorService = Executors.newFixedThreadPool(PhysicThreadManager.PHYS_THREADS, new GamePhysicsThreadFactory("physics"));
        this.physicsTimer = new PhysicsTimer();
    }

    public static double getFrameTime() {
        return 1.0d / PhysicThreadManager.TICKS_PER_SECOND;
    }

    public static long getTicksForUpdate(int TPS) {
        return 1000L / TPS;
    }

    public boolean checkActivePhysics() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) this.getExecutorService();
        return executor.getActiveCount() > 0;
    }

    public void initService() {
        this.getExecutorService().execute(() -> {
            try {
                this.getPhysicsTimer().updateTimer(this.getTps());
            } catch (Exception e) {
                Game.getGame().getLogManager().error(e);
                GameLogging.showExceptionDialog("An exception occurred inside the game. Open the logs folder for details.");
            }
        });
    }

    public void destroy() {
        this.getExecutorService().shutdown();
    }

    public int getTps() {
        return this.tps;
    }

    public final PhysicsTimer getPhysicsTimer() {
        return this.physicsTimer;
    }

    public final ExecutorService getExecutorService() {
        return this.executorService;
    }

    private static class GamePhysicsThreadFactory implements ThreadFactory {
        private final String threadName;

        public GamePhysicsThreadFactory(String threadName) {
            this.threadName = threadName;
        }

        @Override
        public Thread newThread(@NotNull Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(this.threadName);
            return thread;
        }
    }
}
