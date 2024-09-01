layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 aTexture;

struct PointLight
{
    vec4 plPos;
    vec4 plViewPos;
    vec4 plColor;
    vec2 plMeta;
};

layout (std140, binding = 0) uniform SunLight {
    vec4 sunPos;
    vec4 sunColor;
    vec2 sunMeta;
};

layout (std140, binding = 1) uniform PointLights {
    PointLight p_l[128];
    int total_plights;
};

layout (std140, binding = 2) uniform Misc {
    float w_tick;
};

layout (std140, binding = 3) uniform Fog {
    vec4 fogColor;
    float fogDensity;
};

out vec2 texture_coordinates;

out vec3 mv_vertex_pos;
out vec4 m_vertex_pos;
out mat4 out_view_matrix;

uniform mat4 view_matrix;
uniform mat4 model_matrix;
uniform mat4 projection_matrix;

void main()
{
    mat4 model_view_matrix = view_matrix * model_matrix;
    vec4 mv_pos = model_view_matrix * vec4(aPosition, 1.0f);
    gl_Position = projection_matrix * mv_pos;

    texture_coordinates = aTexture;
    mv_vertex_pos = mv_pos.xyz;
    m_vertex_pos = model_matrix * vec4(aPosition, 1.0f);

    out_view_matrix = view_matrix;
}