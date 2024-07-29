package ru.jgems3d.engine.system.settings.objects;

import ru.jgems3d.exceptions.JGemsException;

public class SettingTrueFalse extends SettingObject <Boolean> {
    public SettingTrueFalse(String name, boolean defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public Boolean tryParseFromString(String string) {
        return Boolean.parseBoolean(string);
    }
}