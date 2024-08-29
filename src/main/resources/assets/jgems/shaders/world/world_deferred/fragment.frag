in vec2 out_texture;
in mat4 out_view_matrix;
in mat4 out_inversed_view_matrix;

layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 bright_color;

struct CascadeShadow {
    float split_distance;
    mat4 projection_view;
};

uniform CascadeShadow cascade_shadow[3];
uniform samplerCube point_light_cubemap[3];
uniform sampler2D sun_shadow_map[3];
uniform float far_plane;
uniform bool isSsaoValid;

struct PointLight
{
    vec4 plPos;
    vec4 plViewPos;
    vec4 plColor;
    vec2 plMeta;
};

layout (std140, binding = 0) uniform SunLight {
    vec4 sunPos;
    vec4 sunColor;
    vec2 sunMeta;
};

layout (std140, binding = 1) uniform PointLights {
    PointLight p_l[128];
    int total_plights;
};

layout (std140, binding = 3) uniform Fog {
    vec4 fogColor;
    float fogDensity;
};

uniform sampler2D gPositions;
uniform sampler2D gNormals;
uniform sampler2D gTexture;
uniform sampler2D gEmission;
uniform sampler2D gSpecular;
uniform sampler2D gMetallic;
uniform sampler2D ssaoSampler;

vec4 calc_sun_light(vec3, vec3, vec3);
vec4 calc_point_light(PointLight, vec3, vec3, float, float, float, float);
vec4 calc_light_factor(vec3, float, vec3, vec3, vec3);
vec4 calc_light(vec3, vec3);
vec4 calc_fog(vec3, vec4);

void main()
{
    vec3 frag_pos = texture(gPositions, out_texture).xyz;
    vec3 normals = texture(gNormals, out_texture).xyz;
    vec4 g_texture = texture(gTexture, out_texture);
    vec4 emission = vec4(texture(gEmission, out_texture).rgb, 0.0) * vec4(5.);
    vec4 metallic = texture(gMetallic, out_texture);

    float gray = dot(g_texture.rgb, vec3(0.299, 0.587, 0.114));
    float AO = isSsaoValid ? texture(ssaoSampler, out_texture).r : 1.;
    float f1 = pow(AO, (1.0 - gray) * 3.);

    vec4 lights = calc_light(frag_pos, normals) * vec4(f1);

    frag_color = (g_texture + vec4(metallic.xyz, 0.)) * (lights + emission);
    frag_color = calc_fog(frag_pos.xyz, frag_color);

    float brightness = dot(frag_color.rgb + (emission.rgb), vec3(0.2126, 0.7152, 0.0722));
    bright_color = brightness >= 2.0 ? vec4(frag_color.xyz, 1.) : vec4(0., 0., 0., 1.);
}

float vsmFixLightBleed(float pMax, float amount) {
    return clamp((pMax - amount) / (1.0 - amount), 0.0, 1.0);
}

float VSM(int idx, vec4 shadow_coord, float bias) {
    vec4 vsm = texture(sun_shadow_map[idx], shadow_coord.xy);

    float E_x2 = vsm.y;
    float Ex_2 = vsm.x * vsm.x;
    float var = max(E_x2 - Ex_2, bias);
    float mD = vsm.x - shadow_coord.z;
    float mD_2 = mD * mD;
    float p = var / (var + mD_2);

    return max(vsmFixLightBleed(p, 0.7), int(shadow_coord.z <= vsm.x));
}

float calcShadowDepth(int idx, vec4 shadow_coord, float bias) {
    return VSM(idx, shadow_coord, bias);
}

float calculate_shadow_vsm(vec4 worldPosition, int idx, float bias) {
    vec4 shadowMapPos = cascade_shadow[idx].projection_view * worldPosition;
    vec4 shadow_coord = (shadowMapPos / shadowMapPos.w) * 0.5 + 0.5;
    float c0 = calcShadowDepth(idx, shadow_coord, bias);
    return c0;
}

float calcSunShineVSM(vec4 world_position, vec3 frag_pos) {
    const float bias = 1.0e-5f;
    const float bias_f = 3.0;
    const float half_bias_f = bias_f / 2.0;
    const int max_cascades = 3;
    int cascadeIndex = int(frag_pos.z < cascade_shadow[0].split_distance - half_bias_f) + int(frag_pos.z < cascade_shadow[1].split_distance - half_bias_f);
    float f0 = calculate_shadow_vsm(world_position, cascadeIndex, bias);
    if (cascadeIndex >= 0 && cascadeIndex < max_cascades) {
        int cascadeIndex2 = int(frag_pos.z < cascade_shadow[cascadeIndex].split_distance + half_bias_f) + cascadeIndex;
        float f1 = calculate_shadow_vsm(world_position, cascadeIndex2, bias);
        float p2 = (cascade_shadow[cascadeIndex].split_distance + half_bias_f) - frag_pos.z;
        return mix(f0, f1, p2 / bias_f);
    }
    return f0;
}

