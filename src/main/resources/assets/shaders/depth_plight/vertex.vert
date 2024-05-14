layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 texture;

uniform mat4 model_matrix;

void main()
{
    gl_Position = model_matrix * vec4(aPosition, 1.0f);
}
