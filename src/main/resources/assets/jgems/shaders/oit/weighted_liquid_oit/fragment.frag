//in vec2 out_texture;
//
//layout (location = 0) out vec4 accumulated;
//layout (location = 1) out float reveal;
//
//in vec2 texture_coordinates;
//
//uniform float alpha_factor;
//
//void main()
//{
//    //vec4 color = texture(diffuse_map, texture_coordinates);
//    //if (color.a >= 1.) {
//    //    discard;
//    //}
//    vec4 color = vec4(1., 0., 0., 0.5);
//    float weight = max(min(1.0, max(max(color.r, color.g), color.b) * color.a), color.a) * clamp(0.03 / (1.0e-5f + pow(gl_FragCoord.z / 200, 4.0)), 1.0e-2f, 3.0e+3f);
//
//    accumulated = vec4(color.rgb * color.a, color.a) * weight;
//    reveal = color.a;
//}

/////////////////////////////////

in vec2 texture_coordinates;

in vec3 m_vertex_normal;
in vec4 m_vertex_pos;
in vec3 mv_vertex_normal;
in vec3 mv_vertex_pos;

in mat3 TBN;
in mat4 out_view_matrix;

layout (location = 0) out vec4 accumulated;
layout (location = 1) out float reveal;
layout (location = 2) out vec4 bright_color;

struct CascadeShadow {
    float split_distance;
    mat4 projection_view;
};

uniform CascadeShadow cascade_shadow[3];
uniform samplerCube point_light_cubemap[3];
uniform sampler2D sun_shadow_map[3];
uniform float far_plane;

