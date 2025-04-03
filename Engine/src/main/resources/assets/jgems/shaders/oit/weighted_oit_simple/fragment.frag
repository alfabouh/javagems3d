in vec3 modelview_vertex_pos;
in vec3 normal;

layout (location = 0) out vec4 accumulated;
layout (location = 1) out float reveal;
layout (location = 2) out vec4 bright_color;

uniform vec4 color;

void main()
{
    vec3 gPosition = modelview_vertex_pos;

    vec4 frag_color = color;
    float dotFloat = max(dot(vec3(0.75, 1.0, 0.75), normal), 0.5);

    float weight = max(min(1.0, max(max(frag_color.r, frag_color.g), frag_color.b) * frag_color.a), frag_color.a) * clamp(0.03 / (1.0e-5f + pow(gl_FragCoord.z / 200.0, 4.0)), 1.0e-2f, 3.0e+3f);
    accumulated = vec4((frag_color.rgb * dotFloat) * frag_color.a, frag_color.a) * weight;

    reveal = frag_color.a;
    bright_color = vec4(0., 0., 0., 1.);
}