#extension GL_ARB_bindless_texture : require
precision highp float;
precision highp int;

layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 texture;
layout (location=5) in ivec4 aBoneIndexes;
layout (location=6) in vec4 aBoneWeights;

out vec4 frag_pos;
out vec2 uv_coordinates;
out flat uint matertial_id;
out flat uint ent_id;

uniform mat4 projection_view_matrix;

layout(std430, binding = 1) readonly restrict buffer IndirectBufferData {
    int entityId[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
    int materialId[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
    mat4 modelMatrix[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
    int animationOffset[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
    int animationOffsetPrev[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
    float animationFrameDelta[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
};

#include "/assets/shaders/libs/animations"

void main()
{
    uint idx = gl_BaseInstance + gl_InstanceID;
    ent_id = entityId[idx];
    matertial_id = materialId[idx];
    mat4 model = modelMatrix[ent_id];

    int currAnimationOffset = animationOffset[ent_id];
    int currAnimationOffsetPrev = animationOffsetPrev[ent_id];
    float deltaFrame = animationFrameDelta[ent_id];

    vec4 position = vec4(aPosition, 1.0);
    vec4 tempNormal = vec4(0.0);
    vec4 tempTangent = vec4(0.0);
    vec4 tempBiTangent = vec4(0.0);
    perform_animation(position, tempNormal, tempTangent, tempBiTangent, aBoneIndexes, aBoneWeights, currAnimationOffset, currAnimationOffsetPrev, deltaFrame);

    frag_pos = model * position;
    gl_Position = projection_view_matrix * frag_pos;
    uv_coordinates = texture;
}