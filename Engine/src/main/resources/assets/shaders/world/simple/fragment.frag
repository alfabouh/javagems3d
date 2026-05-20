layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 frag_color2;

uniform vec3 color;
uniform bool light;
in vec3 normal;

void main()
{
    vec4 color = vec4(color, 1.);
    frag_color = light ? color : mix(color, vec4(vec3(dot(vec3(0.25, 0.75, 0.5), normal)), 1.), 0.5);
    frag_color2 = frag_color  * float(light);
}