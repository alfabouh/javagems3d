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

uniform mat4 view_matrix;
uniform mat4 projection_matrix;

layout(std430, binding = 1) buffer IndirectBufferData {
    mat4 modelMatrices[1024];
    int entityId[1024];
};

flat out uint outMaterialIdx;

void main()
{
    vec4 startPos = vec4(aPosition, 1.0);
    vec4 startNormal = vec4(aNormal, 0.0);
    vec4 startTangent = vec4(aTangent, 0.0);
    vec4 startBiTangent = vec4(aBitangent, 0.0);

    uint idx = gl_BaseInstance + gl_InstanceID;

    outMaterialIdx = 0;

    view = view_matrix;
    model = modelMatrices[entityId[idx]];

    mat4 model_view_matrix = view_matrix * model;
    vec4 mv_pos = model_view_matrix * startPos;
    gl_Position = projection_matrix * mv_pos;

    vec3 T = normalize(vec3(model_view_matrix * startTangent));
    vec3 B = normalize(vec3(model_view_matrix * startBiTangent));
    vec3 N = normalize(vec3(model_view_matrix * startNormal));
    TBN = mat3(T, B, N);

    texture_coordinates = aTexture;
    mv_vertex_normal = normalize(model_view_matrix * startNormal).xyz;
    m_vertex_normal = normalize(model * startNormal).xyz;
    mv_vertex_pos = mv_pos.xyz;

    out_model_position = model * startPos;
}