layout (location=0) in vec3 position;

layout (std140, binding = 0) uniform SunLight {
    vec4 sunPos;
    vec4 sunColor;
    vec2 sunMeta;
};

layout (std140, binding = 3) uniform Fog {
    vec4 fogColor;
    float fogDensity;
};

out vec3 out_texture_cube;
uniform mat4 projection_matrix;
uniform mat4 model_view_matrix;

void main()
{
    vec4 pos = projection_matrix * model_view_matrix * vec4(position, 1.0f);
    gl_Position = pos.xyww;
    out_texture_cube = position;
}
