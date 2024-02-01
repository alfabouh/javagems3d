layout (location=0) in vec3 aPosition;

uniform mat4 model_matrix;

void main()
{
    gl_Position = model_matrix * vec4(aPosition, 1.0f);
}
