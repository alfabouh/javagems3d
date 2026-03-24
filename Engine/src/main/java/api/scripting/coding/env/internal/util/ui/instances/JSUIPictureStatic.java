package api.scripting.coding.env.internal.util.ui.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.UIPictureStatic;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.UIText;

@JSCodingClass(binding = "JSUIPictureStatic", description = "...")
public class JSUIPictureStatic {
    @JSHideFromDoc private final UIPictureStatic uiPictureStatic;

    @JSHideFromDoc
    public JSUIPictureStatic(UIPictureStatic uiPictureStatic) {
        this.uiPictureStatic = uiPictureStatic;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSVector2f getPosition() {
        return new JSVector2f(this.uiPictureStatic.getPosition().x, this.uiPictureStatic.getPosition().y);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSVector2f getSize() {
        return new JSVector2f(this.uiPictureStatic.getSize().x, this.uiPictureStatic.getSize().y);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public UIPictureStatic getJavaUIPicture() {
        return this.uiPictureStatic;
    }
}
