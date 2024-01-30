in vec2 texture_coordinates;
in vec3 mv_vertex_normal;
in vec3 mv_vertex_pos;
in mat3 TBN;
in vec4 light_frag_pos;
in vec3 out_view_position;
in vec4 out_world_position;

layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 bright_color;

uniform vec3 camera_pos;
uniform sampler2D texture_sampler;

uniform int texturing_code;
uniform int lighting_code;

const int light_opacity_code = 1 << 2;


const int diffuse_code = 1 << 2;
const int emissive_code = 1 << 3;
const int metallic_code = 1 << 4;
const int normals_code = 1 << 5;
const int specular_code = 1 << 6;
uniform int show_cascades;

uniform samplerCube ambient_cubemap;
uniform vec2 texture_scaling;
uniform vec4 diffuse_color;
uniform sampler2D diffuse_map;
uniform sampler2D normals_map;
uniform sampler2D emissive_map;
uniform sampler2D specular_map;
uniform sampler2D metallic_map;

struct CascadeShadow {
    float split_distance;
    mat4 projection_view;
};

uniform CascadeShadow cascade_shadow[3];
uniform sampler2D shadow_map0;
uniform sampler2D shadow_map1;
uniform sampler2D shadow_map2;

struct PointLight
{
    float plPosX;
    float plPosY;
    float plPosZ;
    float plR;
    float plG;
    float plB;
    float brightness;
};

layout (std140, binding = 0) uniform SunLight {
    float ambient;
    float sunBright;
    float sunX;
    float sunY;
    float sunZ;
};

layout (std140, binding = 1) uniform PointLights {
    PointLight p_l[1024];
};

layout (std140, binding = 2) uniform Misc {
    float w_tick;
};

vec4 calc_sun_light(vec3, vec3, vec3);
vec4 calc_point_light(PointLight, vec3, vec3, float, float, float, float);
vec4 calc_light_factor(vec3, float, vec3, vec3, vec3);
vec4 calc_light();

vec2 scaled_coordinates() {
    return texture_coordinates * texture_scaling;
}

vec4 refract_cubemap(vec3 normal, float cnst) {
    float ratio = 1.0 / cnst;
    vec3 I = normalize(mv_vertex_pos - camera_pos);
    vec3 R = refract(I, normalize(normal), ratio);
    return vec4(texture(ambient_cubemap, R).rgb, 1.0);
}

vec3 calc_normal_map() {
    vec3 normal = texture2D(normals_map, scaled_coordinates()).rgb;
    normal = normalize(normal * 2.0 - 1.0);
    normal = normalize(TBN * normal);
    return normal;
}

float per_cascade_bias_shadow[3] = float[](5.0e-6f, 7.5e-6f, 1.0e-6f);
float per_cascade_linear_shadow[3] = float[](0.5, 0.575, 0.65);

void main()
{
    vec4 diffuse_texture = texture(diffuse_map, scaled_coordinates());
    vec4 emissive_texture = texture(emissive_map, scaled_coordinates());
    vec4 diffuse = (texturing_code & diffuse_code) != 0 ? diffuse_texture : diffuse_color;

    vec4 lightFactor = (lighting_code & light_opacity_code) == 0 ? vec4(1.) : ((texturing_code & emissive_code) != 0 ? emissive_texture * vec4(4.) : calc_light());

    diffuse += (texturing_code & metallic_code) != 0 ? (refract_cubemap(mv_vertex_normal, 1.73) * texture2D(metallic_map, texture_coordinates)) : vec4(0.0);

    vec4 final = diffuse * lightFactor;
    frag_color = final;

    vec3 cascmask = vec3(1.0);
    int cascadeIndex = int(out_view_position.z < cascade_shadow[0].split_distance) + int(out_view_position.z < cascade_shadow[1].split_distance);
    switch (cascadeIndex) {
        case 0:
            cascmask = vec3(1.0f, 0.75f, 0.75f);
            break;
        case 1:
            cascmask = vec3(0.75f, 1.0f, 0.75f);
            break;
        case 2:
            cascmask = vec3(0.75f, 0.75f, 1.0f);
            break;
        default:
            cascmask = vec3(1.0f, 1.0f, 0.75f);
            break;
    }

    frag_color.rgb *= (show_cascades == 1) ? cascmask : vec3(1.0);

    float brightness = frag_color.r + frag_color.g + frag_color.b;
    float distance_to_tx = length(mv_vertex_pos);

    brightness *= distance_to_tx <= 64. ? 1. : 0.;

    bright_color = brightness >= 8. ? frag_color : vec4(0., 0., 0., 1.);
}

float calcVSM(sampler2D samp, vec4 shadow_coord, vec2 offset, float bias, float linear) {
    vec2 moments = texture2D(samp, shadow_coord.xy + offset).rg;

    float variance = moments.y - (moments.x * moments.x);

    variance = max(variance, bias);
    float d = shadow_coord.z - moments.x;
    float shadowPCT = smoothstep(linear, 1.0, variance / (variance + d * d));

    return shadow_coord.z <= moments.x ? 1.0 : shadowPCT;
}

