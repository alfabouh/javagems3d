layout (location = 0) in vec3 position;

uniform mat4 projection_matrix;
uniform mat4 model_matrix;
uniform mat4 view_inversed;

void main()
{
    gl_Position = projection_matrix * (model_matrix * view_inversed * vec4(position, 1.0f));
}
