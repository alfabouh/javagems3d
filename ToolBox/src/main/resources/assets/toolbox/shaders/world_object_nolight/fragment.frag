layout (location = 0) out vec4 frag_color;

in vec3 mv_out_pos;
in vec2 out_texture;
in vec3 mv_vertex_normal;
in vec3 mv_vertex_pos;

uniform bool use_texturing;
uniform int texturing_code;

uniform sampler2D diffuse_map;
uniform vec4 diffuse_color;
uniform bool selected;
uniform float alpha_discard;

void main()
{
    frag_color = (use_texturing && (texturing_code & (1 << 2)) != 0) ? texture(diffuse_map, out_texture) : diffuse_color;
    if (frag_color.a < alpha_discard) {
        discard;
    }
    if (selected) {
        frag_color *= vec4(1.25, 0.25, 0.25, 1.0);
    }
    frag_color.a = 1.0;
}