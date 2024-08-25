layout (location = 0) out vec4 accumulated;
layout (location = 1) out float reveal;

uniform vec4 colour;
uniform bool selected;

uniform bool showFog;
uniform float sunBright;
uniform vec3 fogColor;
uniform float fogDensity;

in vec3 mv_out_pos;

float calc_fog_float(vec3 frag_pos, float f) {
    float distance = length(frag_pos);
    float fogFactor = 1. / exp((distance * fogDensity) * (distance * fogDensity));
    fogFactor = clamp(fogFactor, 0., 1.);
    return f * fogFactor;
}

vec4 calc_fog(vec3 frag_pos, vec4 color) {
    vec3 fog_color = fogColor * sunBright;
    float distance = length(frag_pos);
    float fogFactor = 1. / exp((distance * fogDensity) * (distance * fogDensity));
    fogFactor = clamp(fogFactor, 0., 1.);

    vec3 result = mix(fog_color, color.xyz, fogFactor);
    return vec4(result.xyz, color.w);
}

void main()
{
    vec4 col = colour;
    col.a = 0.5;
    if (selected) {
        col = vec4(vec3(1.) - col.xyz, col.a);
    }

    float weight = max(min(1.0, max(max(col.r, col.g), col.b) * col.a), col.a) * clamp(0.03 / (1.0e-5f + pow(gl_FragCoord.z / 200, 4.0)), 1.0e-2f, 3.0e+3f);
    accumulated = vec4(col.rgb * col.a, col.a) * weight;
    accumulated = (showFog ? calc_fog(mv_out_pos, accumulated) : accumulated);
    reveal = (showFog ? calc_fog_float(mv_out_pos, col.a) : col.a);
}