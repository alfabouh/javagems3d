package api.scripting.coding.env.internal.util.mapping.tags.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.mapping.tags.properties.JSTagItem;
import javagems3d.system.external.mapping.tags.items.TagFloat;

@JSCodingClass(binding = "JSTagFloat", description = "Floating point tag item.")
public class JSTagFloat {

    @JSHideFromDoc
    private final TagFloat tag;

    @JSCodingConstructor(description = "Create TagFloat with value, min and max.")
    public JSTagFloat(float value, float min, float max) {
        this.tag = new TagFloat(value, min, max);
    }

    @JSCodingConstructor(description = "Wrap existing TagItem as JSTagFloat.")
    public JSTagFloat(JSTagItem tagItem) {
        this.tag = (TagFloat) tagItem.getJavaTagItem();
    }

    @JSHideFromDoc
    public TagFloat getJava() {
        return this.tag;
    }

    @JSHideFromDoc
    public JSTagItem toTagItem() {
        return new JSTagItem(this.tag);
    }

    @JSCodingFunctionOrMethod(description = "Set value of the float tag.", paramNames = {"value"})
    public JSTagFloat setValue(float value) {
        this.tag.setValue(value);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get current value of the float tag.")
    public float getValue() {
        return this.tag.getValue();
    }

    @JSCodingFunctionOrMethod(description = "Get minimum allowed value of the float tag.")
    public float getMin() {
        return this.tag.getMin();
    }

    @JSCodingFunctionOrMethod(description = "Get maximum allowed value of the float tag.")
    public float getMax() {
        return this.tag.getMax();
    }

    @JSCodingFunctionOrMethod(description = "Copy the tag.")
    public JSTagFloat copy() {
        return new JSTagFloat(new JSTagItem(this.tag.copy()));
    }

    @Override
    public String toString() {
        return "JSTagFloat(" + this.tag.getValue() + ", min=" + this.tag.getMin() + ", max=" + this.tag.getMax() + ")";
    }
}