layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 aTexture;

out vec2 uv_coordinates;

out vec3 modelview_vertex_pos;
out vec4 model_vertex_pos;
out mat4 out_view_matrix;

uniform mat4 view_matrix;
uniform mat4 model_matrix;
uniform mat4 projection_matrix;

void main()
{
    mat4 model_view_matrix = view_matrix * model_matrix;
    vec4 mv_pos = model_view_matrix * vec4(aPosition, 1.0f);
    gl_Position = projection_matrix * mv_pos;

    uv_coordinates = aTexture;
    modelview_vertex_pos = mv_pos.xyz;
    model_vertex_pos = model_matrix * vec4(aPosition, 1.0f);

    out_view_matrix = view_matrix;
}