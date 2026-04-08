layout (location=0) in vec3 position;
uniform mat4 projection_model_matrix;

void main()
{
    gl_Position = projection_model_matrix * vec4(position, 1.0f);
}
