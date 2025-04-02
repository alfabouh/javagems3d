layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 frag_color2;

uniform vec3 color;
in vec3 normal;

void main()
{
    float dotFloat = max(dot(vec3(0.75, 1.0, 0.75), normal), 0.5);
    frag_color = vec4(color, 1.) * dotFloat;
    frag_color2 = vec4(0.0);
}