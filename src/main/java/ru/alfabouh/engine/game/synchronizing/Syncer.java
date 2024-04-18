package ru.alfabouh.engine.game.synchronizing;

import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.exception.GameException;

import java.util.concurrent.atomic.AtomicBoolean;

public final class Syncer {
    private final AtomicBoolean atomicBoolean;
    private final Object monitor = new Object();

    public Syncer() {
        this.atomicBoolean = new AtomicBoolean(false);
    }

    public void mark() {
        this.atomicBoolean.set(true);
    }

    public void free() {
        this.atomicBoolean.set(false);
    }

    public void blockCurrentThread() {
        if (Game.getGame().isShouldBeClosed()) {
            return;
        }
        if (this.atomicBoolean.get()) {
            try {
                Thread thread = new Thread(() -> {
                    while (true) {
                        if (!this.atomicBoolean.get()) {
                            synchronized (this.monitor) {
                                this.monitor.notifyAll();
                            }
                            break;
                        }
                    }
                });
                thread.start();
                synchronized (this.monitor) {
                    this.monitor.wait();
                }
            } catch (InterruptedException e) {
                throw new GameException(e);
            }
        }
    }
}
