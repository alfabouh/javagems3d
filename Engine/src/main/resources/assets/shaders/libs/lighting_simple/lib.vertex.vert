struct Sun {
    vec3 position;
    float brightness;
    vec3 color;
    float ambient;
};
layout (std430, binding = 5) buffer SunLight {
    Sun sun;
};

struct PointLight
{
    vec3 position;
    float brightness;
    vec3 view_position;
    int attachedShadowSceneId;
    vec3 color;
    int clipRadius;
};
layout (std430, binding = 6) buffer PointLights {
    PointLight p_l[CONST.MAX_POINT_LIGHTS];
    int total_plights;
};

vec3 calc_point_light_simple(PointLight light, vec3 particlePos) {
    float dist = length(light.position - particlePos);
    float attenuation = 1.0 / (1.0 + 0.1 * dist + 0.01 * dist * dist);
    return light.color * vec3((light.brightness * attenuation));
}