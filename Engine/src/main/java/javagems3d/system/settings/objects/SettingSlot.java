/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.system.settings.objects;

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
