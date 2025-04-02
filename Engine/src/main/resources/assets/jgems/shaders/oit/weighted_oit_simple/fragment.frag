in vec3 modelview_vertex_pos;
in vec3 normal;

layout (location = 0) out vec4 accumulated;
layout (location = 1) out float reveal;
layout (location = 2) out vec4 bright_color;

struct Fog {
    vec3 color;
    float density;
};
layout (std430, binding = 7) buffer WorldFog {
    Fog fog;
};

uniform vec4 color;

float calc_fog_float(vec3 frag_pos, float f) {
    float distance = length(frag_pos);
    float fogFactor = 1. / exp((distance * fog.density) * (distance * fog.density));
    fogFactor = clamp(fogFactor, 0., 1.);
    return f * fogFactor;
}

vec4 calc_fog(vec3 frag_pos, vec4 color) {
    if (fog.density <= 0) {
        return color;
    }

    vec3 fog_color = fog.color;
    float distance = length(frag_pos);
    float fogFactor = 1. / exp((distance * fog.density) * (distance * fog.density));
    fogFactor = clamp(fogFactor, 0., 1.);

    vec3 result = mix(fog_color, color.xyz, fogFactor);
    return vec4(result.xyz, color.w);
}

void main()
{
    vec3 gPosition = modelview_vertex_pos;

    vec4 frag_color = color;
    frag_color = calc_fog(gPosition, frag_color);
    float dotFloat = max(dot(vec3(0.75, 1.0, 0.75), normal), 0.5);

    float weight = max(min(1.0, max(max(frag_color.r, frag_color.g), frag_color.b) * frag_color.a), frag_color.a) * clamp(0.03 / (1.0e-5f + pow(gl_FragCoord.z / 200.0, 4.0)), 1.0e-2f, 3.0e+3f);
    accumulated = vec4((frag_color.rgb * dotFloat) * frag_color.a, frag_color.a) * weight;

    reveal = frag_color.a;
    reveal = calc_fog_float(gPosition, frag_color.a);

    bright_color = vec4(0., 0., 0., 1.);
}