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

package javagems3d.system.external.mapping.tags.base;

public enum AxisConstraints {
    NONE(0),
    AXIS_X(1),
    AXIS_Y(2),
    AXIS_Z(4),
    AXIS_XY(1 | 2),
    AXIS_XZ(1 | 4),
    AXIS_YZ(2 | 4),
    AXIS_XYZ(1 | 2 | 4);

    private final int flag;

    public static boolean CHECK(int flag, AxisConstraints constraint) {
        return (flag & constraint.getFlag()) != 0;
    }

    public static AxisConstraints GET(boolean x, boolean y, boolean z) {
        int i = 0;
        if (x) {
            i |= 1;
        }
        if (y) {
            i |= 2;
        }
        if (z) {
            i |= 4;
        }
        for (AxisConstraints axisConstraints : AxisConstraints.values()) {
            if (i == axisConstraints.getFlag()) {
                return axisConstraints;
            }
        }
        return NONE;
    }

    AxisConstraints(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return this.flag;
    }
}