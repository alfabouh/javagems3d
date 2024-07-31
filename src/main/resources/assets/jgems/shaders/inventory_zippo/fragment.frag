layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 frag_color2;
in vec2 texture_coordinates;

uniform bool use_emission;
uniform sampler2D diffuse_map;
uniform sampler2D emissive_map;

void main()
{
    frag_color = texture(diffuse_map, texture_coordinates);
    vec4 evec4 = texture(emissive_map, texture_coordinates);
    vec4 cvec = vec4(frag_color);
    cvec *= vec4(0.0, 0.0, 0.0, 1.0);
    cvec += use_emission && evec4.a > 0 ? evec4 : vec4(0.0);
    frag_color2 = (cvec);
}