layout (location = 0) out vec4 frag_color;

in vec3 mv_out_pos;
in vec2 uv_coordinates;

uniform bool use_texture;
uniform sampler2D diffuse_map;

void main()
{
    float f1 = min(uv_coordinates.y, uv_coordinates.x) + 0.25;
    frag_color = use_texture ? texture(diffuse_map, uv_coordinates) : vec4(vec3(f1), 1.0);
}