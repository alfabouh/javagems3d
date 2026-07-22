package api.scripting.coding.env.internal.util.ui.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.UISlider;

@JSCodingClass(binding = "JSUISlider", description = "Wrapper for a UI slider component, exposing position, size, and underlying Java UISlider object.")
public class JSUISlider {
    @JSHideFromDoc private final UISlider uiSlider;

    @JSHideFromDoc
    public JSUISlider(UISlider uiSlider) {
        this.uiSlider = uiSlider;
    }

    @JSCodingFunctionOrMethod(description = "Get the position of the slider as a JSVector2f.")
    public JSVector2f getPosition() {
        return new JSVector2f(this.uiSlider.getPosition().x, this.uiSlider.getPosition().y);
    }

    @JSCodingFunctionOrMethod(description = "Get the size of the slider as a JSVector2f.")
    public JSVector2f getSize() {
        return new JSVector2f(this.uiSlider.getScaledSize().x, this.uiSlider.getScaledSize().y);
    }

    @JSCodingFunctionOrMethod(description = "Get the underlying Java UISlider object.")
    public UISlider getJavaUISlider() {
        return this.uiSlider;
    }
}
