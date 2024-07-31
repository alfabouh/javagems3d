package ru.jgems3d.engine.graphics.opengl.screen.timer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TimerPool {
    private final Set<JGemsTimer> JGemsTimerSet;

    public TimerPool() {
        this.JGemsTimerSet = new HashSet<>();
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
