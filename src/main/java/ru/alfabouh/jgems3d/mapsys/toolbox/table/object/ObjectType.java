package ru.alfabouh.jgems3d.mapsys.toolbox.table.object;

public enum ObjectType {
    PHYSICS_OBJECT("Entities"),
    PROP_OBJECT("Props"),
    MARKER_OBJECT("Markers"),
    LIQUID_OBJECT("Liquids");

    private final String groupName;

    ObjectType(String name) {
        this.groupName = name;
    }

    public String getGroupName() {
        return this.groupName;
    }
}
