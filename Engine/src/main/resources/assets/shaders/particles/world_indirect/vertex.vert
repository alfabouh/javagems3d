#extension GL_ARB_bindless_texture : require


layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 aTexture;

out vec2 uv_coordinates;
out flat uint ent_id;
out vec3 lightFactor;
out vec4 frag_pos;

uniform mat4 view_matrix;
uniform mat4 projection_matrix;

layout(std430, binding = 14) readonly restrict buffer IndirectBufferData {
    mat4 modelMatrix[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
};

#include "/assets/shaders/libs/shadows_simple"
#include "/assets/shaders/libs/lighting_simple"
#include "/assets/shaders/libs/light_fragment_calc_simple"

void main()
{
    uint idx = gl_BaseInstance + gl_InstanceID;
    ent_id = idx;
    mat4 model = modelMatrix[ent_id];

    vec4 world_pos = model * vec4(aPosition, 1.0);
    vec4 view_pos = view_matrix * world_pos;

    frag_pos = view_pos;

    gl_Position = projection_matrix * view_pos;
    lightFactor = calc_light(view_matrix, world_pos, view_pos.z);
    uv_coordinates = aTexture;
}