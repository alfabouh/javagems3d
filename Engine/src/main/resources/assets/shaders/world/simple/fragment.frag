layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 frag_color2;

uniform vec3 color;
uniform bool light;
in vec3 normal;

void main()
{
    float dotFloat = !light ? max(dot(vec3(0.75, 1.0, 0.75), normal), 0.5) : 1.;
    frag_color = ((vec4(color, 1.)) * dotFloat) + (vec4(normal * vec3(0.5), 0.0)) * (1. - float(light));
    frag_color2 = frag_color  * float(light);
}