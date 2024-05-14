layout (location = 0) in vec3 aPosition;
layout (location = 5) in vec3 aAvarageNormal;

uniform mat4 model_view_matrix;
uniform mat4 projection_matrix;

out vec4 out_mv_position;

void main()
{
    vec4 mv_pos = model_view_matrix * vec4(aPosition + aAvarageNormal * 0.025, 1.0f);
    gl_Position = projection_matrix * mv_pos;

    out_mv_position = mv_pos;
}
