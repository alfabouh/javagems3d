package ru.jgems3d.engine.system.settings.objects;

public class SettingString extends SettingObject <String> {
    public SettingString(String name, String defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public String tryParseFromString(String string) {
        return string;
    }
}
