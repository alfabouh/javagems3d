#extension GL_ARB_bindless_texture : enable

in vec2 uv_coordinates;
layout (location = 0) out vec4 frag_color;

uniform uvec2 texture_map;
uniform uvec2 bloom_map;
uniform bool use_hdr;

uniform float exposure;
uniform float gamma;

vec4 hdr(vec4 in_col, float exposure, float gamma) {
    vec3 rgb = in_col.rgb;
    vec3 bl_c = texture(sampler2D(bloom_map), uv_coordinates).rgb;
    rgb += bl_c;
    vec3 mapped = vec3(1.) - exp(-rgb * exposure);
    mapped = pow(mapped, vec3(1. / gamma));
    return vec4(mapped, in_col.a);
}

vec4 no_hdr(vec4 in_col) {
    vec3 rgb = in_col.rgb;
    vec3 bl_c = texture(sampler2D(bloom_map), uv_coordinates).rgb;
    rgb += bl_c;
    return vec4(rgb, in_col.a);
}

void main() {
  frag_color = use_hdr ? hdr(texture(sampler2D(texture_map), uv_coordinates), exposure, gamma) : no_hdr(texture(sampler2D(texture_map), uv_coordinates));
}