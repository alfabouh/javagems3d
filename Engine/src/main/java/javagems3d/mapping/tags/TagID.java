package javagems3d.mapping.tags;

import java.util.Objects;

public final class TagID {
    private final String id;
    private final String description;

    public TagID(String id, String description) {
        this.id = id;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TagID)) {
            return false;
        }
        TagID tagID = (TagID) o;
        return Objects.equals(this.id, tagID.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

    public String getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public static class DEFAULT {
        public static final TagID IS_STATIC = new TagID("is_static_bl", "Static");
        public static final TagID IS_DYNAMIC = new TagID("is_dynamic_bl", "Dynamic");

        public static final TagID POSITION_X = new TagID("position_x", "Translate X");
        public static final TagID POSITION_Y = new TagID("position_y", "Translate Y");
        public static final TagID POSITION_Z = new TagID("position_z", "Translate Z");
        public static final TagID POSITION_XY = new TagID("position_xy", "Translate XY");
        public static final TagID POSITION_XZ = new TagID("position_xz", "Translate XZ");
        public static final TagID POSITION_YZ = new TagID("position_yz", "Translate YZ");
        public static final TagID POSITION_XYZ = new TagID("position_xyz", "Translate XYZ");

        public static final TagID ROTATION_X = new TagID("rotation_x", "Rotation X");
        public static final TagID ROTATION_Y = new TagID("rotation_y", "Rotation Y");
        public static final TagID ROTATION_Z = new TagID("rotation_z", "Rotation Z");
        public static final TagID ROTATION_XY = new TagID("rotation_xy", "Rotation XY");
        public static final TagID ROTATION_XZ = new TagID("rotation_xz", "Rotation XZ");
        public static final TagID ROTATION_YZ = new TagID("rotation_yz", "Rotation YZ");
        public static final TagID ROTATION_XYZ = new TagID("rotation_xyz", "Rotation XYZ");

        public static final TagID SCALING_X = new TagID("scaling_x", "Scaling X");
        public static final TagID SCALING_Y = new TagID("scaling_y", "Scaling Y");
        public static final TagID SCALING_Z = new TagID("scaling_z", "Scaling Z");
        public static final TagID SCALING_XY = new TagID("scaling_xy", "Scaling XY");
        public static final TagID SCALING_XZ = new TagID("scaling_xz", "Scaling XZ");
        public static final TagID SCALING_YZ = new TagID("scaling_yz", "Scaling YZ");
        public static final TagID SCALING_XYZ = new TagID("scaling_xyz", "Scaling XYZ");

        public static final TagID COLOR3 = new TagID("color3", "Color3");
        public static final TagID COLOR4 = new TagID("color4", "Color4");
    }
}
