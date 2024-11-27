package javagems3d.system.resources.assets.models.mesh.vertex.pointers;

@SuppressWarnings("all")
public abstract class DefaultAttributePointers {
    public static final RenderAttributePointer ATTR_POSITIONS = new RenderAttributePointer(0, 3, Float.BYTES);
    public static final RenderAttributePointer ATTR_TEXTURE_COORDINATES = new RenderAttributePointer(1, 2, Float.BYTES);
    public static final RenderAttributePointer ATTR_NORMALS = new RenderAttributePointer(2, 3, Float.BYTES);
    public static final RenderAttributePointer ATTR_TANGENTS = new RenderAttributePointer(3, 2, Float.BYTES);
    public static final RenderAttributePointer ATTR_BI_TANGENTS = new RenderAttributePointer(4, 3, Float.BYTES);
    public static final RenderAttributePointer ATTR_BONE_INDEXES = new RenderAttributePointer(5, 4, Float.BYTES);
    public static final RenderAttributePointer ATTR_BONE_WEIGHTS = new RenderAttributePointer(6, 4, Float.BYTES);
}