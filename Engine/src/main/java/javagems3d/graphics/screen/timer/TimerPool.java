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

package javagems3d.graphics.screen.timer;

import javagems3d.system.service.synchronizing.SyncManager;

import java.util.Iterator;
import java.util.Set;

public final class TimerPool {
    private final Set<JGemsTimedAction> timers;

    public TimerPool() {
        this.timers = SyncManager.createSyncronisedSet();
    }

    public void update() {
        Iterator<JGemsTimedAction> gameRenderTimerIterator = this.timers.iterator();
        while (gameRenderTimerIterator.hasNext()) {
            JGemsTimedAction timedAction = gameRenderTimerIterator.next();
            if (timedAction.isShouldBeErased()) {
                gameRenderTimerIterator.remove();
            } else {
                timedAction.update();
            }
        }
    }

    public void clear() {
        this.getTimerSet().clear();
    }

    public void deleteTimer(JGemsTimedAction timedAction) {
        timedAction.dispose();
    }

    public JGemsTimedAction createTimer() {
        JGemsTimedAction timedAction = new JGemsTimedAction();
        this.getTimerSet().add(timedAction);
        return timedAction;
    }

    public Set<JGemsTimedAction> getTimerSet() {
        return this.timers;
    }
}
