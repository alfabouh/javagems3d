/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.system.service.synchronizing;

import javagems3d.JGems3D;
import javagems3d.system.service.exceptions.JGemsException;
import javagems3d.system.service.exceptions.JGemsRuntimeException;

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

    public void blockCurrentThread(final boolean flagB) throws JGemsException {
        if (JGems3D.get().isShouldBeClosed()) {
            return;
        }

        this.lock.lock();
        try {
            while (this.flag == flagB) {
                this.condition.await();
            }
        } catch (InterruptedException e) {
            throw new JGemsRuntimeException(e);
        } finally {
            this.lock.unlock();
        }
    }
}