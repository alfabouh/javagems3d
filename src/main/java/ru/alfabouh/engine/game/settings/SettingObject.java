package ru.alfabouh.engine.game.settings;

public abstract class SettingObject {
    private final String name;
    private final Object[] defaultValue;

    public SettingObject(String name, Object... defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return this.name;
    }

    public Object[] getDefaultValue() {
        return this.defaultValue;
    }
}
