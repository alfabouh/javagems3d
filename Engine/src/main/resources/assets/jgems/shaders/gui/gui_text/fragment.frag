#extension GL_ARB_bindless_texture : require

layout (location = 0) out vec4 frag_color;
in vec2 uv_coordinates;

uniform vec4 color;
uniform sampler2D texture_map;

void main()
{
    frag_color = color * texture(texture_map, uv_coordinates);
}
