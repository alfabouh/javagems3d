package api.scripting.coding.env.internal.util.mapping.tags.instances.inner;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.external.mapping.tags.items.TagRadioBoolean;

@JSCodingClass(binding = "JSRadioInfo", description = "Option info for JSTagRadioBoolean.")
public class JSRadioInfo {
    private final TagRadioBoolean.Info info;

    @JSCodingConstructor(description = "Create radio boolean info with name and flag.")
    public JSRadioInfo(String name, boolean flag) {
        this.info = new TagRadioBoolean.Info(name, flag);
    }

    @JSCodingConstructor(description = "Wrap existing JSRadioInfo.")
    public JSRadioInfo(TagRadioBoolean.Info info) {
        this.info = info;
    }

    @JSHideFromDoc
    public TagRadioBoolean.Info toJava() {
        return this.info;
    }

    @JSCodingFunctionOrMethod(description = "Get option name.")
    public String getName() {
        return this.info.getName();
    }

    @JSCodingFunctionOrMethod(description = "Check if option is selected.")
    public boolean isSelected() {
        return this.info.isFlag();
    }

    @JSCodingFunctionOrMethod(description = "Set option as selected or not.", paramNames = {"selected"})
    public JSRadioInfo setSelected(boolean selected) {
        this.info.setFlag(selected);
        return this;
    }
}