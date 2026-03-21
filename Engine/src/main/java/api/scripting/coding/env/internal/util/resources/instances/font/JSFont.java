package api.scripting.coding.env.internal.util.resources.instances.font;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.JSCanBeCachedInMemory;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font.JGemsGuiFont;

@JSCodingClass(binding = "JSFont", description = "...")
public class JSFont implements JSCanBeCachedInMemory {
    @JSHideFromDoc
    private final JGemsGuiFont guiFont;

    public JSFont(JGemsGuiFont guiFont) {
        this.guiFont = guiFont;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JGemsGuiFont getJavaGuiFont() {
        return this.guiFont;
    }
}
