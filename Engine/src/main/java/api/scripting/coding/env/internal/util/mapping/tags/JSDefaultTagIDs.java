package api.scripting.coding.env.internal.util.mapping.tags;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import javagems3d.system.external.mapping.tags.TagID;

@JSCodingClass(binding = "JSDefaultTagIDs", description = "Default predefined TagIDs.")
public class JSDefaultTagIDs {

    @JSCodingField(description = "Physics state tag")
    public static final JSTagID PHYSICS_STATE = new JSTagID(TagID.DEFAULT.PHYSICS_STATE);

    @JSCodingField(description = "String ID")
    public static final JSTagID MARKER_STRING_ID = new JSTagID(TagID.DEFAULT.MARKER_STRING_ID);

    @JSCodingField(description = "Position X")
    public static final JSTagID POSITION_X = new JSTagID(TagID.DEFAULT.POSITION_X);

    @JSCodingField(description = "Position Y")
    public static final JSTagID POSITION_Y = new JSTagID(TagID.DEFAULT.POSITION_Y);

    @JSCodingField(description = "Position Z")
    public static final JSTagID POSITION_Z = new JSTagID(TagID.DEFAULT.POSITION_Z);

    @JSCodingField(description = "Position XY")
    public static final JSTagID POSITION_XY = new JSTagID(TagID.DEFAULT.POSITION_XY);

    @JSCodingField(description = "Position XZ")
    public static final JSTagID POSITION_XZ = new JSTagID(TagID.DEFAULT.POSITION_XZ);

    @JSCodingField(description = "Position YZ")
    public static final JSTagID POSITION_YZ = new JSTagID(TagID.DEFAULT.POSITION_YZ);

    @JSCodingField(description = "Position XYZ")
    public static final JSTagID POSITION_XYZ = new JSTagID(TagID.DEFAULT.POSITION_XYZ);

    @JSCodingField(description = "Rotation X")
    public static final JSTagID ROTATION_X = new JSTagID(TagID.DEFAULT.ROTATION_X);

    @JSCodingField(description = "Rotation Y")
    public static final JSTagID ROTATION_Y = new JSTagID(TagID.DEFAULT.ROTATION_Y);

    @JSCodingField(description = "Rotation Z")
    public static final JSTagID ROTATION_Z = new JSTagID(TagID.DEFAULT.ROTATION_Z);

    @JSCodingField(description = "Rotation XY")
    public static final JSTagID ROTATION_XY = new JSTagID(TagID.DEFAULT.ROTATION_XY);

    @JSCodingField(description = "Rotation XZ")
    public static final JSTagID ROTATION_XZ = new JSTagID(TagID.DEFAULT.ROTATION_XZ);

    @JSCodingField(description = "Rotation YZ")
    public static final JSTagID ROTATION_YZ = new JSTagID(TagID.DEFAULT.ROTATION_YZ);

    @JSCodingField(description = "Rotation XYZ")
    public static final JSTagID ROTATION_XYZ = new JSTagID(TagID.DEFAULT.ROTATION_XYZ);

    @JSCodingField(description = "Scaling X")
    public static final JSTagID SCALING_X = new JSTagID(TagID.DEFAULT.SCALING_X);

    @JSCodingField(description = "Scaling Y")
    public static final JSTagID SCALING_Y = new JSTagID(TagID.DEFAULT.SCALING_Y);

    @JSCodingField(description = "Scaling Z")
    public static final JSTagID SCALING_Z = new JSTagID(TagID.DEFAULT.SCALING_Z);

    @JSCodingField(description = "Scaling XY")
    public static final JSTagID SCALING_XY = new JSTagID(TagID.DEFAULT.SCALING_XY);

    @JSCodingField(description = "Scaling XZ")
    public static final JSTagID SCALING_XZ = new JSTagID(TagID.DEFAULT.SCALING_XZ);

    @JSCodingField(description = "Scaling YZ")
    public static final JSTagID SCALING_YZ = new JSTagID(TagID.DEFAULT.SCALING_YZ);

    @JSCodingField(description = "Scaling XYZ")
    public static final JSTagID SCALING_XYZ = new JSTagID(TagID.DEFAULT.SCALING_XYZ);

    @JSCodingField(description = "RGB color")
    public static final JSTagID COLOR3 = new JSTagID(TagID.DEFAULT.COLOR3);

    @JSCodingField(description = "RGBA color")
    public static final JSTagID COLOR4 = new JSTagID(TagID.DEFAULT.COLOR4);

    @JSCodingField(description = "Brightness")
    public static final JSTagID BRIGHTNESS = new JSTagID(TagID.DEFAULT.BRIGHTNESS);

    @JSCodingField(description = "Object list")
    public static final JSTagID OBJECT_LIST = new JSTagID(TagID.DEFAULT.OBJECT_LIST);

    @JSCodingField(description = "Float2")
    public static final JSTagID FLOAT2 = new JSTagID(TagID.DEFAULT.FLOAT2);

    @JSCodingField(description = "Float3")
    public static final JSTagID FLOAT3 = new JSTagID(TagID.DEFAULT.FLOAT3);

    @JSCodingField(description = "Float4")
    public static final JSTagID FLOAT4 = new JSTagID(TagID.DEFAULT.FLOAT4);
}