float calculate_point_light_shadow(samplerCube vsmCubemap, vec3 fragPosition, vec3 lightPos)
{
    vec3 fragToLight = fragPosition - lightPos;
    float currentDepth = length(fragToLight);
    currentDepth /= far_plane;

    vec4 vsm = texture(vsmCubemap, normalize(fragToLight));

    float E_x2 = vsm.y;
    float Ex_2 = vsm.x * vsm.x;
    float var = max(E_x2 - Ex_2, 1.0e-5f);
    float mD = vsm.x - currentDepth;
    float mD_2 = mD * mD;
    float p = var / (var + mD_2);

    return max(vsmFixLightBleed(p, 0.7), int(currentDepth <= vsm.x));
}

vec4 calc_light(vec3 frag_pos, vec3 normal) {
    vec4 lightFactors = vec4(sunColor.xyz * sunMeta.x, 1.0);

    vec3 sunPos = normalize(sunPos.xyz);

    vec4 view_pos = vec4(frag_pos, 1.0);
    vec4 world_position = out_inversed_view_matrix * view_pos;
    world_position /= world_position.w;

    float sun_shadow = calcSunShineVSM(world_position, frag_pos);

    vec4 sunFactor = calc_sun_light(sunPos, frag_pos, normal);

    vec4 point_light_factor = vec4(0.0);
    for (int i = 0; i < total_plights; i++) {
        PointLight p = p_l[i];
        float p_brightness = p.plMeta.x;
        float at_base = 1.0;
        float linear = 0.09 * p_brightness;
        float expo = 0.032 * p_brightness;
        float p_id = p.plMeta.y;
        vec4 shadow = p_id >= 0 ? vec4(calculate_point_light_shadow(point_light_cubemap[int(p_id)], world_position.xyz, p.plPos.xyz)) : vec4(1.0);
        point_light_factor += calc_point_light(p, frag_pos, normal, at_base, linear, expo, p_brightness) * shadow;
    }

    float brightness = dot(point_light_factor.rgb, vec3(0.2126, 0.7152, 0.0722)) * 5.0;
    lightFactors += sunFactor * clamp(sun_shadow + brightness, 0.0, 1.0);

    lightFactors += point_light_factor;

    return lightFactors;
}

vec4 calc_light_factor(vec3 colors, float brightness, vec3 vPos, vec3 light_dir, vec3 vNormal) {
    if (dot(vNormal, light_dir) + 1.e-5 < 0) {
        return vec4(0.);
    }
    vec4 diffuseC = vec4(0.);
    vec4 specularC = vec4(0.);

    float specularF = 0.;
    float diffuseF = max(dot(vNormal, light_dir), 0.);
    diffuseC = vec4(colors, 1.) * brightness * diffuseF;

    vec3 camDir = normalize(-vPos);
    vec3 reflectionF = normalize(light_dir + camDir);
    specularF = max(dot(vNormal, reflectionF), 0.);
    specularF = pow(specularF, 8.0);
    specularC = brightness * specularF * vec4(colors, 1.);

    vec4 specularFactor = vec4(vec3(1.0) - texture(gSpecular, out_texture).rgb, 1.0);
    return diffuseC + (specularC * specularFactor);
}

vec4 calc_sun_light(vec3 sunPos, vec3 vPos, vec3 vNormal) {
    return calc_light_factor(sunColor.xyz, sunMeta.y, vPos, normalize(sunPos), vNormal);
}

vec4 calc_point_light(PointLight light, vec3 vPos, vec3 vNormal, float at_base, float linear, float expo, float bright) {
    vec3 pos = light.plViewPos.xyz;

    vec3 light_dir = pos - vPos;
    vec3 to_light = normalize(light_dir);
    vec4 light_c = calc_light_factor(light.plColor.xyz, bright, vPos, to_light, vNormal);

    float dist = length(light_dir);
    float attenuation_factor = at_base + linear * dist + expo * pow(dist, 2);
    return light_c / attenuation_factor;
}

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