layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 frag_color2;

in vec2 texture_coordinates;
uniform sampler2D diffuse_map;
uniform float alpha_discard;

void main()
{
    frag_color = texture(diffuse_map, vec2(texture_coordinates.x, texture_coordinates.y));
    if (frag_color.a < alpha_discard) {
        discard;
    }
    frag_color2 = vec4(0.0);
}