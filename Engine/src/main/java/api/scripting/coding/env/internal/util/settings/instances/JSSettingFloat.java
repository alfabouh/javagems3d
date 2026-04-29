package api.scripting.coding.env.internal.util.settings.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.settings.objects.SettingFloatBar;

@JSCodingClass(binding = "JSSettingFloat", description = "Float setting.")
public class JSSettingFloat implements JSSettingI {
    @JSHideFromDoc
    private final SettingFloatBar setting;

    @JSCodingConstructor(description = "Create float setting.", paramNames = {"name", "defaultValue"})
    public JSSettingFloat(String name, float defaultValue) {
        this.setting = new SettingFloatBar(name, defaultValue);
    }

    @JSHideFromDoc
    public JSSettingFloat(SettingFloatBar setting) {
        this.setting = setting;
    }

    @JSCodingFunctionOrMethod(description = "Get value.")
    public float get() {
        return this.setting.getValue();
    }

    @JSCodingFunctionOrMethod(description = "Set value.", paramNames = {"value"})
    public void set(float value) {
        this.setting.setValue(value);
    }

    @JSHideFromDoc
    public SettingFloatBar getJavaSetting() {
        return this.setting;
    }
}