package ru.alfabouh.jgems3d.engine.system.settings.objects;

import ru.alfabouh.jgems3d.engine.system.settings.basic.SettingObject;

public class SettingTrueFalse extends SettingObject {
    private boolean flag;

    public SettingTrueFalse(String name, Object... value) {
        super(name, value);
        this.flag = (boolean) value[0];
    }

    public boolean isFlag() {
        return this.flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}