package api.scripting.coding.env.internal.util.ui.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.UIPictureStatic;

@JSCodingClass(binding = "JSUIPictureStatic", description = "Wrapper for a static UI picture, exposing position, size, and the underlying Java UIPictureStatic object.")
public class JSUIPictureStatic {
    @JSHideFromDoc private final UIPictureStatic uiPictureStatic;

    @JSHideFromDoc
    public JSUIPictureStatic(UIPictureStatic uiPictureStatic) {
        this.uiPictureStatic = uiPictureStatic;
    }

    @JSCodingFunctionOrMethod(description = "Get the position of the static picture as a JSVector2f.")
    public JSVector2f getPosition() {
        return new JSVector2f(this.uiPictureStatic.getPosition().x, this.uiPictureStatic.getPosition().y);
    }

    @JSCodingFunctionOrMethod(description = "Get the size of the static picture as a JSVector2f.")
    public JSVector2f getSize() {
        return new JSVector2f(this.uiPictureStatic.getScaledSize().x, this.uiPictureStatic.getScaledSize().y);
    }

    @JSCodingFunctionOrMethod(description = "Get the underlying Java UIPictureStatic object.")
    public UIPictureStatic getJavaUIPicture() {
        return this.uiPictureStatic;
    }
}