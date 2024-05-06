layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 frag_color2;

in vec2 texture_coordinates;
uniform sampler2D diffuse_map;
in vec3 mv_vertex_pos;

layout (std140, binding = 3) uniform Fog {
    float fogDensity;
    float fogColorR;
    float fogColorG;
    float fogColorB;
};

layout (std140, binding = 0) uniform SunLight {
    float ambient;
    float sunBright;
    float sunX;
    float sunY;
    float sunZ;
    float sunColorR;
    float sunColorG;
    float sunColorB;
};

vec4 calc_fog(vec3 frag_pos, vec4 color) {
    vec3 fog_color = vec3(fogColorR, fogColorG, fogColorB) * vec3(sunColorR, sunColorG, sunColorB) * sunBright;
    float distance = length(frag_pos);
    float fogFactor = 1. / exp((distance * fogDensity) * (distance * fogDensity));
    fogFactor = clamp(fogFactor, 0., 1.);

    vec3 result = mix(fog_color, color.xyz, fogFactor);
    return vec4(result.xyz, color.w);
}

void main()
{
    vec4 t_col = texture(diffuse_map, vec2(texture_coordinates.x, 1.0 - texture_coordinates.y));
    t_col = pow(t_col, vec4(2.0)) * vec4(vec3(3.0), 1.0);
    t_col = vec4(vec3(dot(t_col.rgb, vec3(0.299, 0.587, 0.114))), t_col.a);

    frag_color = t_col;
    frag_color = fogDensity > 0 ? calc_fog(mv_vertex_pos, frag_color) : frag_color;
    frag_color2 = vec4(0.0);
}