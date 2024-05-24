package ru.alfabouh.engine.render.screen.timer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TimerPool {
    private final Set<GameRenderTimer> gameRenderTimerSet;

    public TimerPool() {
        this.gameRenderTimerSet = new HashSet<>();
    }

    public void update() {
        Iterator<GameRenderTimer> gameRenderTimerIterator = this.gameRenderTimerSet.iterator();
        while (gameRenderTimerIterator.hasNext()) {
            GameRenderTimer gameRenderTimer = gameRenderTimerIterator.next();
            if (gameRenderTimer.isShouldBeErased()) {
                gameRenderTimerIterator.remove();
            } else {
                gameRenderTimer.update();
            }
        }
    }

    public void clear() {
        this.getTimerSet().clear();
    }

    public void deleteTimer(GameRenderTimer gameRenderTimer) {
        gameRenderTimer.delete();
    }

    public GameRenderTimer createTimer() {
        GameRenderTimer gameRenderTimer = new GameRenderTimer();
        this.getTimerSet().add(gameRenderTimer);
        return gameRenderTimer;
    }

    public Set<GameRenderTimer> getTimerSet() {
        return this.gameRenderTimerSet;
    }
}
