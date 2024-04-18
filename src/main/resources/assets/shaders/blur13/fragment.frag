layout (location = 0) out vec4 frag_color;
uniform sampler2D texture_sampler;
uniform vec2 resolution;
uniform vec2 direction;

vec4 blur(sampler2D txt, vec2 uv, vec2 res) {
    vec4 color = vec4(0.0);
    vec2 off1 = vec2(1.411764705882353) * direction;
    vec2 off2 = vec2(3.2941176470588234) * direction;
    vec2 off3 = vec2(5.176470588235294) * direction;
    color += texture(txt, uv) * 0.1964825501511404;
    color += texture(txt, uv + (off1 / resolution)) * 0.2969069646728344;
    color += texture(txt, uv - (off1 / resolution)) * 0.2969069646728344;
    color += texture(txt, uv + (off2 / resolution)) * 0.09447039785044732;
    color += texture(txt, uv - (off2 / resolution)) * 0.09447039785044732;
    color += texture(txt, uv + (off3 / resolution)) * 0.010381362401148057;
    color += texture(txt, uv - (off3 / resolution)) * 0.010381362401148057;
    return color;
}

void main()
{
    vec2 texel_size = textureSize(texture_sampler, 0);
    frag_color = blur(texture_sampler, gl_FragCoord.xy / texel_size, resolution);
}