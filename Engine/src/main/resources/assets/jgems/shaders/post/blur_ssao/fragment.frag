layout (location = 0) out float frag_color;

uniform sampler2D texture_sampler;
in vec2 texture_coordinates;

void main()
{
    vec2 texelSize = 1.0 / vec2(textureSize(texture_sampler, 0));
    float result = 0.0;
    for (int x = -2; x < 2; ++x)
    {
        for (int y = -2; y < 2; ++y)
        {
            vec2 offset = vec2(float(x), float(y)) * texelSize;
            result += texture(texture_sampler, texture_coordinates + offset).r;
        }
    }
    frag_color = result / (4.0 * 4.0);
}