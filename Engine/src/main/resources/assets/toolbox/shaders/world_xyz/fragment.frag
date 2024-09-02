layout (location = 0) out vec4 frag_color;
uniform vec4 diffuse_color;

void main()
{
    frag_color = diffuse_color;
}