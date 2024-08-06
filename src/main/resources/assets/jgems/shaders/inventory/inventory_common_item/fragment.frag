layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 frag_color2;
in vec2 texture_coordinates;
uniform sampler2D diffuse_map;

void main()
{
    frag_color = texture(diffuse_map, vec2(texture_coordinates.x, texture_coordinates.y));
    vec4 cvec = vec4(frag_color);
    cvec *= vec4(0.0, 0.0, 0.0, 1.0);
    frag_color2 = cvec;
}