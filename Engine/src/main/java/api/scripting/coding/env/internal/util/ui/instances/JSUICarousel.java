package api.scripting.coding.env.internal.util.ui.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.UICarousel;
import javagems3d.graphics.rendering.ui.jgems_imgui.elements.UISlider;

@JSCodingClass(binding = "JSUICarousel", description = "...")
public class JSUICarousel {
    @JSHideFromDoc private final UICarousel uiCarousel;

    @JSHideFromDoc
    public JSUICarousel(UICarousel uiCarousel) {
        this.uiCarousel = uiCarousel;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSVector2f getPosition() {
        return new JSVector2f(this.uiCarousel.getPosition().x, this.uiCarousel.getPosition().y);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSVector2f getSize() {
        return new JSVector2f(this.uiCarousel.getSize().x, this.uiCarousel.getSize().y);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public UICarousel getJavaUICarousel() {
        return this.uiCarousel;
    }
}
