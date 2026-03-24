package api.scripting.coding.env.internal.util.settings.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.settings.objects.SettingIntSlots;
import javagems3d.system.settings.objects.SettingSlot;

@JSCodingClass(binding = "JSSettingIntSlots", description = "Integer slot-based setting.")
public class JSSettingIntSlots implements JSSettingI, JSSettingSlotI {
    @JSHideFromDoc
    private final SettingIntSlots setting;

    @JSCodingConstructor(description = "Create int slot setting.", paramNames = {"name", "defaultValue", "min", "max"})
    public JSSettingIntSlots(String name, int defaultValue, int min, int max) {
        this.setting = new SettingIntSlots(name, defaultValue, min, max);
    }

    @JSHideFromDoc
    public JSSettingIntSlots(SettingIntSlots setting) {
        this.setting = setting;
    }

    @JSCodingFunctionOrMethod(description = "Add option.", paramNames = {"index", "name", "isI18n"})
    public void add(int index, String name, boolean isI18n) {
        this.setting.addArticle(index, name, isI18n);
    }

    @JSCodingFunctionOrMethod(description = "Get current index.")
    public int get() {
        return this.setting.getValue();
    }

    @JSCodingFunctionOrMethod(description = "Set index.", paramNames = {"value"})
    public void set(int value) {
        this.setting.setValue(value);
    }

    @JSCodingFunctionOrMethod(description = "Get current name.")
    public String getCurrentName() {
        return this.setting.getCurrentName();
    }

    @JSHideFromDoc
    public SettingIntSlots getJavaSetting() {
        return this.setting;
    }

    @JSHideFromDoc
    @Override
    public SettingSlot getJavaSettingSlot() {
        return this.getJavaSetting();
    }
}