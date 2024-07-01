package ru.alfabouh.jgems3d.mapsys.toolbox.table.object.attributes;

public enum AttributeIDS {
    IS_STATIC("is_static", "Is Static"),
    COLOR("color", "Color"),
    POSITION_XYZ("position_xyz", "Position"),
    ROTATION_XYZ("rotation_xyz", "Rotation"),
    SCALING_XYZ("scaling_xyz", "Scaling"),
    SOUND("sound", "Sound Name"),
    SOUND_VOL("sound_vol", "Sound Volume");

    private final String id;
    private final String description;
    AttributeIDS(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }
}
