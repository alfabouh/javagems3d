package api.scripting.coding.env.internal.util.ui.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.UIDefaultButton;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.UISlider;

@JSCodingClass(binding = "JSUISlider", description = "...")
public class JSUISlider {
    @JSHideFromDoc private final UISlider uiSlider;

    @JSHideFromDoc
    public JSUISlider(UISlider uiSlider) {
        this.uiSlider = uiSlider;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSVector2f getPosition() {
        return new JSVector2f(this.uiSlider.getPosition().x, this.uiSlider.getPosition().y);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSVector2f getSize() {
        return new JSVector2f(this.uiSlider.getSize().x, this.uiSlider.getSize().y);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public UISlider getJavaUISlider() {
        return this.uiSlider;
    }
}
