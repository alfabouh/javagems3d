package ru.alfabouh.jgems3d.mapsys.toolbox.table.object.attributes;

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
    FLOAT_0_1(0.0f, 1.0f),
    FLOAT_0_50(0.0f, 50.0f),
    BOOL,
    DOUBLE,

    STATIC_NO_EDIT;

    private final Object[] params;

    AttributeFlag() {
        this((Object) null);
    }

    AttributeFlag(Object... params) {
        this.params = params;
    }

    @SuppressWarnings("unchecked")
    public <T> T checkAndGet(Class<?> clazz, int i) {
        if (this.getParams() != null && this.getParams().length > i) {
            if (this.getParams()[i].getClass().isAssignableFrom(clazz)) {
                return (T) this.getParams()[i];
            }
        }
        return null;
    }

    public Object[] getParams() {
        return this.params;
    }
}
