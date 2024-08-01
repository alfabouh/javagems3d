layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 aTexture;

out vec2 texture_coordinates;

uniform mat4 model_view_matrix;
uniform mat4 projection_matrix;

void main()
{
    vec4 mv_pos = model_view_matrix * vec4(aPosition, 1.0f);
    gl_Position = projection_matrix * mv_pos;
    texture_coordinates = aTexture;
}