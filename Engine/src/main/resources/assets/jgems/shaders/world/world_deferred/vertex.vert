layout (location=0) in vec3 position;
layout (location=1) in vec2 texture;

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

layout (std140, binding = 3) uniform Fog {
    vec4 fogColor;
    float fogDensity;
};

out vec2 out_texture;
out mat4 out_view_matrix;
out mat4 out_inversed_view_matrix;
uniform mat4 projection_model_matrix;
uniform mat4 view_matrix;

void main()
{
    gl_Position = projection_model_matrix * vec4(position, 1.0f);
    out_texture = texture;

    out_inversed_view_matrix = inverse(view_matrix);
    out_view_matrix = view_matrix;
}