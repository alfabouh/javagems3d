package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.attributes;

public enum AttributeFlag {
    COLOR3,
    POSITION_XYZ(1 | 2 | 4),
    ROTATION_XYZ(1 | 2 | 4),
    SCALING_XYZ(1 | 2 | 4),
    POSITION_X(1),
    POSITION_Y(2),
    POSITION_Z(4),
    ROTATION_X(1),
    ROTATION_Y(2),
    ROTATION_Z(4),
    SCALING_X(1),
    SCALING_Y(2),
    SCALING_Z(4),
    STRING,
    INT,
    FLOAT,
    BOOL,
    DOUBLE,

    STATIC_NO_EDIT;

    private final int params;

    AttributeFlag() {
        this(0);
    }

    AttributeFlag(int params) {
        this.params = params;
    }

    public int getParams() {
        return this.params;
    }
}
