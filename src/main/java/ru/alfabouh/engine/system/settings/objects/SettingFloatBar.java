package ru.alfabouh.engine.system.settings.objects;

import ru.alfabouh.engine.system.settings.basic.SettingObject;

public class SettingFloatBar extends SettingObject {
    private float value;

    public SettingFloatBar(String name, Object... value) {
        super(name, value);
        this.value = (float) value[0];
    }

    public float getValue() {
        return this.value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
