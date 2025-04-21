package api.scripting.classes.global.timer;
import javagems3d.graphics.screen.timer.JGemsTimer;
import javagems3d.help.JGemsHelper;
import javagems3d.system.service.synchronizing.SyncManager;
import javagems3d.system.service.synchronizing.Syncer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class TimerManagerJS {
    private final Map<String, TimerInfo> timerMap;

    public TimerManagerJS() {
        this.timerMap = SyncManager.createSyncronisedMap();
    }

    public synchronized void pushTimer(String id, int seconds, int repeatTimes, @NotNull TimerCallbackJS timerCallbackJS) {
        this.getTimerMap().put(id, new TimerInfo(seconds, repeatTimes, timerCallbackJS));
    }

    public synchronized void removeTimer(String id) {
        if (this.getTimerMap().containsKey(id)) {
            this.getTimerMap().get(id).clear();
            this.getTimerMap().remove(id);
        }
    }

    public synchronized void clear() {
        this.getTimerMap().forEach((k, v) -> v.clear());
        this.getTimerMap().clear();
    }

    public synchronized boolean isTimerActive(String id) {
        return this.getTimerMap().get(id).isActive();
    }

    public void renderThreadUpdateTimers() {
        for (TimerInfo timerInfo : this.getTimerMap().values()) {
            if (!timerInfo.isActive()) {
                continue;
            }
            if (timerInfo.getCurrentlyRepeated() >= timerInfo.getRepeatTimes()) {
                timerInfo.setActive(false);
                timerInfo.clear();
                continue;
            }
            if (timerInfo.getTimer().resetTimerAfterReachedSeconds(timerInfo.getSeconds())) {
                timerInfo.getTimerCallback().action();
                timerInfo.incRepeated();
            }
        }
    }

    public Map<String, TimerInfo> getTimerMap() {
        return this.timerMap;
    }

    public static class TimerInfo {
        private int currentlyRepeated;
        private boolean active;

        private final int seconds;
        private final int repeatTimes;
        private final TimerCallbackJS timerCallbackJS;
        private final JGemsTimer timer;

        public TimerInfo(int seconds, int repeatTimes, @NotNull TimerCallbackJS timerCallbackJS) {
            this.currentlyRepeated = 0;
            this.active = true;

            this.seconds = seconds;
            this.repeatTimes = repeatTimes;
            this.timerCallbackJS = timerCallbackJS;
            this.timer = JGemsHelper.screen().createTimer();
        }

        void clear() {
            JGemsHelper.screen().getTimerPool().deleteTimer(this.getTimer());
        }

        void incRepeated() {
            this.currentlyRepeated += 1;
        }

        void setActive(boolean active) {
            this.active = active;
        }

        public boolean isActive() {
            return this.active;
        }

        int getCurrentlyRepeated() {
            return this.currentlyRepeated;
        }

        JGemsTimer getTimer() {
            return this.timer;
        }

        int getSeconds() {
            return this.seconds;
        }

        int getRepeatTimes() {
            return this.repeatTimes;
        }

        TimerCallbackJS getTimerCallback() {
            return this.timerCallbackJS;
        }
    }
}
