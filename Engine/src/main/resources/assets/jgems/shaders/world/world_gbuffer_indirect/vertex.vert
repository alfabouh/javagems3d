layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 aTexture;
layout (location=2) in vec3 aNormal;
layout (location=3) in vec3 aTangent;
layout (location=4) in vec3 aBitangent;

out vec2 texture_coordinates;
out vec3 m_vertex_normal;
out vec3 mv_vertex_normal;
out vec3 mv_vertex_pos;
out vec4 out_model_position;
out mat3 TBN;

out mat4 view;
out mat4 model;

out flat uint matertial_id;

uniform mat4 view_matrix;
uniform mat4 projection_matrix;

layout(std430, binding = 1) buffer IndirectBufferData {
    mat4 modelMatrices[1024];
    int entityIds[1024];
    int materialIds[1024];
};

void main()
{
    vec4 position = vec4(aPosition, 1.0);
    vec4 normal = vec4(aNormal, 0.0);
    vec4 tangent = vec4(aTangent, 0.0);
    vec4 bitanget = vec4(aBitangent, 0.0);

    uint idx = gl_BaseInstance + gl_InstanceID;
    matertial_id = materialIds[idx];
    int ent_id = entityIds[idx];
    model = modelMatrices[ent_id];

    view = view_matrix;
    mat4 model_view_matrix = view_matrix * model;
    vec4 mv_pos = model_view_matrix * position;
    gl_Position = projection_matrix * mv_pos;

    vec3 T = normalize(vec3(model_view_matrix * tangent));
    vec3 B = normalize(vec3(model_view_matrix * bitanget));
    vec3 N = normalize(vec3(model_view_matrix * normal));
    TBN = mat3(T, B, N);

    texture_coordinates = aTexture;
    mv_vertex_normal = normalize(model_view_matrix * normal).xyz;
    m_vertex_normal = normalize(model * normal).xyz;
    mv_vertex_pos = mv_pos.xyz;

    out_model_position = model * position;
}