float calculate_shadow_pcf(vec4 worldPosition, int idx, float bias, float linear) {
    vec4 shadowMapPos = cascade_shadow[idx].projection_view * worldPosition;
    vec4 shadow_coord = (shadowMapPos / shadowMapPos.w) * 0.5 + 0.5;

    float shadow = 0.0;
    vec2 texelSize = 1.0 / textureSize(idx == 0 ? shadow_map0 : idx == 1 ? shadow_map1 : shadow_map2, 0);

    for (int x = -1; x <= 1; x++) {
        for (int y = -1; y <= 1; y++) {
            shadow += calcVSM(idx == 0 ? shadow_map0 : idx == 1 ? shadow_map1 : shadow_map2, shadow_coord, vec2(x, y) * texelSize, bias, linear);
        }
    }

    return shadow / 9.0;
}

float calculate_shadow_no_pcf(vec4 worldPosition, int idx, float bias, float linear) {
    vec4 shadowMapPos = cascade_shadow[idx].projection_view * worldPosition;
    vec4 shadow_coord = (shadowMapPos / shadowMapPos.w) * 0.5 + 0.5;
    return calcVSM(idx == 0 ? shadow_map0 : idx == 1 ? shadow_map1 : shadow_map2, shadow_coord, vec2(0.0), bias, linear);
}

vec4 calc_light() {
    vec4 lightFactors = vec4(0.);
    vec3 normal = normalize(mv_vertex_normal);
    vec3 sunPos = normalize(vec3(sunX, sunY, sunZ));
    int cascadeIndex = int(out_view_position.z < cascade_shadow[0].split_distance) + int(out_view_position.z < cascade_shadow[1].split_distance);
    float bias = per_cascade_bias_shadow[cascadeIndex];
    float linear = per_cascade_linear_shadow[cascadeIndex];
    float sun_shadow = (cascadeIndex != 2 ? calculate_shadow_pcf(out_world_position, cascadeIndex, bias, linear) : calculate_shadow_no_pcf(out_world_position, cascadeIndex, bias, linear));

    vec4 calcSunFactor = abs(dot(normal, sunPos)) < 0.001 ? vec4(0.0) : calc_sun_light(sunPos, mv_vertex_pos, normal);

    int i = 0;
    vec4 point_light_factor = vec4(0.0);
    while (p_l[i].brightness > 0) {
        PointLight p = p_l[i++];
        float bright = p.brightness;
        float at_base = 1.8 / (bright * 0.5);
        float linear = 2.25 / (bright * 2.75);
        float expo = 0.6 / (bright * 0.25f);
        point_light_factor += calc_point_light(p, mv_vertex_pos, normal, at_base, linear, expo, bright);
    }

    float prgb = point_light_factor.r + point_light_factor.g + point_light_factor.b;
    lightFactors += calcSunFactor * clamp(sun_shadow + prgb, 0.0, 1.0);
    lightFactors += point_light_factor;
    lightFactors += ambient;
    return lightFactors;
}

vec4 calc_light_factor(vec3 colors, float brightness, vec3 vPos, vec3 light_dir, vec3 vNormal) {
    vec3 new_normal = (texturing_code & normals_code) != 0 ? calc_normal_map() : vNormal;
    vec4 diffuseC = vec4(0.);
    vec4 specularC = vec4(0.);

    float specularF = 0.;
    float diffuseF = max(dot(new_normal, light_dir), 0.);
    diffuseC = vec4(colors, 1.) * brightness * diffuseF;

    vec3 camDir = normalize(-vPos);
    vec3 from_light = light_dir;
    vec3 reflectionF = normalize(from_light + camDir);
    specularF = max(dot(new_normal, reflectionF), 0.);
    specularF = pow(specularF, 12.0);
    specularC = brightness * specularF * vec4(colors, 1.);

    vec4 specularMap = (texturing_code & specular_code) != 0 ? texture(specular_map, texture_coordinates) : vec4(1.);

    return dot(vNormal, from_light) + 0.0001 >= 0 ? (diffuseC + (specularC * specularMap)) : vec4(0.);
}

vec4 calc_sun_light(vec3 sunPos, vec3 vPos, vec3 vNormal) {
    return calc_light_factor(vec3(1., 0.97, 0.94), sunBright, vPos, normalize(sunPos), vNormal);
}

vec4 calc_point_light(PointLight light, vec3 vPos, vec3 vNormal, float at_base, float linear, float expo, float bright) {
    vec3 pos = vec3(light.plPosX, light.plPosY, light.plPosZ);

    vec3 light_dir = pos - vPos;
    vec3 to_light = normalize(light_dir);
    vec4 light_c = calc_light_factor(vec3(light.plR, light.plG, light.plB), bright, vPos, to_light, vNormal);

    float dist = length(light_dir);
    float attenuation_factor = at_base + linear * dist + expo * pow(dist, 2);
    return light_c / attenuation_factor;
}