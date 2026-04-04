package api.scripting.coding.env.internal.game.init.events.rendering.ui;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.misc.JSFunction;
import api.scripting.coding.env.internal.util.world.render.screen.JSScreen;
import api.scripting.coding.env.internal.util.world.render.screen.JSWindow;
import api.scripting.coding.env.internal.util.ui.JSUIDrawer;
import api.scripting.coding.env.internal.util.ui.JSUIPanelWrapper;
import api.system.scripting.JavaToJsAPI;
import logger.Log;

@JSCodingClass(binding = "JSUiBehaviourEvent", description = "Event used to register UI panel behaviours such as draw, lifecycle and window events.")
public class JSUiBehaviourEvent implements JSEventI {
    @JSHideFromDoc private JSScreen jsScreen;
    @JSHideFromDoc private JSUIDrawer uiDrawer;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSUiBehaviourEvent() {
    }

    @JSHideFromDoc
    public JSUiBehaviourEvent(JSUIDrawer uiDrawer, JSScreen jsScreen) {
        this.jsScreen = jsScreen;
        this.uiDrawer = uiDrawer;
    }

    @JSCodingFunctionOrMethod(description = "Bind function to UI panel behaviour (draw, construct, destruct, resize).", paramNames = {"uniqueName", "uiBehaviourTarget", "function"})
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
            case ON_DESTRUCT -> panelWrapper.setOnDestruct(function);
            case ON_WINDOW_RESIZED -> panelWrapper.setOnWindowResize(function);
        }
    }

    @JSCodingFunctionOrMethod(description = "Get current window.")
    public JSWindow getWindow() {
        return this.getScreen().getWindow();
    }

    @JSCodingFunctionOrMethod(description = "Get current screen.")
    public JSScreen getScreen() {
        return this.jsScreen;
    }

    @JSCodingFunctionOrMethod(description = "Get UI drawer for rendering elements.")
    public JSUIDrawer getUiDrawer() {
        return this.uiDrawer;
    }

    @JSCodingFunctionOrMethod(description = "Get frame delta time.")
    public float getFrameDeltaTime() {
        return this.uiDrawer.getJavaUI().frameDeltaTicks;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSUiBehaviourEvent";
    }
}