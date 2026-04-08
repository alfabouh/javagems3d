#extension GL_ARB_bindless_texture : require
precision highp float;
precision highp int;

layout (early_fragment_tests) in;

in flat uint ent_id;

layout(std430, binding = 5) buffer Visibility {
    restrict int visibility[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
};

void main()
{
    visibility[ent_id] = 1;
}