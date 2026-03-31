package api.scripting.coding.env.internal.util.mapping.tags.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.mapping.tags.properties.JSTagItem;
import javagems3d.system.external.mapping.tags.items.TagCheckBoolean;

@JSCodingClass(binding = "JSTagCheckBoolean", description = "Boolean check tag item.")
public class JSTagCheckBoolean {
    @JSHideFromDoc
    private final TagCheckBoolean tag;

    @JSCodingConstructor(description = "Create a boolean check tag with initial value.")
    public JSTagCheckBoolean(boolean flag) {
        this.tag = new TagCheckBoolean(flag);
    }

    @JSCodingConstructor(description = "Wrap existing TagItem as JSTagCheckBoolean.")
    public JSTagCheckBoolean(JSTagItem tagItem) {
        this.tag = (TagCheckBoolean) tagItem.getJavaTagItem();
    }

    @JSHideFromDoc
    public TagCheckBoolean getJava() {
        return this.tag;
    }

    @JSCodingFunctionOrMethod(description = "Set the flag value.", paramNames = {"flag"})
    public JSTagCheckBoolean setFlag(boolean flag) {
        this.tag.setFlag(flag);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get the flag value.")
    public boolean isFlag() {
        return this.tag.isFlag();
    }

    @JSCodingFunctionOrMethod(description = "Copy the tag.")
    public JSTagCheckBoolean copy() {
        return new JSTagCheckBoolean(new JSTagItem(this.tag.copy()));
    }

    @Override
    public String toString() {
        return "JSTagCheckBoolean{" +
                "flag=" + tag.isFlag() +
                '}';
    }
}