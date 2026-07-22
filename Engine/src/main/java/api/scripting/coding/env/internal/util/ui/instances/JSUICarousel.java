package api.scripting.coding.env.internal.util.ui.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.UICarousel;

@JSCodingClass(binding = "JSUICarousel", description = "UI carousel component with position and size properties.")
public class JSUICarousel {
    @JSHideFromDoc private final UICarousel uiCarousel;

    @JSHideFromDoc
    public JSUICarousel(UICarousel uiCarousel) {
        this.uiCarousel = uiCarousel;
    }

    @JSCodingFunctionOrMethod(description = "Get current position")
    public JSVector2f getPosition() {
        return new JSVector2f(this.uiCarousel.getPosition().x, this.uiCarousel.getPosition().y);
    }

    @JSCodingFunctionOrMethod(description = "Get current size")
    public JSVector2f getSize() {
        return new JSVector2f(this.uiCarousel.getScaledSize().x, this.uiCarousel.getScaledSize().y);
    }

    @JSCodingFunctionOrMethod(description = "Get the carousel object")
    public UICarousel getJavaUICarousel() {
        return this.uiCarousel;
    }
}