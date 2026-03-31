package api.scripting.coding.env.internal.util.mapping.tags.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.mapping.tags.properties.JSTagItem;
import javagems3d.system.external.mapping.tags.items.TagObjectsList;

@JSCodingClass(binding = "JSTagObjectsList", description = "Objects list tag item.")
public class JSTagObjectsList {

    @JSHideFromDoc
    private final TagObjectsList tag;

    @JSCodingConstructor(description = "Create empty objects list tag.")
    public JSTagObjectsList() {
        this.tag = new TagObjectsList();
    }

    @JSCodingConstructor(description = "Wrap existing TagObjectsList.")
    public JSTagObjectsList(JSTagItem tagItem) {
        this.tag = (TagObjectsList) tagItem.getJavaTagItem();
    }

    @JSHideFromDoc
    public TagObjectsList getJava() {
        return this.tag;
    }

    @JSHideFromDoc
    public JSTagItem toTagItem() {
        return new JSTagItem(this.tag);
    }

    @JSCodingFunctionOrMethod(description = "Set selected object value.", paramNames = {"value"})
    public JSTagObjectsList setValue(int value) {
        this.tag.setValue(value);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get selected object value.")
    public int getValue() {
        return this.tag.getValue();
    }

    @JSCodingFunctionOrMethod(description = "Copy tag.")
    public JSTagObjectsList copy() {
        return new JSTagObjectsList(new JSTagItem(this.tag.copy()));
    }

    @Override
    public String toString() {
        return "JSTagObjectsList(" + this.tag.getValue() + ")";
    }
}