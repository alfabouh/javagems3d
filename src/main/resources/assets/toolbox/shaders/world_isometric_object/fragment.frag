layout (location = 0) out vec4 frag_color;

in vec3 mv_out_pos;
in vec2 out_texture;
uniform int texturing_code;

uniform sampler2D diffuse_map;
uniform vec3 diffuse_color;

void main()
{
    frag_color = ((texturing_code & (1 << 2)) != 0) ? texture(diffuse_map, out_texture) : vec4(diffuse_color, 1.0);
}