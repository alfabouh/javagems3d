layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 texture;

uniform mat4 projection_view_matrix;
uniform mat4 model_matrix;

out vec2 out_texture;

void main()
{
    gl_Position = projection_view_matrix * model_matrix * vec4(aPosition, 1.0f);
    out_texture = texture;
}
