layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec2 aTexture;
layout (location = 2) in vec3 aNormal;

uniform mat4 view_matrix;
uniform mat4 model_matrix;
uniform mat4 projection_matrix;
uniform vec3 sunPos;
out vec3 mv_out_pos;

out vec3 outSunPos;
out vec2 out_texture;
out vec3 mv_vertex_normal;
out vec3 mv_vertex_pos;

void main()
{
    mat4 model_view_matrix = view_matrix * model_matrix;
    vec4 mv_pos = model_view_matrix * vec4(aPosition, 1.0f);
    gl_Position = projection_matrix * mv_pos;

    mv_vertex_normal = normalize(model_view_matrix * vec4(aNormal, 0.0f)).xyz;
    mv_vertex_pos = mv_pos.xyz;

    mv_out_pos = gl_Position.xyz;
    out_texture = aTexture;

    outSunPos = normalize(view_matrix * vec4(sunPos, 0.0)).xyz;
}
