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
