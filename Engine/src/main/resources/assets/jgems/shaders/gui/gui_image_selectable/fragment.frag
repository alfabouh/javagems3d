#extension GL_ARB_bindless_texture : require

layout (location = 0) out vec4 frag_color;
in vec2 uv_coordinates;

uniform sampler2D texture_map;
uniform bool selected;

void main()
{
    vec4 sel = selected ? vec4(vec3(0.5), 1.0) : vec4(1.0);
    frag_color = texture(texture_map, uv_coordinates) * sel;
}
