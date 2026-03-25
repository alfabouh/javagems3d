package api.scripting.coding.env.internal.util.screen;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.management.JSPath;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import api.scripting.coding.env.internal.util.screen.timer.JSTimerPool;
import javagems3d.graphics.screen.JGemsScreen;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.service.files.source.ISource;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;

@JSCodingClass(binding = "JSScreen", description = "Represents a screen with rendering, window, and timer functionality.")
public class JSScreen {
    @JSHideFromDoc private final JGemsScreen screen;
    private final JSTimerPool timerPool;

    @JSHideFromDoc
    public JSScreen(JGemsScreen screen) {
        this.screen = screen;
        this.timerPool = new JSTimerPool(screen.getTimerPool());
    }

    @JSCodingFunctionOrMethod(description = "Switches between screen modes.")
    public void switchScreenMode() {
        this.screen.switchScreenMode();
    }

    @JSCodingFunctionOrMethod(description = "Refreshes scene resources.")
    public void refreshSceneResources() {
        this.screen.refreshSceneResources();
    }

    @JSCodingFunctionOrMethod(description = "Returns the current render ticks.")
    public float getRenderTicks() {
        return this.screen.getRenderTicks();
    }

    @JSCodingFunctionOrMethod(description = "Get window dimensions as a vector.")
    public JSVector2f getWindowDimensions() {
        return new JSVector2f(this.screen.getWindowDimensions().x, this.screen.getWindowDimensions().y);
    }

    @JSCodingFunctionOrMethod(description = "Get window instance.")
    public JSWindow getWindow() {
        return new JSWindow(this.screen.getWindow());
    }

    @JSCodingFunctionOrMethod(description = "Get timer pool.")
    public JSTimerPool getTimerPool() {
        return this.timerPool;
    }
}