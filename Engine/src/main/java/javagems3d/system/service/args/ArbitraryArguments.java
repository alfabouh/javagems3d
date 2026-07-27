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

package javagems3d.system.service.args;

import org.jetbrains.annotations.Nullable;

public final class ArbitraryArguments {
    private final Object[] objects;

    private ArbitraryArguments(Object... objects) {
        this.objects = objects;
    }

    public static ArbitraryArguments empty() {
        return new ArbitraryArguments();
    }

    public static ArbitraryArguments pass(Object... objects) {
        return new ArbitraryArguments(objects);
    }

    public Object[] getObjects() {
        return this.objects;
    }

    public Getter getterFunc() {
        return new Getter();
    }

    public class Getter {
        private Getter() {
        }

        public boolean checkLength(int expected) {
            return ArbitraryArguments.this.getObjects().length == expected;
        }

        public boolean checkRowByTypes(Class<?>... argsByClassesRow) {
            if (!this.checkLength(argsByClassesRow.length)) {
                return false;
            }

            for (int i = 0; i < ArbitraryArguments.this.getObjects().length; i++) {
                Object o = ArbitraryArguments.this.getObjects()[i];
                if (!o.getClass().isAssignableFrom(argsByClassesRow[i])) {
                    return false;
                }
            }

            return true;
        }

        @SuppressWarnings("all")
        @Nullable
        public <T> T getObject(int arrIdx) {
            if (arrIdx < 0 || arrIdx >= ArbitraryArguments.this.getObjects().length) {
                return null;
            }
            try {
                return (T) ArbitraryArguments.this.getObjects()[arrIdx];
            } catch (ClassCastException e) {
                return null;
            }
        }
    }
}
