in vec2 out_texture;
layout (location = 0) out vec4 frag_color;

uniform float offset;
uniform float w_tick;
uniform sampler2D texture_sampler;

vec2 curveUV(vec2 inVec, float factor) {
    vec2 curveUV = inVec;
    curveUV = curveUV * 2.0 - 1.0;

    float dist = length(curveUV);
    float curve = (1.0 + factor * 1.5) - dist * dist * factor;

    curveUV *= curve;
    curveUV = curveUV * 0.5 + 0.5;

    return curveUV;
}

void main()
{
    const float panic_val = 0.025;

    vec2 offres = vec2(offset / textureSize(texture_sampler, 0));
    vec2 texCoords = (out_texture / (1.0 - offres)) - offres / 2.0;

    vec2 distortedCoord = curveUV(texCoords, panic_val * 0.5);

    const float C1 = w_tick * 10.0 + panic_val;
    const float C2 = panic_val * 0.1;

    distortedCoord.x += sin(texCoords.y * 8.0) * sin(C1) * C2;
    distortedCoord.y += sin(texCoords.x * 16.0) * cos(C1) * C2;

    frag_color = texture(texture_sampler, distortedCoord);
}
