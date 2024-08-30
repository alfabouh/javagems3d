/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package ru.jgems3d.engine.system.settings.objects;

public abstract class SettingSlot extends SettingObject<Integer> {
    private final int min;
    private final int max;

    public SettingSlot(String name, int defaultValue, int min, int max) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
    }

    public abstract String getCurrentName();

    public Integer tryParseFromString(String string) {
        return Integer.parseInt(string);
    }

    public int getMax() {
        return this.max;
    }

    public int getMin() {
        return this.min;
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
