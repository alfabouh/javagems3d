layout (location=0) in vec3 position;
layout (location=1) in vec2 texture;

out vec2 uv_coordinates;
uniform mat4 projection_matrix;
uniform mat4 view;

void main()
{
    vec4 pos = view * vec4(position, 1.0f);
    gl_Position = projection_matrix * pos.xyzw;
    uv_coordinates = texture;
}
