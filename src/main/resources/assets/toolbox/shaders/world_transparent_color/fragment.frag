layout (location = 0) out vec4 accumulated;
layout (location = 1) out float reveal;

uniform vec4 colour;
uniform bool selected;

void main()
{
    vec4 col = colour;
    if (selected) {
        col *= 0.5;
    }
    col.a = 0.5;

    float weight = max(min(1.0, max(max(col.r, col.g), col.b) * col.a), col.a) * clamp(0.03 / (1.0e-5f + pow(gl_FragCoord.z / 200, 4.0)), 1.0e-2f, 3.0e+3f);
    accumulated = vec4(col.rgb * col.a, col.a) * weight;
    reveal = col.a;
}