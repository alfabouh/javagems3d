in vec3 modelview_vertex_pos;
in vec3 normal;

layout (location = 0) out vec4 accumulated;
layout (location = 1) out float reveal;
layout (location = 2) out vec4 bright_color;

uniform vec4 color;

#include "/assets/shaders/libs/oit"

void main()
{
    vec3 gPosition = modelview_vertex_pos;

    vec4 frag_color = color;
    float dotFloat = max(dot(vec3(0.75, 1.0, 0.75), normal), 0.5);

    accumulated = calc_accumulated(frag_color);
    reveal = calc_alpha(frag_color);
    bright_color = vec4(0., 0., 0., 1.);
}