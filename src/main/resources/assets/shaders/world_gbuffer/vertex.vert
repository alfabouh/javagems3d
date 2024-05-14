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

uniform mat4 view_matrix;
uniform mat4 model_matrix;
uniform mat4 projection_matrix;

void main()
{
    mat4 model_view_matrix = view_matrix * model_matrix;
    vec4 mv_pos = model_view_matrix * vec4(aPosition, 1.0f);
    gl_Position = projection_matrix * mv_pos;

    vec3 T = normalize(vec3(model_view_matrix * (vec4(aTangent, 0.0))));
    vec3 B = normalize(vec3(model_view_matrix * (vec4(aBitangent, 0.0))));
    vec3 N = normalize(vec3(model_view_matrix * (vec4(aNormal, 0.0))));
    TBN = mat3(T, B, N);

    texture_coordinates = aTexture;
    mv_vertex_normal = normalize(model_view_matrix * vec4(aNormal, 0.0f)).xyz;
    m_vertex_normal = normalize(model_matrix * vec4(aNormal, 0.0f)).xyz;
    mv_vertex_pos = mv_pos.xyz;

    out_model_position = model_matrix * vec4(aPosition, 1.0f);
}
