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

@JSCodingClass(binding = "JSScreen", description = "...")
public class JSScreen {
    @JSHideFromDoc private final JGemsScreen screen;
    private final JSTimerPool timerPool;

    @JSHideFromDoc
    public JSScreen(JGemsScreen screen) {
        this.screen = screen;
        this.timerPool = new JSTimerPool(screen.getTimerPool());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void switchScreenMode() {
        this.screen.switchScreenMode();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void refreshSceneResources() {
        this.screen.refreshSceneResources();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public float getRenderTicks() {
        return this.screen.getRenderTicks();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSVector2f getWindowDimensions() {
        return new JSVector2f(this.screen.getWindowDimensions().x, this.screen.getWindowDimensions().y);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSWindow getWindow() {
        return new JSWindow(this.screen.getWindow());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSTimerPool getTimerPool() {
        return this.timerPool;
    }
}
