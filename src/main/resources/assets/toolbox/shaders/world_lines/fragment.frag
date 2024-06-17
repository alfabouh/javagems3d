layout (location = 0) out vec4 frag_color;

uniform vec4 colour;

void main()
{
    frag_color = colour;
}