layout (location=0) in vec3 position;

out vec3 uv_coordinates_cube;

uniform mat4 projection_matrix;
uniform mat4 model_view_matrix;

void main()
{
    vec4 pos = projection_matrix * model_view_matrix * vec4(position, 1.0f);
    gl_Position = pos.xyww;
    uv_coordinates_cube = position;
}
