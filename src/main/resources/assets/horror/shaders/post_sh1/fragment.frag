in vec2 out_texture;
layout (location = 0) out vec4 frag_color;

uniform sampler2D texture_sampler;
uniform vec2 screenSize;
uniform float panic;

uniform bool lose;
uniform bool won;

layout (std140, binding = 2) uniform Misc {
    float w_tick;
};

float rand(vec2 co)
{
    return fract(sin(dot(co.xy + tan(w_tick), vec2(12.9898, 78.233))) * 43758.5453);
}

vec4 random_noise_red() {
    float pixelSize = 0.005;
    vec2 tex = out_texture;
    vec2 pixelCoords = floor(tex / pixelSize) * pixelSize;
    vec4 colors = vec4(1., 0., 0., 1.);
    float grain = rand(pixelCoords);
    return colors * grain;
}

vec4 random_noise(vec4 txtr, float factor) {
    float pixelSize = 0.0035;
    vec2 tex = out_texture;
    vec2 pixelCoords = floor(tex / pixelSize) * pixelSize;
    vec4 colors = txtr;
    float grain = clamp(rand(pixelCoords) * (0.05 + factor * 0.1), 0.0, 1.0);
    return txtr + grain;
}

vec4 vinnette(vec4 txt, vec2 textCoords, float factor) {
    vec2 center = vec2(0.5, 0.5);
    float dist = length(textCoords - center);
    float vinnette = smoothstep(1.0, 0.0, dist * (factor * 1.0));
    return txt * (vec4(vec3(vinnette), 1.0));
}

vec2 curveUV(vec2 inVec, float factor) {
    const float C1 = w_tick * 5.0 + factor;
    const float C2 = factor * 0.05;

    return vec2(inVec.x + sin(inVec.y * 8.0) * sin(C1) * C2, inVec.y + sin(inVec.x * 16.0) * cos(C1) * C2);
}

void main()
{
    float panC = panic;

    vec4 color = texture(texture_sampler, curveUV(out_texture, panC));
    color = vinnette(color, out_texture, panC);
    color = random_noise(color, panC);

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

    if (lose) {
        frag_color = random_noise_red();
    } else if (won) {
        frag_color = color + vec4(vec3(min(w_tick * 0.3f, 1.)), 0.);
    } else {
        frag_color = color * min(w_tick, 1.);
    }
}