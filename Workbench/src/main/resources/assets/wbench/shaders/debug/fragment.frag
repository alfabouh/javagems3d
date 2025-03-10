layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 frag_color2;

uniform vec4 color;

void main()
{
    frag_color = color;
    frag_color2 = vec4(0.0);
}