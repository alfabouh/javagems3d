package api.scripting.coding.env.internal.util.screen.timer;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.graphics.screen.timer.JGemsTimedAction;

@JSCodingClass(binding = "JSTimedAction", description = "...")
public class JSTimedAction {
    public final JGemsTimedAction timer;

    @JSHideFromDoc
    public JSTimedAction(JGemsTimedAction timer) {
        this.timer = timer;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void reset() {
        this.timer.reset();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void dispose() {
        this.timer.dispose();
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {"seconds"})
    public boolean resetTimerAfterReachedSeconds(double seconds) {
        return this.timer.resetTimerAfterReachedSeconds(seconds);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public boolean isShouldBeErased() {
        return this.timer.isShouldBeErased();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public double getLastTime() {
        return this.timer.getLastTime();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public float getDeltaTime() {
        return this.timer.getDeltaTime();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public double getAccumulatedTime() {
        return this.timer.getAccumulatedTime();
    }
}
