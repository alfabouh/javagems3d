package api.scripting.coding.env.internal.game.init.events.ui;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.resources.cache.JSSystemResources;
import api.scripting.coding.env.internal.util.screen.JSScreen;
import api.scripting.coding.env.internal.util.ui.JSUIPanelWrapper;
import api.system.scripting.JavaToJsAPI;
import javagems3d.system.service.collections.Pair;

@JSCodingClass(binding = "JSRegisterUiEvent", description = "...")
public class JSRegisterUiEvent implements JSEventI {
    @JSHideFromDoc private JSScreen jsScreen;

    @JSCodingConstructor(description = "...")
    public JSRegisterUiEvent() {
    }

    @JSHideFromDoc
    public JSRegisterUiEvent(JSScreen jsScreen) {
        this.jsScreen = jsScreen;
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {"uniqueName"})
    public void registerPanel(String uniqueName) {
        JavaToJsAPI.uiContainer.setPanelUI(uniqueName, new JSUIPanelWrapper());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void registerMainMenuPanel(String uniqueName) {
        JavaToJsAPI.uiContainer.setMainMenuPanel(new Pair<>(uniqueName, new JSUIPanelWrapper()));
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSScreen getScreen() {
        return this.jsScreen;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSRegisterUiEvent";
    }
}
