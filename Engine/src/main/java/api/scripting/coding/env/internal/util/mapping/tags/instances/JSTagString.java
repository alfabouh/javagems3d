package api.scripting.coding.env.internal.util.mapping.tags.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.mapping.tags.properties.JSTagItem;
import javagems3d.system.external.mapping.tags.items.TagString;

@JSCodingClass(binding = "JSTagString", description = "String tag item.")
public class JSTagString {
    @JSHideFromDoc
    private final TagString tag;

    @JSCodingConstructor(description = "Create string tag.", paramNames = {"text"})
    public JSTagString(String text) {
        this.tag = new TagString(text);
    }

    @JSCodingConstructor(description = "Wrap existing TagString.", paramNames = {"tagItem"})
    public JSTagString(JSTagItem tagItem) {
        this.tag = (TagString) tagItem.getJavaTagItem();
    }

    @JSHideFromDoc
    public TagString getJava() {
        return this.tag;
    }

    @JSHideFromDoc
    public JSTagItem toTagItem() {
        return new JSTagItem(this.tag);
    }

    @JSCodingFunctionOrMethod(description = "Set text value.", paramNames = {"text"})
    public JSTagString setText(String text) {
        this.tag.setText(text);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get text value.")
    public String getText() {
        return this.tag.getText();
    }

    @JSCodingFunctionOrMethod(description = "Copy tag.")
    public JSTagString copy() {
        return new JSTagString(this.tag.getText());
    }

    @Override
    public String toString() {
        return "JSTagString(" + this.tag.getText() + ")";
    }
}