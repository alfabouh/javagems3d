layout (location=0) in vec3 position;
layout (location=1) in vec2 texture;

uniform mat4 projection_model_matrix;
out vec2 texture_coordinates;

void main()
{
    gl_Position = projection_model_matrix * vec4(position, 1.0f);
    texture_coordinates = texture;
}
