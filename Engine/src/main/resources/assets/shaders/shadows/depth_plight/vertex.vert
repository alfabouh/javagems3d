layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 texture;

out vec4 frag_pos;
out vec2 uv_coordinates;

uniform mat4 model_matrix;
uniform mat4 projection_view_matrix;

void main()
{
    gl_Position = projection_view_matrix * model_matrix * vec4(aPosition, 1.0f);
    frag_pos = (model_matrix * vec4(aPosition, 1.0f));

    uv_coordinates = texture;
}