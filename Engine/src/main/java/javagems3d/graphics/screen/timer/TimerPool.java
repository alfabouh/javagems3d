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
