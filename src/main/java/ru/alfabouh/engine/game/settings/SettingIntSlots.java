package ru.alfabouh.engine.game.settings;

import java.util.HashMap;
import java.util.Map;

public class SettingIntSlots extends SettingObject {
    private int value;
    private final int min;
    private final int max;
    private final Map<Integer, String> intNames;

    public SettingIntSlots(String name, Object... value) {
        super(name, value);
        this.intNames = new HashMap<>();
        this.value = (int) value[0];
        this.min = (int) value[1];
        this.max = (int) value[2];
    }

    public void addName(int i, String name) {
        this.intNames.put(i, name);
    }

    public String getName(int i) {
        return this.intNames.containsKey(i) ? this.intNames.get(i) : Integer.toString(i);
    }

    public String getCurrentName() {
        return this.getName(this.getValue());
    }

    public int getMax() {
        return this.max;
    }

    public int getMin() {
        return this.min;
    }

    public int getValue() {
        return this.value;
    }

    public boolean goRight() {
        if (this.getValue() < this.getMax()) {
            this.setValue(this.getValue() + 1);
            return true;
        }
        return false;
    }

    public boolean goLeft() {
        if (this.getValue() > this.getMin()) {
            this.setValue(this.getValue() - 1);
            return true;
        }
        return false;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
