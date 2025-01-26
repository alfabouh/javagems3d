layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 aTexture;
layout (location=2) in vec3 aNormal;
layout (location=3) in vec3 aTangent;
layout (location=4) in vec3 aBitangent;

layout (std140, binding = 0) uniform SunLight {
    vec4 sunPos;
    vec4 sunColor;
    vec2 sunMeta;
};

layout (std140, binding = 3) uniform Fog {
    vec4 fogColor;
    float fogDensity;
};

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

layout(std430, binding = 1) buffer IndirectBufferData {
    mat4 modelMatrices[2048];
    int entityIds[2048];
    int materialIds[2048];
};

void main()
{
    uint idx = gl_BaseInstance + gl_InstanceID;
    matertial_id = materialIds[idx];
    ent_id = entityIds[idx];
    mat4 model = modelMatrices[ent_id];

    mat4 model_view_matrix = view_matrix * model;
    vec4 mv_pos = model_view_matrix * vec4(aPosition, 1.0f);
    gl_Position = (projection_matrix * mv_pos);

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