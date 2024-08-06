layout (location = 0) out vec4 frag_color;

in vec2 out_texture;
uniform sampler2D texture_sampler;
uniform float blur;

void main()
{
    float blur_radius = blur;

    vec2 texel_size = 1.0 / textureSize(texture_sampler, 0);

    vec4 color = vec4(0.0);
    int radius = int(blur_radius);

    for (int y = -radius; y <= radius; ++y)
    {
        for (int x = -radius; x <= radius; ++x)
        {
            vec2 offset = vec2(float(x), float(y)) * texel_size;
            color += texture(texture_sampler, out_texture + offset);
        }
    }

    float num_samples = float((2 * radius + 1) * (2 * radius + 1));
    color /= num_samples;

    frag_color = color;
}

/*
void main()
{
    float blur_radius = 13.0f;

    vec2 texel_size = 1.0 / resolution;
    vec2 uv = gl_FragCoord.xy / resolution;

    vec4 color = vec4(0.0);
    int radius = int(blur_radius);

    int num_samples = 0;
    for (int y = -radius; y <= radius; ++y)
    {
        for (int x = -radius; x <= radius; ++x)
        {
            vec2 offset = vec2(float(x), float(y)) * texel_size;
            vec2 sample_uv = uv + offset;

            if (sample_uv.x >= 0.0 && sample_uv.x <= 1.0 && sample_uv.y >= 0.0 && sample_uv.y <= 1.0)
            {
                color += texture(texture_sampler, sample_uv);
                num_samples++;
            }
        }
    }

    color /= num_samples > 0 ? float(num_samples) : 1.0;

    frag_color = color;
}
/*

void main()
{
    float blur_radius = 3.0f;

    vec2 texel_size = 1.0 / resolution;
    vec2 uv = gl_FragCoord.xy / resolution;

    vec4 color = vec4(0.0);
    int radius = int(blur_radius);

    for (int y = -radius; y <= radius; ++y)
    {
        for (int x = -radius; x <= radius; ++x)
        {
            vec2 offset = vec2(float(x), float(y)) * texel_size;
            color += texture(texture_sampler, uv + offset);
        }
    }

    float num_samples = float((2 * radius + 1) * (2 * radius + 1));
    color /= num_samples;

    frag_color = color;
}
*/