layout (location=0) in vec3 position;
layout (location=1) in vec2 texture;

out vec2 out_texture;
uniform mat4 projection_model_matrix;
uniform mat4 view_matrix;

void main()
{
    gl_Position = projection_model_matrix * vec4(position, 1.0f);
    out_texture = texture;
}