package api.scripting.coding.env.internal.util.world.render.screen.timer;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.graphics.screen.timer.JGemsTimedAction;

@JSCodingClass(binding = "JSTimedAction", description = "Represents a timed action with methods to reset, disposal, and querying elapsed time.")
public class JSTimedAction {
    public final JGemsTimedAction timer;

    @JSHideFromDoc
    public JSTimedAction(JGemsTimedAction timer) {
        this.timer = timer;
    }

    @JSCodingFunctionOrMethod(description = "Reset the timer to its initial state.")
    public void reset() {
        this.timer.reset();
    }

    @JSCodingFunctionOrMethod(description = "Dispose of the timer and release resources.")
    public void dispose() {
        this.timer.dispose();
    }

    @JSCodingFunctionOrMethod(description = "Reset the timer if the specified number of seconds has elapsed.", paramNames = {"seconds"})
    public boolean resetTimerAfterReachedSeconds(double seconds) {
        return this.timer.resetTimerAfterReachedSeconds(seconds);
    }

    @JSCodingFunctionOrMethod(description = "Check if the timer should be erased.")
    public boolean isShouldBeErased() {
        return this.timer.isShouldBeErased();
    }

    @JSCodingFunctionOrMethod(description = "Get the last recorded time of the timer.")
    public double getLastTime() {
        return this.timer.getLastTime();
    }

    @JSCodingFunctionOrMethod(description = "Get the delta time since the last update of the timer.")
    public float getDeltaTime() {
        return this.timer.getDeltaTime();
    }

    @JSCodingFunctionOrMethod(description = "Get the total accumulated time tracked by the timer.")
    public double getAccumulatedTime() {
        return this.timer.getAccumulatedTime();
    }
}