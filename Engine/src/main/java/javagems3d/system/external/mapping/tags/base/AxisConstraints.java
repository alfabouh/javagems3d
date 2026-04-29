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