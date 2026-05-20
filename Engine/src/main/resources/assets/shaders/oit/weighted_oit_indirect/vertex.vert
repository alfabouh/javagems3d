layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 aTexture;
layout (location=2) in vec3 aNormal;
layout (location=3) in vec3 aTangent;
layout (location=4) in vec3 aBitangent;

out vec2 uv_coordinates;

out vec3 modelview_vertex_normal;
out vec3 model_vertex_normal;
out vec3 modelview_vertex_pos;
out vec4 model_vertex_pos;

out mat3 TBN;
out mat4 out_view_matrix;

out flat uint matertial_id;
out flat uint ent_id;

uniform mat4 view_matrix;
uniform mat4 projection_matrix;


layout(std430, binding = 1) readonly restrict buffer IndirectBufferData {
    int entityId[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
    int materialId[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
    mat4 modelMatrix[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
    int animationOffset[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
    int animationOffsetPrev[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
    float animationFrameDelta[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
};

void main()
{
    uint idx = gl_BaseInstance + gl_InstanceID;
    ent_id = entityId[idx];
    matertial_id = materialId[idx];
    mat4 model = modelMatrix[ent_id];
    int currAnimationOffset = animationOffset[ent_id];
    int currAnimationOffsetPrev = animationOffsetPrev[ent_id];

    mat4 model_view_matrix = view_matrix * model;
    vec4 mv_pos = model_view_matrix * vec4(aPosition, 1.0f);
    gl_Position = projection_matrix * mv_pos;

    uv_coordinates = aTexture;

    modelview_vertex_normal = normalize(model_view_matrix * vec4(aNormal, 0.0f)).xyz;
    model_vertex_normal = normalize(model * vec4(aNormal, 0.0f)).xyz;
    modelview_vertex_pos = mv_pos.xyz;
    model_vertex_pos = model * vec4(aPosition, 1.0f);

    vec3 T = normalize(vec3(model_view_matrix * (vec4(aTangent, 0.0))));
    vec3 B = normalize(vec3(model_view_matrix * (vec4(aBitangent, 0.0))));
    vec3 N = normalize(vec3(model_view_matrix * (vec4(aNormal, 0.0))));
    TBN = mat3(T, B, N);

    out_view_matrix = view_matrix;
}