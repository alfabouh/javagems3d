package api.scripting.coding.env.internal.util.resources.instances.font;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.JSCanBeCachedInMemory;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.JGemsGuiFont;

@JSCodingClass(binding = "JSFont", description = "Wrapper for Java GUI fonts used in scripting, providing access to the underlying JGemsGuiFont for rendering text and UI elements.")
public class JSFont implements JSCanBeCachedInMemory {
    @JSHideFromDoc
    private final JGemsGuiFont guiFont;

    @JSHideFromDoc
    public JSFont(JGemsGuiFont guiFont) {
        this.guiFont = guiFont;
    }

    @JSCodingFunctionOrMethod(description = "Get the underlying Java GUI font object.")
    public JGemsGuiFont getJavaGuiFont() {
        return this.guiFont;
    }
}
