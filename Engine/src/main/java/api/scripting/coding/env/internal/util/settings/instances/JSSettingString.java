package api.scripting.coding.env.internal.util.settings.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.settings.objects.SettingString;

@JSCodingClass(binding = "JSSettingString", description = "String setting.")
public class JSSettingString implements JSSettingI {
    @JSHideFromDoc
    private final SettingString setting;

    @JSCodingConstructor(description = "Create string setting.", paramNames = {"name", "defaultValue"})
    public JSSettingString(String name, String defaultValue) {
        this.setting = new SettingString(name, defaultValue);
    }

    @JSHideFromDoc
    public JSSettingString(SettingString setting) {
        this.setting = setting;
    }

    @JSCodingFunctionOrMethod(description = "Get value.")
    public String get() {
        return this.setting.getValue();
    }

    @JSCodingFunctionOrMethod(description = "Set value.", paramNames = {"value"})
    public void set(String value) {
        this.setting.setValue(value);
    }

    @JSHideFromDoc
    public SettingString getJavaSetting() {
        return this.setting;
    }
}