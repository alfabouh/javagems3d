package api.scripting.coding.env.internal.util.settings.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.settings.objects.SettingChooseLanguage;
import javagems3d.system.settings.objects.SettingSlot;

@JSCodingClass(binding = "JSSettingLanguage", description = "Language selection setting.")
public class JSSettingLanguage implements JSSettingI, JSSettingSlotI {
    @JSHideFromDoc private final SettingChooseLanguage setting;

    @JSCodingConstructor(description = "Create language setting.", paramNames = {"name"})
    public JSSettingLanguage(String name) {
        this.setting = new SettingChooseLanguage(name, null);
    }

    @JSHideFromDoc
    public JSSettingLanguage(SettingChooseLanguage setting) {
        this.setting = setting;
    }

    @JSCodingFunctionOrMethod(description = "Get current language name.")
    public String getCurrentName() {
        return this.setting.getCurrentName();
    }

    @JSCodingFunctionOrMethod(description = "Get current index.")
    public int get() {
        return this.setting.getValue();
    }

    @JSCodingFunctionOrMethod(description = "Set language index.", paramNames = {"value"})
    public void set(int value) {
        this.setting.setValue(value);
    }

    @JSHideFromDoc
    public SettingChooseLanguage getJavaSetting() {
        return this.setting;
    }

    @JSHideFromDoc
    @Override
    public SettingSlot getJavaSettingSlot() {
        return this.getJavaSetting();
    }
}