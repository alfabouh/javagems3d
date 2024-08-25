in vec2 out_texture;
layout (location = 0) out vec4 frag_color;

uniform sampler2D texture_sampler;
uniform vec2 screenSize;

void main()
{
    vec4 color = texture(texture_sampler, out_texture);

    float bayerMatrix[16] = float[16](
    0.0,  8.0,  2.0, 10.0,
    12.0, 4.0, 14.0, 6.0,
    3.0, 11.0, 1.0,  9.0,
    15.0, 7.0, 13.0, 5.0
    );

    vec2 pixelPos = out_texture * screenSize;

    int x = int(mod(pixelPos.x, 4.0));
    int y = int(mod(pixelPos.y, 4.0));
    int index = x + y * 4;

    float ditherValue = (bayerMatrix[index] / 16.0) - 0.5;

    color.rgb = floor(color.rgb * 8.0 + ditherValue) / 8.0;

    frag_color = color;
}