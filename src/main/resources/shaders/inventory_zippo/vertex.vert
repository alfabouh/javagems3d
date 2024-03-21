layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 aTexture;
layout (location=2) in vec3 aNormal;
layout (location=3) in vec3 aTangent;
layout (location=4) in vec3 aBitangent;

layout (std140, binding = 2) uniform Misc {
    float w_tick;
};

out vec2 texture_coordinates;
uniform mat4 model_matrix;
uniform mat4 projection_matrix;
out vec3 mv_vertex_normal;
out vec3 mv_vertex_pos;

void main()
{
    vec4 m_pos = model_matrix * vec4(aPosition, 1.0f);
    gl_Position = projection_matrix * m_pos;
    texture_coordinates = aTexture;

    mv_vertex_normal = normalize(model_matrix * vec4(aNormal, 0.0f)).xyz;
    mv_vertex_pos = m_pos.xyz;
}
