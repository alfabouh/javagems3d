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

        public static final TagID SOUND_VOLUME = new TagID("sound_volume", "Sound Volume", "Controls the playback volume of the sound");
        public static final TagID SOUND_PITCH = new TagID("sound_pitch", "Sound Pitch", "Controls the playback pitch of the sound");
        public static final TagID SOUND_DISTANCE = new TagID("sound_distance", "Sound Distance", "Maximum distance for 3D sound attenuation. If value < 0, sound is played as mono (non-positional)");
        public static final TagID SOUND_PATH = new TagID("sound_path", "Sound", "Path to the sound resource inside game resources");

        public static final TagID PARTICLE_NORMALIZE_Y = new TagID("particle_normalize_y", "Normalize Y Rotation", "If enabled, particle billboard rotates only around Y axis");
        public static final TagID PARTICLE_SPAWN_POS_OFFSET = new TagID("particle_spawn_pos_offset", "Particle Spawn Position Offset", "Base positional offset applied to particle spawn position");
        public static final TagID TEXTURE_PATH = new TagID("texture_path", "Texture", "Path to the texture resource inside game resources");
        public static final TagID TEXTURE_PATH_EMISSIVE = new TagID("texture_path_em", "Texture Emissive", "Path to the emissive texture resource inside game resources");

        public static final TagID TEXTURE_RESOURCE = new TagID("texture_resource", "Texture", "Path to the particle texture resource");
        public static final TagID PARTICLE_RESPAWN_TIME = new TagID("particle_respawn_time", "Respawn Time", "Time between particle spawns. If value is less or equal to 0, only one particle can exist at the same time and a new particle spawns only after previous disappears");
        public static final TagID PARTICLE_RESPAWN_TIME_RANDOM_OFFSET_RANGE = new TagID("particle_respawn_time_random_offset_range", "Respawn Time Random", "Random additional offset range applied to particle respawn time");
        public static final TagID PARTICLE_LIFE_TIME = new TagID("particle_life_time", "Lifetime", "Particle lifetime in seconds. If value is less or equal to 0, particle lives infinitely");
        public static final TagID PARTICLE_LIFE_TIME_RANDOM_OFFSET_RANGE = new TagID("particle_life_time_random_offset_range", "Lifetime Random", "Random additional offset range applied to particle lifetime");
        public static final TagID PARTICLE_RANDOM_SPAWN_POS_OFFSET_RANGE = new TagID("particle_random_spawn_pos_offset_range", "Spawn Pos Random", "Random positional offset range applied on particle spawn");
        public static final TagID PARTICLE_RANDOM_SPAWN_SCALING_OFFSET_RANGE = new TagID("particle_random_spawn_scaling_offset_range", "Spawn Scale Random", "Random scale offset range applied on particle spawn");
        public static final TagID PARTICLE_BASIC_VELOCITY = new TagID("particle_basic_velocity", "Velocity", "Base particle movement velocity");
        public static final TagID PARTICLE_BASIC_VELOCITY_RANDOM_OFFSET_RANGE = new TagID("particle_basic_velocity_random_offset_range", "Velocity Random", "Random additional offset range applied to particle velocity");
        public static final TagID PARTICLE_BASIC_ACCELERATION = new TagID("particle_basic_acceleration", "Acceleration", "Base particle acceleration multiplier");
        public static final TagID PARTICLE_GRAVITY = new TagID("particle_gravity", "Gravity", "Gravity force applied to particles");
        public static final TagID PARTICLE_BASE_SCALE = new TagID("particle_base_scale", "Scale", "Base particle scale");
        public static final TagID PARTICLE_COLOR_MASK = new TagID("particle_color_mask", "Color Mask", "Particle color multiplier mask");
        public static final TagID PARTICLE_BLENDING_TRANSPARENCY = new TagID("particle_blending_transparency", "Transparency", "Particle blending transparency factor");
        public static final TagID PARTICLE_ALPHA_DISCARD = new TagID("particle_alpha_discard", "Alpha Discard", "Alpha discard threshold value");
        public static final TagID PARTICLE_EMISSIVE_COLOR = new TagID("particle_emissive_color", "Emissive Color", "Particle emissive color mask");
        public static final TagID PARTICLE_EMISSIVE_FACTOR_STRENGTH = new TagID("particle_emissive_factor_strength", "Emissive Strength", "Particle emissive intensity strength");
        public static final TagID CELLS_X = new TagID("cells_x", "Cells X", "Amount of texture atlas cells along X axis");
        public static final TagID CELLS_Y = new TagID("cells_y", "Cells Y", "Amount of texture atlas cells along Y axis");
        public static final TagID MAX_SPRITES = new TagID("max_sprites", "Max Sprites", "Maximum amount of texture atlas sprites used by particles");
        public static final TagID PARTICLE_FADE_OUT = new TagID("particle_fade_out", "Fade Out", "Smoothly fades particle transparency before particle disappears");
        public static final TagID PARTICLE_ANIMATION_LOOPED = new TagID("particle_animation_looped", "Particle Animation Looped", "Should particle texture atlas animation loop");
        public static final TagID PARTICLE_ANIMATION_SPEED = new TagID("particle_animation_speed", "Particle Animation Speed", "Particle texture atlas animation playback speed multiplier, if looped");

        public static final TagID DECAL_LAYER_ID = new TagID("decal_layer", "Layer ID", "Decal will be rendered, if object's decal layer ID matches decal's layer. If value == 0, renders everywhere");
        public static final TagID EMISSIVE_FACTOR = new TagID("emissive_factor_strength", "Emissive Strength", "Emissive intensity strength");
        public static final TagID ALPHA_DISCARD = new TagID("alpha_discard", "Alpha Discard", "Alpha discard threshold value");

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
        public static final TagID OBJECT_LIST_ATTACHED = new TagID("obj_list_selected_att", "Attach To");

        public static final TagID CUT_OFF = new TagID("cut_off", "Light's cone cut off");
        public static final TagID ATTENUATION_FACTOR = new TagID("att_factor", "Light's attenuation factor");

        public static final TagID FLOAT2 = new TagID("float2", "2 Values");
        public static final TagID FLOAT3 = new TagID("float3", "3 Values");
        public static final TagID FLOAT4 = new TagID("float4", "4 Values");
    }
}
