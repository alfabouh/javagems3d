package api.scripting.coding.env.internal.util.mapping.tags.properties;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.external.mapping.tags.base.ColorMode;

@JSCodingClass(binding = "JSColorMode", description = "Color mode enum.")
public enum JSColorMode {

    @JSCodingField(description = "RGB color (3 components)")
    COLOR3(ColorMode.COLOR3),

    @JSCodingField(description = "RGBA color (4 components)")
    COLOR4(ColorMode.COLOR4);

    @JSHideFromDoc
    private final ColorMode mode;

    @JSHideFromDoc
    JSColorMode(ColorMode mode) {
        this.mode = mode;
    }

    @JSHideFromDoc
    public ColorMode getJava() {
        return this.mode;
    }
}