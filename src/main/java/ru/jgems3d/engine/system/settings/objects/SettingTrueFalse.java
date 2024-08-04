package ru.jgems3d.engine.system.settings.objects;

public class SettingTrueFalse extends SettingObject <Boolean> {
    public SettingTrueFalse(String name, boolean defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public Boolean tryParseFromString(String string) {
        return Boolean.parseBoolean(string);
    }
}