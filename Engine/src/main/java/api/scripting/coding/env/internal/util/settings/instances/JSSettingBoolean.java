package api.scripting.coding.env.internal.util.settings.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.settings.objects.SettingTrueFalse;

@JSCodingClass(binding = "JSSettingBoolean", description = "Boolean setting (true/false).")
public class JSSettingBoolean implements JSSettingI {
    @JSHideFromDoc
    private final SettingTrueFalse setting;

    @JSCodingConstructor(description = "Create boolean setting.", paramNames = {"name", "defaultValue"})
    public JSSettingBoolean(String name, boolean defaultValue) {
        this.setting = new SettingTrueFalse(name, defaultValue);
    }

    @JSHideFromDoc
    public JSSettingBoolean(SettingTrueFalse setting) {
        this.setting = setting;
    }

    @JSCodingFunctionOrMethod(description = "Get current value.")
    public boolean get() {
        return this.setting.getValue();
    }

    @JSCodingFunctionOrMethod(description = "Set value.", paramNames = {"value"})
    public void set(boolean value) {
        this.setting.setValue(value);
    }

    @JSHideFromDoc
    public SettingTrueFalse getJavaSetting() {
        return this.setting;
    }
}