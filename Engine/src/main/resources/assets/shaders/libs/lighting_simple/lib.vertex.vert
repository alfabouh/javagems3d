struct Sun {
    vec3 position;
    float brightness;
    vec3 color;
    float ambient;
};
layout (std430, binding = 5) readonly restrict buffer SunLight {
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
layout (std430, binding = 6) readonly restrict buffer PointLights {
    PointLight p_l[CONST.MAX_POINT_LIGHTS];
    int total_plights;
};

struct SpotLight
{
    vec3 position;
    float brightness;
    vec3 direction;
    int attachedShadowSceneId;
    vec3 view_position;
    float attenuationFactor;
    vec3 color;
    float cutOff;
    float clipRadius;
};
layout (std430, binding = 15) readonly restrict buffer SpotLights {
    SpotLight s_l[CONST.MAX_SPOT_LIGHTS];
    int total_slights;
};

vec3 calc_point_light_simple(PointLight light, vec3 particlePos) {
    float dist = length(light.position - particlePos);
    float attenuation = 1.0 / (1.0 + 0.1 * dist + 0.01 * dist * dist);
    return light.color * vec3((light.brightness * attenuation));
}

vec3 calc_spot_light_simple(mat4 view_matrix, SpotLight light, vec3 particlePos) {
    vec3 light_dir = light.position - particlePos;
    vec3 to_light = normalize(light_dir);
    float theta = dot(to_light, -(inverse(view_matrix) * vec4(light.direction, 0.)).xyz);
    if (theta < light.cutOff)
    {
        return vec3(0.);
    }
    float dist = length(light_dir);
    float attenuation = 1.0 / (1.0 + 0.1 * dist + 0.01 * dist * dist);
    return light.color * vec3((light.brightness * attenuation));
}