#extension GL_ARB_bindless_texture : require

layout (early_fragment_tests) in;

in flat uint ent_id;

layout(std430, binding = 5) buffer Visibility {
    writeonly int visibility[2048];
};

void main()
{
    visibility[ent_id] = 1;
}