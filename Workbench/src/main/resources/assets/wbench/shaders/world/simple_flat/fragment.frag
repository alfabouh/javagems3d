layout (location = 0) out vec4 frag_color;
uniform vec4 color;

in vec3 vertex_position;

void main()
{
    vec2 pos = vertex_position.xz;

    int scaling = 16;
    int x = int(floor(pos.x / scaling));
    int y = int(floor(pos.y / scaling));

    frag_color = (x + y) % 2 == 0 ? color : (color + vec4(0.15, 0.15, 0.25, 0.0));
}