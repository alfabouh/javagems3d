package api.scripting.coding.env.internal.game.init.events.ui;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.misc.JSFunction;
import api.scripting.coding.env.internal.util.resources.cache.JSSystemResources;
import api.scripting.coding.env.internal.util.screen.JSScreen;
import api.scripting.coding.env.internal.util.screen.JSWindow;
import api.scripting.coding.env.internal.util.ui.JSUIDrawer;
import api.scripting.coding.env.internal.util.ui.JSUIPanelWrapper;
import api.system.scripting.JavaToJsAPI;
import logger.Log;

@JSCodingClass(binding = "JSUiBehaviourEvent", description = "...")
public class JSUiBehaviourEvent implements JSEventI {
    @JSHideFromDoc private JSScreen jsScreen;
    @JSHideFromDoc private JSUIDrawer uiDrawer;

    @JSCodingConstructor(description = "...")
    public JSUiBehaviourEvent() {
    }

    @JSHideFromDoc
    public JSUiBehaviourEvent(JSUIDrawer uiDrawer, JSScreen jsScreen) {
        this.jsScreen = jsScreen;
        this.uiDrawer = uiDrawer;
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {"uniqueName", "uiBehaviourTarget", "function"})
    public void registerUiBehaviour(String uniqueName, JSUiBehaviourTargets uiBehaviourTarget, JSFunction function) {
        JSUIPanelWrapper panelWrapper = JavaToJsAPI.uiContainer.getPanelUIMap().get(uniqueName);
        if (panelWrapper == null && uniqueName.equals(JavaToJsAPI.uiContainer.getMainMenuPanel().first())) {
            panelWrapper = JavaToJsAPI.uiContainer.getMainMenuPanel().second();
        }
        if (panelWrapper == null) {
            Log.get().error(uniqueName + " is NULL UI!");
            return;
        }
        switch (uiBehaviourTarget) {
            case ON_DRAW -> panelWrapper.setDrawPanel(function);
            case ON_CONSTRUCT -> panelWrapper.setOnConstruct(function);
            case ON_DESTRUCT ->  panelWrapper.setOnDestruct(function);
            case ON_WINDOW_RESIZED -> panelWrapper.setOnWindowResize(function);
        }
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSWindow getWindow() {
        return this.getScreen().getWindow();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSScreen getScreen() {
        return this.jsScreen;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSUIDrawer getUiDrawer() {
        return this.uiDrawer;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public float getFrameDeltaTime() {
        return this.uiDrawer.getJavaUI().frameDeltaTicks;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSUiBehaviourEvent";
    }
}
