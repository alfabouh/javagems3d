#extension GL_ARB_bindless_texture : require
precision highp float;
precision highp int;

in vec2 uv_coordinates;
in vec4 frag_pos;

layout (location = 0) out vec4 frag_color0;

uniform float alpha_discard;
uniform vec4 diffuse_color;
uniform uvec2 diffuse_map;
uniform bool use_texture;
uniform vec3 lightPos;
uniform float far_plane;

void main()
{
    float diffuse_a = diffuse_color.a;
    if (use_texture) {
        diffuse_a *= texture(sampler2D(diffuse_map), uv_coordinates).a;
    }
    if (diffuse_a < alpha_discard) {
        discard;
    }
    float lightDistance = length(frag_pos.xyz - lightPos);
    lightDistance /= far_plane;

    float d = lightDistance;
    float dx = dFdx(d);
    float dy = dFdy(d);
    float moment2 = d * d + 0.25 * (dx * dx + dy * dy);
    frag_color0 = vec4(d, moment2, 0.0, 1.0);
}