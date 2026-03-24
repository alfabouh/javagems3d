package api.scripting.coding.env.internal.util.ui.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.UIDefaultButton;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.UIAction;

@JSCodingClass(binding = "JSUIDefaultButton", description = "...")
public class JSUIDefaultButton {
    @JSHideFromDoc private final UIDefaultButton uiDefaultButton;

    @JSHideFromDoc
    public JSUIDefaultButton(UIDefaultButton uiDefaultButton) {
        this.uiDefaultButton = uiDefaultButton;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSVector2f getPosition() {
        return new JSVector2f(this.uiDefaultButton.getPosition().x, this.uiDefaultButton.getPosition().y);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSVector2f getSize() {
        return new JSVector2f(this.uiDefaultButton.getSize().x, this.uiDefaultButton.getSize().y);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSUIDefaultButton setOnUnClick(Runnable onUnClick) {
        this.uiDefaultButton.setOnClick(onUnClick::run);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSUIDefaultButton setOnEntered(Runnable onEntered) {
        this.uiDefaultButton.setOnEntered(onEntered::run);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSUIDefaultButton setOnClick(Runnable onClick) {
        this.uiDefaultButton.setOnClick(onClick::run);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSUIDefaultButton setOnInside(Runnable onInside) {
        this.uiDefaultButton.setOnInside(onInside::run);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSUIDefaultButton setOnLeft(Runnable onLeft) {
        this.uiDefaultButton.setOnLeft(onLeft::run);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public UIDefaultButton getJavaUIDefaultButton() {
        return this.uiDefaultButton;
    }
}
