in vec2 out_texture;
layout (location = 0) out vec4 frag_color;

uniform sampler2D texture_sampler;
uniform float kernel[5];

vec4 calc_blur() {
    vec2 texel_size = textureSize(texture_sampler, 0);
    vec2 fragCoord = gl_FragCoord.xy;
    vec4 color = vec4(0.);

    for (int i = 0; i < 5; ++i) {
        for (int j = 0; j < 5; ++j) {
            vec2 offset = vec2(float(i) - float(5) / 2.0, float(j) - float(5) / 2.0);
            vec2 uv = (fragCoord + offset) / texel_size;
            color += texture(texture_sampler, uv) * kernel[i] * kernel[j];
        }
    }
    return color;
}

void main()
{
    frag_color = calc_blur();
}