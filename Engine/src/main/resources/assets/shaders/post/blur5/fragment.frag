#extension GL_ARB_bindless_texture : enable

layout (location = 0) out vec4 frag_color;
uniform sampler2D texture_map;
uniform vec2 direction;
in vec2 uv_coordinates;

vec4 blur(sampler2D txt, vec2 uv) {
    vec2 texel = 1.0 / vec2(textureSize(txt, 0));
    vec4 color = vec4(0.0);
    vec2 off1 = vec2(1.3333333333333333) * direction;
    color += texture(txt, uv) * 0.29411764705882354;
    color += texture(txt, uv + (off1 * texel)) * 0.35294117647058826;
    color += texture(txt, uv - (off1 * texel)) * 0.35294117647058826;
    return color;
}

void main()
{
    frag_color = blur(texture_map, uv_coordinates);
}