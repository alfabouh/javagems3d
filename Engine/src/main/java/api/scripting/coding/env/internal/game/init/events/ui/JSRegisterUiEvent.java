package api.scripting.coding.env.internal.game.init.events.ui;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.world.render.screen.JSScreen;
import api.scripting.coding.env.internal.util.ui.JSUIPanelWrapper;
import api.system.scripting.JavaToJsAPI;
import javagems3d.system.service.collections.Pair;

@JSCodingClass(binding = "JSRegisterUiEvent", description = "Event for registering UI panels in the game, including main menu and custom panels.")
public class JSRegisterUiEvent implements JSEventI {
    @JSHideFromDoc private JSScreen jsScreen;

    @JSCodingConstructor(description = "Creates a new JSRegisterUiEvent instance.")
    public JSRegisterUiEvent() {
    }

    @JSHideFromDoc
    public JSRegisterUiEvent(JSScreen jsScreen) {
        this.jsScreen = jsScreen;
    }

    @JSCodingFunctionOrMethod(description = "Register a new UI panel by its unique name.", paramNames = {"uniqueName"})
    public void registerPanel(String uniqueName) {
        JavaToJsAPI.uiContainer.setPanelUI(uniqueName, new JSUIPanelWrapper());
    }

    @JSCodingFunctionOrMethod(description = "Register the main menu panel with a unique name.", paramNames = {"uniqueName"})
    public void registerMainMenuPanel(String uniqueName) {
        JavaToJsAPI.uiContainer.setMainMenuPanel(new Pair<>(uniqueName, new JSUIPanelWrapper()));
    }

    @JSCodingFunctionOrMethod(description = "Get the screen associated with this UI registration event.")
    public JSScreen getScreen() {
        return this.jsScreen;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSRegisterUiEvent";
    }
}