struct Sun {
    vec3 position;
    float _padding0; //PAD
    vec3 color;
    float ambient;
    float brightness;
};
layout (std430, binding = 5) buffer SunLight {
    Sun sun;
};

struct PointLight
{
    vec3 position;
    float _padding0;
    vec3 view_position;
    float _padding00;
    vec3 color;
    float brightness;
    int attachedShadowSceneId;
    int _padding000;
    int _padding0000;
    int _padding00000;
};
layout (std430, binding = 6) buffer PointLights {
    PointLight p_l[CONST.MAX_POINT_LIGHTS];
    int total_plights;
};

vec3 calc_light_factor(vec3 colors, float brightness, vec3 vPos, vec3 light_dir, vec3 vNormal, float specularFactor) {
    if (dot(vNormal, light_dir) + 1.e-24 < 0) {
        return vec3(0.);
    }
    vec3 diffuseC = vec3(0.);
    vec3 specularC = vec3(0.);
    float specularF = 0.;
    float diffuseF = max(dot(vNormal, light_dir), 0.);
    diffuseC = vec3(colors) * brightness * diffuseF;
    vec3 camDir = normalize(-vPos);
    vec3 reflectionF = normalize(light_dir + camDir);
    specularF = max(dot(vNormal, reflectionF), 0.);
    specularF = pow(specularF, 8.0);
    specularC = brightness * specularF * vec3(colors);
    return diffuseC + (specularC * specularFactor);
}

vec3 calc_sun_light(vec3 position, vec3 vPos, vec3 vNormal, float specularFactor) {
    return calc_light_factor(sun.color, sun.brightness, vPos, normalize(position), vNormal, specularFactor);
}

vec3 calc_point_light(PointLight light, vec3 vPos, vec3 vNormal, float at_base, float linear, float expo, float bright, float specularFactor) {
    vec3 pos = light.view_position;
    vec3 light_dir = pos - vPos;
    vec3 to_light = normalize(light_dir);
    vec3 light_c = calc_light_factor(light.color, bright, vPos, to_light, vNormal, specularFactor);
    float dist = length(light_dir);
    float attenuation_factor = at_base + linear * dist + expo * pow(dist, 2);
    return light_c / attenuation_factor;
}

vec3 getParams(float brightness) {
    float at_base = 1.0;
    float linear = 0.1 * brightness;
    float expo = 0.032 / sqrt(brightness);
    return vec3(at_base, linear, expo);
}