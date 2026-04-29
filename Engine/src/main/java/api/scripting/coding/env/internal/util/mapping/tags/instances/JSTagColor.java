package api.scripting.coding.env.internal.util.mapping.tags.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.mapping.tags.properties.JSColorMode;
import api.scripting.coding.env.internal.util.mapping.tags.properties.JSTagItem;
import api.scripting.coding.env.internal.util.math.JSVector4f;
import javagems3d.system.external.mapping.tags.base.ColorMode;
import javagems3d.system.external.mapping.tags.items.TagColor;
import org.joml.Vector4f;

@JSCodingClass(binding = "JSTagColor", description = "Color tag item with ColorMode and Vector4f.")
public class JSTagColor {
    @JSHideFromDoc
    private final TagColor tag;

    @JSCodingConstructor(description = "Create a color tag with ColorMode and Vector4f.")
    public JSTagColor(JSColorMode colorMode, JSVector4f colorVector) {
        this.tag = new TagColor(colorMode.getJava(), colorVector.getJavaVector4f());
    }

    @JSCodingConstructor(description = "Wrap existing TagItem as JSTagColor.")
    public JSTagColor(JSTagItem tagItem) {
        this.tag = (TagColor) tagItem.getJavaTagItem();
    }

    @JSHideFromDoc
    public TagColor getJava() {
        return this.tag;
    }

    @JSCodingFunctionOrMethod(description = "Get the ColorMode.")
    public JSColorMode getColorMode() {
        return this.tag.getColorMode() == ColorMode.COLOR3 ? JSColorMode.COLOR3 : JSColorMode.COLOR4;
    }

    @JSCodingFunctionOrMethod(description = "Get the current color vector.")
    public JSVector4f getColorVector() {
        return new JSVector4f(this.tag.getColorVector());
    }

    @JSCodingFunctionOrMethod(description = "Set the color vector.", paramNames = {"color"})
    public JSTagColor setColor(JSVector4f color) {
        this.tag.setColor(color.getJavaVector4f());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Copy the tag.")
    public JSTagColor copy() {
        return new JSTagColor(new JSTagItem(this.tag.copy()));
    }

    @Override
    public String toString() {
        Vector4f c = tag.getColorVector();
        return "JSTagColor{" +
                "colorVector=(" + c.x + "," + c.y + "," + c.z + "," + c.w + ")" +
                ", colorMode=" + tag.getColorMode() +
                '}';
    }
}