struct PointLight
{
    float plPosX;
    float plPosY;
    float plPosZ;
    float plR;
    float plG;
    float plB;
    float brightness;
    float shadowMapId;
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

layout (std140, binding = 1) uniform PointLights {
    PointLight p_l[128];
    int total_plights;
};

layout (std140, binding = 2) uniform Misc {
    float w_tick;
};

layout (std140, binding = 3) uniform Fog {
    float fogDensity;
    float fogColorR;
    float fogColorG;
    float fogColorB;
};

uniform vec3 camera_pos;

uniform int texturing_code;
uniform int lighting_code;

const int light_opacity_code = 1 << 2;
const int light_bright_code = 1 << 3;

const int diffuse_code = 1 << 2;
const int emission_code = 1 << 3;
const int metallic_code = 1 << 4;
const int normals_code = 1 << 5;
const int specular_code = 1 << 6;

uniform samplerCube ambient_cubemap;
uniform vec4 diffuse_color;
uniform sampler2D diffuse_map;
uniform sampler2D normals_map;
uniform sampler2D emissive_map;
uniform sampler2D specular_map;
uniform sampler2D metallic_map;

uniform float alpha_factor;
uniform vec2 texture_scaling;

vec4 calc_sun_light(vec3, vec3, vec3);
vec4 calc_point_light(PointLight, vec3, vec3, float, float, float, float);
vec4 calc_light_factor(vec3, float, vec3, vec3, vec3);
vec4 calc_light(vec3, vec3);
vec4 calc_fog(vec3, vec4);
float calc_fog_float(vec3, float);

bool checkCode(int i1, int i2) {
    int i3 = i1 & i2;
    return bool(i3 != 0);
}

vec4 refract_cubemap(vec3 normal, float cnst) {
    float fogFactor = fogDensity * 100;
    float f = 1.0 - clamp(fogFactor, 0.0, 0.7);

    float ratio = 1.0 / cnst;
    vec3 I = normalize(m_vertex_pos.xyz - camera_pos);
    vec3 R = refract(I, normalize(normal), ratio);
    return f * vec4(texture(ambient_cubemap, R).rgb, 1.0);
}

vec2 getScaledTexture() {
    const float speed = 5.;

    float wave1 = sin(texture_coordinates.x * 20.0 + (w_tick * speed)) * 0.01;
    float wave2 = cos(texture_coordinates.y * 25.0 + (w_tick * speed) * 0.5) * 0.01;
    vec2 sincosFactor = vec2(wave1, wave2);

    return (texture_coordinates) * (texture_scaling) + sincosFactor;
}

vec3 calc_normal_map() {
    vec3 normal = texture(normals_map, getScaledTexture()).rgb;
    normal = normalize(normal * 2.0 - 1.0);
    normal = normalize(TBN * normal);
    return normal;
}

void main()
{
    vec3 frag_pos = mv_vertex_pos;
    vec3 normals = normalize(checkCode(texturing_code, normals_code) ? calc_normal_map() : mv_vertex_normal);
    vec4 g_texture = checkCode(texturing_code, diffuse_code) ? texture(diffuse_map, getScaledTexture()) : diffuse_color;
    vec4 emission = checkCode(lighting_code, light_bright_code) ? vec4(1.0) : checkCode(texturing_code, emission_code) ? texture(emissive_map, getScaledTexture()) : vec4(vec3(0.0), 1.0);
    vec4 metallic = (checkCode(texturing_code, metallic_code) ? texture(metallic_map, getScaledTexture()) : vec4(vec3(0.0), 1.0)) * refract_cubemap(m_vertex_normal, 1.73);

    vec4 lights = calc_light(frag_pos, normals);

    vec4 frag_color = (g_texture + vec4(metallic.xyz, 0.)) * (lights + emission);
    frag_color = fogDensity > 0 ? calc_fog(frag_pos.xyz, frag_color) : frag_color;
    frag_color = vec4(frag_color.xyz, g_texture.a);

    float a_factor = alpha_factor * frag_color.a;

    float weight = max(min(1.0, max(max(frag_color.r, frag_color.g), frag_color.b) * a_factor), a_factor) * clamp(0.03 / (1.0e-5f + pow(gl_FragCoord.z / 200.0, 4.0)), 1.0e-2f, 3.0e+3f);

    accumulated = vec4(frag_color.rgb * a_factor, a_factor) * weight;

    reveal = fogDensity > 0 ? calc_fog_float(frag_pos.xyz, a_factor) : a_factor;

    float brightness = dot(frag_color.rgb + emission.rgb, vec3(0.2126, 0.7152, 0.0722));
    bright_color = brightness >= 0.75 ? frag_color : vec4(0., 0., 0., g_texture.a);
}

float vsmFixLightBleed(float pMax, float amount)
{
    return clamp((pMax - amount) / (1.0 - amount), 0.0, 1.0);
}

float calcVSM(int idx, vec4 shadow_coord, float bias) {
    vec4 vsm = texture(sun_shadow_map[idx], shadow_coord.xy);

    float E_x2 = vsm.y;
    float Ex_2 = vsm.x * vsm.x;
    float var = max(E_x2 - Ex_2, bias);
    float mD = vsm.x - shadow_coord.z;
    float mD_2 = mD * mD;
    float p = var / (var + mD_2);

    return max(vsmFixLightBleed(p, 0.7), int(shadow_coord.z <= vsm.x));
}

float calculate_shadow_vsm(vec4 worldPosition, int idx, float bias) {
    vec4 shadowMapPos = cascade_shadow[idx].projection_view * worldPosition;
    vec4 shadow_coord = (shadowMapPos / shadowMapPos.w) * 0.5 + 0.5;
    float c0 = calcVSM(idx, shadow_coord, bias);
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
    vec4 lightFactors = vec4(vec3(sunColorR, sunColorG, sunColorB) * ambient, 1.0);

    vec3 sunPos = normalize(vec3(sunX, sunY, sunZ));

    float sun_shadow = calcSunShineVSM(m_vertex_pos, frag_pos);

    vec4 sunFactor = calc_sun_light(sunPos, frag_pos, normal);

    vec4 point_light_factor = vec4(0.0);
    for (int i = 0; i < total_plights; i++) {
        PointLight p = p_l[i];
        float p_brightness = p.brightness;
        float at_base = 1.0;
        float linear = 0.09 * p_brightness;
        float expo = 0.032 * p_brightness;
        float p_id = p.shadowMapId;
        vec4 shadow = p_id >= 0 ? vec4(calculate_point_light_shadow(point_light_cubemap[int(p_id)], m_vertex_pos.xyz, vec3(p.plPosX, p.plPosY, p.plPosZ))) : vec4(1.0);
        point_light_factor += calc_point_light(p, frag_pos, normal, at_base, linear, expo, p_brightness) * shadow;
    }

    float brightness = dot(point_light_factor.rgb, vec3(0.2126, 0.7152, 0.0722)) * 5.0;
    lightFactors += sunFactor * clamp(sun_shadow + brightness, 0.0, 1.0);

    lightFactors += point_light_factor;

    return lightFactors;
}

vec4 calc_light_factor(vec3 colors, float brightness, vec3 vPos, vec3 light_dir, vec3 vNormal) {
    vec4 diffuseC = vec4(0.);
    vec4 specularC = vec4(0.);

    float specularF = 0.;
    float diffuseF = max(dot(vNormal, light_dir), 0.);
    diffuseC = vec4(colors, 1.) * brightness * diffuseF;

    vec3 camDir = normalize(-vPos);
    vec3 from_light = light_dir;
    vec3 reflectionF = normalize(from_light + camDir);
    specularF = max(dot(vNormal, reflectionF), 0.);
    specularF = pow(specularF, 64.0);
    specularC = brightness * specularF * vec4(colors, 1.) * vec4(4.);

    vec4 specularFactor = checkCode(texturing_code, specular_code) ? vec4(vec3(1.0) - texture(specular_map, texture_coordinates).rgb, 1.0) : vec4(1.);
    return dot(vNormal, from_light) + 0.0001 >= 0 ? (diffuseC + (specularC * specularFactor)) : vec4(0.);
}

vec4 calc_sun_light(vec3 sunPos, vec3 vPos, vec3 vNormal) {
    return abs(dot(vNormal, sunPos)) < 0.001 ? vec4(0.0) : calc_light_factor(vec3(sunColorR, sunColorG, sunColorB), sunBright, vPos, normalize(sunPos), vNormal);
}

vec4 calc_point_light(PointLight light, vec3 vPos, vec3 vNormal, float at_base, float linear, float expo, float bright) {
    vec3 pos = (out_view_matrix * vec4(vec3(light.plPosX, light.plPosY, light.plPosZ), 1.0)).xyz;

    vec3 light_dir = pos - vPos;
    vec3 to_light = normalize(light_dir);
    vec4 light_c = calc_light_factor(vec3(light.plR, light.plG, light.plB), bright, vPos, to_light, vNormal);

    float dist = length(light_dir);
    float attenuation_factor = at_base + linear * dist + expo * pow(dist, 2);
    return light_c / attenuation_factor;
}

vec4 calc_fog(vec3 frag_pos, vec4 color) {
    vec3 fog_color = vec3(fogColorR, fogColorG, fogColorB);
    float distance = length(frag_pos);
    float fogFactor = 1. / exp((distance * fogDensity) * (distance * fogDensity));
    fogFactor = clamp(fogFactor, 0., 1.);

    vec3 result = mix(fog_color, color.xyz, fogFactor);
    return vec4(result.xyz, color.w);
}

float calc_fog_float(vec3 frag_pos, float f) {
    float distance = length(frag_pos);
    float fogFactor = 1. / exp((distance * fogDensity) * (distance * fogDensity));
    fogFactor = clamp(fogFactor, 0., 1.);
    return f * fogFactor;
}