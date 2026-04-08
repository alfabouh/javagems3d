layout (location=0) in vec3 aPosition;
layout (location=2) in vec3 aNormal;

out vec3 modelview_vertex_pos;
out vec3 normal;

uniform mat4 view_matrix;
uniform mat4 model_matrix;
uniform mat4 projection_matrix;

void main()
{
    mat4 model_view_matrix = view_matrix * model_matrix;
    vec4 mv_pos = model_view_matrix * vec4(aPosition, 1.0f);
    gl_Position = projection_matrix * mv_pos;

    normal = normalize(model_matrix * vec4(aNormal, 0.0f)).xyz;
    modelview_vertex_pos = mv_pos.xyz;
}