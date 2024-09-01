layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 aTexture;

layout (std140, binding = 3) uniform Fog {
    vec4 fogColor;
    float fogDensity;
};

layout (std140, binding = 2) uniform Misc {
    float w_tick;
};

out vec3 mv_vertex_pos;
out vec2 texture_coordinates;
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
}
