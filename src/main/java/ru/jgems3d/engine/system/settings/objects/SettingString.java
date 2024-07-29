package ru.jgems3d.engine.system.settings.objects;

import ru.jgems3d.exceptions.JGemsException;

public class SettingString extends SettingObject <String> {
    public SettingString(String name, String defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public String tryParseFromString(String string) {
        return string;
    }
}
