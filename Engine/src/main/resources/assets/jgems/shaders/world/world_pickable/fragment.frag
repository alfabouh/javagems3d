layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 frag_color2;

layout (std140, binding = 3) uniform Fog {
    vec4 fogColor;
    float fogDensity;
};

in vec3 mv_vertex_pos;
in vec2 texture_coordinates;
uniform sampler2D diffuse_map;
uniform float alpha_discard;

vec4 calc_fog(vec3 frag_pos, vec4 color) {
    if (fogDensity <= 0) {
        return color;
    }
    vec3 fog_color = fogColor.xyz;
    float distance = length(frag_pos);
    float fogFactor = 1. / exp((distance * fogDensity) * (distance * fogDensity));
    fogFactor = clamp(fogFactor, 0., 1.);

    vec3 result = mix(fog_color, color.xyz, fogFactor);
    return vec4(result.xyz, color.w);
}

void main()
{
    frag_color = texture(diffuse_map, vec2(texture_coordinates.x, texture_coordinates.y));
    if (frag_color.a < alpha_discard) {
        discard;
    }
    frag_color = calc_fog(mv_vertex_pos, frag_color);
    frag_color2 = vec4(0.0);
}