package api.scripting.coding.env.internal.util.ui.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.UIText;

@JSCodingClass(binding = "JSUIText", description = "...")
public class JSUIText {
    @JSHideFromDoc private final UIText uiText;

    @JSHideFromDoc
    public JSUIText(UIText uiText) {
        this.uiText = uiText;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public String getText() {
        return this.uiText.getText();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSVector2f getSize() {
        return new JSVector2f(this.uiText.getSize().x, this.uiText.getSize().y);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public UIText getJavaUIText() {
        return this.uiText;
    }
}
