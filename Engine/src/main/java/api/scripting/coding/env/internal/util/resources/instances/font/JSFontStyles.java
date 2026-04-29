package api.scripting.coding.env.internal.util.resources.instances.font;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;

import java.awt.*;

@JSCodingClass(binding = "JSFontStyles", description = "Enumeration of font styles for scripting, mapping to Java AWT font style constants.")
public enum JSFontStyles {
    @JSCodingField(description = "Plain style") PLAIN(Font.PLAIN),
    @JSCodingField(description = "Italic style") ITALIC(Font.ITALIC),
    @JSCodingField(description = "Bold style") BOLD(Font.BOLD);

    private final int value;
    JSFontStyles(int value) {
        this.value = value;
    }

    @JSCodingFunctionOrMethod(description = "Get the integer value of the font style as used by Java AWT.")
    public int getValue() {
        return this.value;
    }

    @JSHideFromDoc
    @Override
    public String toString() {
        return super.toString();
    }
}
