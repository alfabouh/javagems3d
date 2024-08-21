layout (location = 0) in vec3 position;

uniform mat4 view_matrix;
uniform mat4 model_matrix;
uniform mat4 projection_matrix;

void main()
{
    vec4 mv_pos = view_matrix * model_matrix * vec4(position, 1.0f);
    gl_Position = projection_matrix * mv_pos;
}
