package ru.alfabouh.jgems3d.engine.system.settings.basic;

public abstract class SettingSlot extends SettingObject {
    private int value;
    private int min;
    private int max;

    public SettingSlot(String name, Object... value) {
        super(name, value);
        if (value != null) {
            this.value = (int) value[0];
            this.min = (int) value[1];
            this.max = (int) value[2];
        }
    }

    public abstract String getCurrentName();

    public int getMax() {
        return this.max;
    }

    public int getMin() {
        return this.min;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
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
}
