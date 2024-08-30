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

package javagems3d.toolbox.map_sys.save.objects.object_attributes;

public final class AttributeID {
    public static final AttributeID IS_PROP = new AttributeID("is_prop", "Is Prop");
    public static final AttributeID IS_STATIC = new AttributeID("is_static", "Is Static");
    public static final AttributeID BRIGHTNESS = new AttributeID("brightness", "Brightness");
    public static final AttributeID COLOR = new AttributeID("color", "Color");
    public static final AttributeID POSITION_XYZ = new AttributeID("position_xyz", "Position");
    public static final AttributeID ROTATION_XYZ = new AttributeID("rotation_xyz", "Rotation");
    public static final AttributeID SCALING_XYZ = new AttributeID("scaling_xyz", "Scaling");
    public static final AttributeID SOUND = new AttributeID("sound", "Sound Name");
    public static final AttributeID NAME = new AttributeID("name", "Name");
    public static final AttributeID SOUND_VOL = new AttributeID("sound_vol", "Sound Volume");
    public static final AttributeID SOUND_PITCH = new AttributeID("sound_pitch", "Sound Pitch");
    public static final AttributeID SOUND_ROLL_OFF = new AttributeID("sound_roll_off", "Sound Roll Off");

    private final String id;
    private final String description;

    public AttributeID(String id, String description) {
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
