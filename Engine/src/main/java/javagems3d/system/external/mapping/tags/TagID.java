package javagems3d.system.external.mapping.tags;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class TagID {
    private final String id;
    private String normalName;
    private String toolTip;

    public TagID(@NotNull String id, @NotNull String normalName, @Nullable String toolTip) {
        this.id = id;
        this.normalName = normalName;
        this.toolTip = toolTip;
    }

    public TagID(@NotNull String id, @NotNull String normalName) {
        this(id, normalName, null);
    }

    public TagID(@NotNull String id) {
        this(id, "", null);
    }

    public TagID(@NotNull TagID id, @NotNull String normalName, @Nullable String toolTip) {
        this(id.getId(), normalName, toolTip);
    }

    public TagID(@NotNull TagID id, @NotNull String normalName) {
        this(id.getId(), normalName, id.getToolTip());
    }

    public TagID(@NotNull TagID id) {
        this(id.getId(), id.getNormalName(), id.getToolTip());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TagID tagID)) {
            return false;
        }
        return Objects.equals(this.id, tagID.id);
    }

    public TagID setNormalName(String normalName) {
        this.normalName = normalName;
        return this;
    }

    public TagID setToolTip(String toolTip) {
        this.toolTip = toolTip;
        return this;
    }

    public String getToolTip() {
        return this.toolTip;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

    public String getId() {
        return this.id;
    }

    public String getNormalName() {
        return this.normalName;
    }

    public static class DEFAULT {
        public static final TagID PHYSICS_STATE = new TagID("phys_state", "Physics Type", "Determines the physical condition of the object.");
        public static final TagID DIRECT_INDIRECT_RENDERING = new TagID("dir_indir_render", "Rendering Type", "Determines the rendering algorithm of the object. " +
                "\nDirect is simple algorithm (one object=one CPU->GPU call, less memory usage, less performance, if scene has a lot of objects) " +
                "\nIndirect is complex algorithm (all objects=one CPU-GPU call, much more memory usage, more performance with lots of objects)");

        public static final TagID MARKER_STRING_ID = new TagID("marker_string_id", "User String ID", "Object's id");

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

        public static final TagID COLOR3 = new TagID("color3", "Color RGB");
        public static final TagID COLOR4 = new TagID("color4", "Color RGBA");

        public static final TagID SHADOW_MAP = new TagID("shadow_map_toggle1", "Shadow Map", "(Def. Tag) Toggles shadow map on this light source. Warn! Engine supports only 3 point light shadow map rendered at once.");
        public static final TagID BRIGHTNESS = new TagID("brightness", "Brightness");
        public static final TagID OBJECT_LIST = new TagID("obj_list_selected", "Select Object");

        public static final TagID FLOAT2 = new TagID("float2", "2 Values");
        public static final TagID FLOAT3 = new TagID("float3", "3 Values");
        public static final TagID FLOAT4 = new TagID("float4", "4 Values");
    }
}
