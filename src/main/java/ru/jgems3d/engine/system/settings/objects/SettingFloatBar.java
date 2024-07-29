package ru.jgems3d.engine.system.settings.objects;

import org.jetbrains.annotations.NotNull;

public class SettingFloatBar extends SettingObject <Float> {
    public SettingFloatBar(String name, float defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public Float tryParseFromString(String string) {
        return Float.parseFloat(string);
    }
}
