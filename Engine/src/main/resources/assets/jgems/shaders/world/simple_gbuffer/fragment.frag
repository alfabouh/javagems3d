layout (early_fragment_tests) in;
layout (location = 0) out vec3 gPosition;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gColor;
layout (location = 3) out vec3 gEmission;
layout (location = 4) out vec2 gMetallicRoughness;

in vec3 modelview_vertex_pos;
uniform vec3 color;

void main()
{
    gNormal = vec3(0.);
    gPosition = modelview_vertex_pos;
    gColor = vec4(color, 1.);
    gEmission = vec3(0.);
    gMetallicRoughness = vec2(0.);
}