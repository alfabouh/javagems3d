package ru.alfabouh.jgems3d.engine.system.settings.objects;

import ru.alfabouh.jgems3d.engine.system.settings.basic.SettingObject;

public class SettingString extends SettingObject {
    private String value;

    public SettingString(String name, Object... value) {
        super(name, value);
        this.value = (String) value[0];
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
