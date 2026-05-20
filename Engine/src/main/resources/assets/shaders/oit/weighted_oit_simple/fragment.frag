in vec3 modelview_vertex_pos;
in vec3 normal;

layout (location = 0) out vec4 accumulated;
layout (location = 1) out float reveal;
layout (location = 2) out vec4 bright_color;

uniform vec4 color;

#include "/assets/shaders/libs/oit"

void main()
{
    vec4 frag_color = mix(color, vec4(vec3(dot(vec3(0.25, 0.75, 0.5), normal)), color.a), 0.5);
    accumulated = calc_accumulated(frag_color);
    reveal = calc_alpha(frag_color);
    bright_color = vec4(0., 0., 0., 1.);
}