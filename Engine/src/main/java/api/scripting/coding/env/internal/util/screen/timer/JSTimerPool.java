package api.scripting.coding.env.internal.util.screen.timer;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.graphics.screen.timer.TimerPool;

@JSCodingClass(binding = "JSTimerPool", description = "...")
public class JSTimerPool {
    @JSHideFromDoc public final TimerPool timerPool;

    @JSHideFromDoc
    public JSTimerPool(TimerPool timerPool) {
        this.timerPool = timerPool;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSTimedAction createTimer() {
        return new JSTimedAction(this.timerPool.createTimer());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void deleteTimer(JSTimedAction timedAction) {
        timedAction.dispose();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void clear() {
        this.timerPool.clear();
    }
}