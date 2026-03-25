package api.scripting.coding.env.internal.util.ui.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.UIText;

@JSCodingClass(binding = "JSUIText", description = "Text element with content and size, supporting retrieval of text and dimensions.")
public class JSUIText {
    @JSHideFromDoc private final UIText uiText;

    @JSHideFromDoc
    public JSUIText(UIText uiText) {
        this.uiText = uiText;
    }

    @JSCodingFunctionOrMethod(description = "Get text content")
    public String getText() {
        return this.uiText.getText();
    }

    @JSCodingFunctionOrMethod(description = "Get size of text element")
    public JSVector2f getSize() {
        return new JSVector2f(this.uiText.getSize().x, this.uiText.getSize().y);
    }

    @JSCodingFunctionOrMethod(description = "Get underlying UIText")
    public UIText getJavaUIText() {
        return this.uiText;
    }
}