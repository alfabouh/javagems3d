package ru.alfabouh.engine.system.synchronizing;

import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.system.exception.GameException;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public final class Syncer {
    private final ReentrantLock lock;
    private final Condition condition;
    private boolean flag;

    public Syncer() {
        this.lock = new ReentrantLock();
        this.condition = lock.newCondition();
        this.flag = false;
    }

    public boolean isFlag() {
        return this.flag;
    }

    public void mark() {
        this.lock.lock();
        try {
            this.flag = true;
        } finally {
            this.lock.unlock();
        }
    }

    public void free() {
        this.lock.lock();
        try {
            this.flag = false;
            this.condition.signalAll();
        } finally {
            this.lock.unlock();
        }
    }

    public void blockCurrentThread(final boolean flagB) throws GameException {
        if (JGems.get().isShouldBeClosed()) {
            return;
        }

        this.lock.lock();
        try {
            while (this.flag == flagB) {
                this.condition.await();
            }
        } catch (InterruptedException e) {
            throw new GameException(e);
        } finally {
            this.lock.unlock();
        }
    }
}