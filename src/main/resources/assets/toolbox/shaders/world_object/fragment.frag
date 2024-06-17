layout (location = 0) out vec4 frag_color;

in vec3 mv_out_pos;
in vec2 out_texture;
in vec3 mv_vertex_normal;
in vec3 mv_vertex_pos;

uniform int texturing_code;

uniform bool showLight;
in vec3 outSunPos;
uniform vec3 sunColor;
uniform float sunBright;

uniform sampler2D diffuse_map;
uniform vec3 diffuse_color;
uniform bool selected;

vec4 calc_sun_light(vec3 vPos, vec3 vNormal) {
    float diffuseF = max(dot(vNormal, normalize(outSunPos)), 0.);
    vec3 diffuseC = sunColor * sunBright * diffuseF;
    return vec4(diffuseC + vec3(sunBright * sunColor) * 0.7, 1.0);
}

void main()
{
    frag_color = ((texturing_code & (1 << 2)) != 0) ? vec4(texture(diffuse_map, out_texture).rgb, 1.0) : vec4(diffuse_color, 1.0);
    if (selected) {
        frag_color *= vec4(1.25, 0.25, 0.25, 1.0);
    }
    if (showLight) {
        frag_color *= calc_sun_light(mv_vertex_pos, mv_vertex_normal);
    }
}