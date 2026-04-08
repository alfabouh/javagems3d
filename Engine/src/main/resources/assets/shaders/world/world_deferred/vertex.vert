layout (location=0) in vec3 position;
layout (location=1) in vec2 texture;

out vec2 uv_coordinates;
out mat4 out_view_matrix;
out mat4 out_inversed_view_matrix;

uniform mat4 projection_model_matrix;
uniform mat4 view_matrix;

void main()
{
    gl_Position = projection_model_matrix * vec4(position, 1.0f);
    uv_coordinates = texture;

    out_inversed_view_matrix = inverse(view_matrix);
    out_view_matrix = view_matrix;
}