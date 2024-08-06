layout (location = 0) out vec4 frag_color;
uniform sampler2D texture_sampler;
uniform vec2 resolution;
uniform vec2 direction;

vec4 blur(sampler2D txt, vec2 uv, vec2 res) {
    vec4 color = vec4(0.0);
    vec2 off1 = vec2(1.3333333333333333) * direction;
    color += texture(txt, uv) * 0.29411764705882354;
    color += texture(txt, uv + (off1 / resolution)) * 0.35294117647058826;
    color += texture(txt, uv - (off1 / resolution)) * 0.35294117647058826;
    return color;
}

void main()
{
    vec2 texel_size = textureSize(texture_sampler, 0);
    frag_color = blur(texture_sampler, gl_FragCoord.xy / texel_size, resolution);
}