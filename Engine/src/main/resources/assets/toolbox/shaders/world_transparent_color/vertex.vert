layout (location = 0) in vec3 position;

uniform mat4 view_matrix;
uniform mat4 model_matrix;
uniform mat4 projection_matrix;
out vec3 mv_out_pos;

void main()
{
    vec4 mv_pos = view_matrix * model_matrix * vec4(position, 1.0f);
    gl_Position = projection_matrix * mv_pos;

    mv_out_pos = gl_Position.xyz;
}
