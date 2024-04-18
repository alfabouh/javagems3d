layout (location = 0) out vec4 frag_color;
uniform sampler2D texture_sampler;
uniform vec2 resolution;
uniform vec2 direction;

vec4 blur(sampler2D txt, vec2 uv, vec2 res) {
    vec4 color = vec4(0.0);
    vec2 off1 = vec2(1.3846153846) * direction;
    vec2 off2 = vec2(3.2307692308) * direction;
    color += texture(txt, uv) * 0.2270270270;
    color += texture(txt, uv + (off1 / resolution)) * 0.3162162162;
    color += texture(txt, uv - (off1 / resolution)) * 0.3162162162;
    color += texture(txt, uv + (off2 / resolution)) * 0.0702702703;
    color += texture(txt, uv - (off2 / resolution)) * 0.0702702703;
    return color;
}

void main()
{
    vec2 texel_size = textureSize(texture_sampler, 0);
    frag_color = blur(texture_sampler, gl_FragCoord.xy / texel_size, resolution);
}