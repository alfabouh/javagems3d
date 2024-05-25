layout (location=0) in vec3 aPosition;

uniform mat4 model_matrix;
uniform mat4 view_matrix;
out vec4 frag_pos;

void main()
{
    gl_Position = view_matrix * model_matrix * vec4(aPosition, 1.0f);
    frag_pos = (model_matrix * vec4(aPosition, 1.0f));
}
