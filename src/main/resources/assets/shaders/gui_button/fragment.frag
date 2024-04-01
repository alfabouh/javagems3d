layout (location = 0) out vec4 frag_color;
uniform vec4 background_color;
uniform int selected;

void main()
{
    frag_color = selected == 1 ? (background_color * background_color) : background_color;
}
