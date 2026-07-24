layout (location=0) in vec3 position;

uniform mat4 projection_matrix;
uniform mat4 model_view_matrix;
out vec2 uv_coordinates;

out vec4 box_model_frag_pos;

void main()
{
    gl_Position = projection_matrix * model_view_matrix * vec4(position, 1.0f);
    vec3 ndc = gl_Position.xyz / gl_Position.w;
    box_model_frag_pos = gl_Position;
}