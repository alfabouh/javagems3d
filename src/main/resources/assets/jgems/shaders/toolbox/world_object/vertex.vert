layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec2 aTexture;

uniform mat4 model_view_matrix;
uniform mat4 projection_matrix;
out vec3 mv_out_pos;

out vec2 out_texture;

void main()
{
    vec4 mv_pos = model_view_matrix * vec4(aPosition, 1.0f);
    gl_Position = projection_matrix * mv_pos;

    mv_out_pos = gl_Position.xyz;
    out_texture = aTexture;
}
