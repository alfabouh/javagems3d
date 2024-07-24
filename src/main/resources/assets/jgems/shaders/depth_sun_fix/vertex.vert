layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 texture;

uniform mat4 projection_model_matrix;

out vec2 out_texture;

void main()
{
    gl_Position = projection_model_matrix * vec4(aPosition, 1.0f);
    out_texture = texture;
}
