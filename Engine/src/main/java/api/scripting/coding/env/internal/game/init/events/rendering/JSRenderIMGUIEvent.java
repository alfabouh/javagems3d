package api.scripting.coding.env.internal.game.init.events.rendering;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.controlling.JSController;
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.misc.JSFrameTicking;
import api.scripting.coding.env.internal.util.ui.JSImGui;
import api.scripting.coding.env.internal.util.ui.JSUIDrawer;
import api.scripting.coding.env.internal.util.world.render.screen.JSScreen;
import imgui.ImGui;

@JSCodingClass(binding = "JSRenderIMGUIEvent", description = "...")
public class JSRenderIMGUIEvent implements JSEventI {
    @JSCodingField(description = "Cancellation flag")
    private boolean cancel;

    @JSCodingField(description = "...")
    public JSScreen screen;

    @JSCodingField(description = "...")
    public JSFrameTicking frameTicking;

    @JSCodingField(description = "...")
    public JSController controller;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSRenderIMGUIEvent() {
    }

    public JSRenderIMGUIEvent(JSScreen screen, JSFrameTicking frameTicking, JSController controller) {
        this.screen = screen;
        this.frameTicking = frameTicking;
        this.controller = controller;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSScreen getScreen() {
        return this.screen;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSFrameTicking getFrameTicking() {
        return this.frameTicking;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSController getController() {
        return this.controller;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSRenderUIEvent";
    }
}
