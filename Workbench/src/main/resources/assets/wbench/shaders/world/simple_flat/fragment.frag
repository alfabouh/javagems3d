layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 bright_color;

uniform vec4 color;
uniform float drawCenterRect;

in vec3 vertex_position;

void main()
{
    vec2 pos = vertex_position.xz;

    int scaling = 4;
    int x = int(floor(pos.x / scaling));
    int y = int(floor(pos.y / scaling));

    frag_color = (x + y) % 2 == 0 ? color : (color + vec4(0.15, 0.15, 0.25, 0.0));

    float inRect = step(abs(pos.x), drawCenterRect) * step(abs(pos.y), drawCenterRect);
    frag_color *= mix(vec4(1.), vec4(3.0, 0.0, 0.0, 1.), inRect);

    bright_color = vec4(0.);
}