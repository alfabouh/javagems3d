package api.scripting.coding.env.internal.util.ui.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.UIPictureSizable;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.UIPictureStatic;

@JSCodingClass(binding = "JSUIPictureSizable", description = "UI element representing an image with configurable size.")
public class JSUIPictureSizable {
    @JSHideFromDoc private final UIPictureSizable uiPictureSizable;

    @JSHideFromDoc
    public JSUIPictureSizable(UIPictureSizable uiPictureSizable) {
        this.uiPictureSizable = uiPictureSizable;
    }

    @JSCodingFunctionOrMethod(description = "Get image position.")
    public JSVector2f getPosition() {
        return new JSVector2f(this.uiPictureSizable.getPosition().x, this.uiPictureSizable.getPosition().y);
    }

    @JSCodingFunctionOrMethod(description = "Get image size.")
    public JSVector2f getSize() {
        return new JSVector2f(this.uiPictureSizable.getSize().x, this.uiPictureSizable.getSize().y);
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java UI image instance.")
    public UIPictureSizable getJavaUIPicture() {
        return this.uiPictureSizable;
    }
}