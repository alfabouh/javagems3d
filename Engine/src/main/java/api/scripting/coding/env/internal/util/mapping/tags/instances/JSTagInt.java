package api.scripting.coding.env.internal.util.mapping.tags.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.mapping.tags.properties.JSTagItem;
import javagems3d.system.external.mapping.tags.items.TagInt;

@JSCodingClass(binding = "JSTagInt", description = "Integer tag item with min/max.")
public class JSTagInt {
    @JSHideFromDoc
    private final TagInt tag;

    @JSCodingConstructor(description = "Create an integer tag with value, min and max.")
    public JSTagInt(int value, int min, int max) {
        this.tag = new TagInt(value, min, max);
    }

    @JSCodingConstructor(description = "Wrap existing TagItem as JSTagInt.")
    public JSTagInt(JSTagItem tagItem) {
        this.tag = (TagInt) tagItem.getJavaTagItem();
    }

    @JSHideFromDoc
    public TagInt getJava() {
        return this.tag;
    }

    @JSCodingFunctionOrMethod(description = "Get current value.")
    public int getValue() {
        return this.tag.getValue();
    }

    @JSCodingFunctionOrMethod(description = "Set current value.", paramNames = {"value"})
    public JSTagInt setValue(int value) {
        this.tag.setValue(value);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get minimum value.")
    public int getMin() {
        return this.tag.getMin();
    }

    @JSCodingFunctionOrMethod(description = "Get maximum value.")
    public int getMax() {
        return this.tag.getMax();
    }

    @JSCodingFunctionOrMethod(description = "Copy the tag.")
    public JSTagInt copy() {
        return new JSTagInt(new JSTagItem(this.tag.copy()));
    }

    @Override
    public String toString() {
        return "JSTagInt{" +
                "value=" + tag.getValue() +
                ", min=" + tag.getMin() +
                ", max=" + tag.getMax() +
                '}';
    }
}