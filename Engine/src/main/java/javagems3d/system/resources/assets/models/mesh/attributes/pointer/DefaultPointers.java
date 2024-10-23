package javagems3d.system.resources.assets.models.mesh.attributes.pointer;

public abstract class DefaultPointers {
    public static final AttributePointer POSITIONS = new AttributePointer(0, 3);
    public static final AttributePointer TEXTURE_COORDINATES = new AttributePointer(1, 2);
    public static final AttributePointer NORMALS = new AttributePointer(2, 3);
    public static final AttributePointer TANGENTS = new AttributePointer(3, 2);
    public static final AttributePointer BI_TANGENTS = new AttributePointer(4, 3);
}
