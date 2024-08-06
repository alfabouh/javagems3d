layout (location = 0) out vec4 frag_color;
uniform vec4 background_color;
uniform bool selected;

void main()
{
    frag_color = selected ? (background_color * background_color) : background_color;
}
