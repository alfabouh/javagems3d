package api.scripting.coding.env.internal.util.world.render.screen.timer;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.graphics.screen.timer.TimerPool;

@JSCodingClass(binding = "JSTimerPool", description = "Wrapper for a TimerPool, allowing creation, deletion, and management of timed actions in scripting.")
public class JSTimerPool {
    @JSHideFromDoc public final TimerPool timerPool;

    @JSHideFromDoc
    public JSTimerPool(TimerPool timerPool) {
        this.timerPool = timerPool;
    }

    @JSCodingFunctionOrMethod(description = "Create a new timed action managed by this timer pool.", paramNames = {})
    public JSTimedAction createTimer() {
        return new JSTimedAction(this.timerPool.createTimer());
    }

    @JSCodingFunctionOrMethod(description = "Delete a previously created timed action.", paramNames = {"timedAction"})
    public void deleteTimer(JSTimedAction timedAction) {
        timedAction.dispose();
    }

    @JSCodingFunctionOrMethod(description = "Clear all timers in this timer pool.", paramNames = {})
    public void clear() {
        this.timerPool.clear();
    }
}