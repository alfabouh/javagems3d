package javagems3d.mapping.tags.base;

public enum AxisConstraints {
    NULL(0),
    AXIS_X(1),
    AXIS_Y(2),
    AXIS_Z(4),
    AXIS_XY(1 | 2),
    AXIS_XZ(1 | 4),
    AXIS_YZ(2 | 4),
    AXIS_XYZ(1 | 2 | 4);

    private final int flag;

    AxisConstraints(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return this.flag;
    }
}