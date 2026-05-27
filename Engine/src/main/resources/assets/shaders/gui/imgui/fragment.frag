#extension GL_ARB_bindless_texture : require


layout (location = 0) out vec4 frag_color;

in vec2 uv_coordinates;
in vec4 out_color;

uniform sampler2D texture_map;

void main()
{
    frag_color = out_color * texture(texture_map, uv_coordinates);
}
