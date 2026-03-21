package api.scripting.coding.env.internal.util.resources.instances.font;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSHideFromDoc;

import java.awt.*;

@JSCodingClass(binding = "JSFontStyles", description = "...")
public enum JSFontStyles {
    @JSCodingField(description = "PLAIN", paramName = "PLAIN") PLAIN(Font.PLAIN),
    @JSCodingField(description = "ITALIC", paramName = "ITALIC") ITALIC(Font.ITALIC),
    @JSCodingField(description = "BOLD", paramName = "BOLD") BOLD(Font.BOLD);

    private final int value;
    JSFontStyles(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    @JSHideFromDoc
    @Override
    public String toString() {
        return super.toString();
    }
}
