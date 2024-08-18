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

package ru.jgems3d.toolbox.map_sys.save.objects.object_attributes;

public enum AttributeTarget {
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

    AttributeTarget() {
        this((Object) null);
    }

    AttributeTarget(Object... params) {
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
