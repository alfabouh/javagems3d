package api.scripting.coding.env.internal.util.mapping.tags.properties;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.external.mapping.tags.items.TagItem;

@JSCodingClass(binding = "JSTagItem", description = "Wrapper for TagItem.")
public class JSTagItem {
    @JSHideFromDoc
    private final TagItem tagItem;

    @JSCodingConstructor(description = "Create tag wrapper.", paramNames = {"tagItem"})
    public JSTagItem(TagItem tagItem) {
        this.tagItem = tagItem;
    }

    @JSHideFromDoc
    public TagItem getJavaTagItem() {
        return this.tagItem;
    }

    @JSCodingFunctionOrMethod(description = "Get tag type string.")
    public String getType() {
        return this.tagItem.getTypeString();
    }

    @Override
    public String toString() {
        return "JSTagItem(" + this.tagItem.getTypeString() + ")";
    }
}