layout (location = 0) out vec4 frag_color;

uniform vec3 diffuse_color;
uniform bool selected;

void main()
{
    frag_color = vec4(diffuse_color, 1.0);
    if (selected) {
        frag_color = vec4(1.0, 0.0, 0.0, 1.0);
    }
}