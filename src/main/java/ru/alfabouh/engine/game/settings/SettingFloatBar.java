package ru.alfabouh.engine.game.settings;

import ru.alfabouh.engine.game.Game;

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
