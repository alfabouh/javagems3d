/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
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
        this.condition = this.lock.newCondition();
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