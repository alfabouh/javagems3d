package ru.alfabouh.jgems3d.proxy.mapsys.toolbox.table.object.attributes;

public enum AttributeIDS {
    IS_STATIC("is_static", "Is Static"),
    COLOR("color", "Color"),
    ROTATION_Y("rotation_y", "Rotation Y");

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