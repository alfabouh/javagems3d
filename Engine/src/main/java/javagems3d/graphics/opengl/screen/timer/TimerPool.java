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

package javagems3d.graphics.opengl.screen.timer;

import javagems3d.system.service.synchronizing.SyncManager;

import java.util.Iterator;
import java.util.Set;

public final class TimerPool {
    private final Set<JGemsTimer> JGemsTimerSet;

    public TimerPool() {
        this.JGemsTimerSet = SyncManager.createSyncronisedSet();
    }

    public void update() {
        Iterator<JGemsTimer> gameRenderTimerIterator = this.JGemsTimerSet.iterator();
        while (gameRenderTimerIterator.hasNext()) {
            JGemsTimer JGemsTimer = gameRenderTimerIterator.next();
            if (JGemsTimer.isShouldBeErased()) {
                gameRenderTimerIterator.remove();
            } else {
                JGemsTimer.update();
            }
        }
    }

    public void clear() {
        this.getTimerSet().clear();
    }

    public void deleteTimer(JGemsTimer JGemsTimer) {
        JGemsTimer.dispose();
    }

    public JGemsTimer createTimer() {
        JGemsTimer JGemsTimer = new JGemsTimer();
        this.getTimerSet().add(JGemsTimer);
        return JGemsTimer;
    }

    public Set<JGemsTimer> getTimerSet() {
        return this.JGemsTimerSet;
    }
}
