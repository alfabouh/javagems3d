#extension GL_ARB_bindless_texture : require


in vec2 uv_coordinates;
in vec3 modelview_vertex_normal;
in vec3 modelview_vertex_pos;
in vec4 model_vertex_pos;
in vec3 model_vertex_normal;
in mat3 TBN;

layout (location = 0) out vec4 accumulated;
layout (location = 1) out float reveal;
layout (location = 2) out vec4 bright_color;

layout (std430, binding = 0) readonly restrict buffer Timer {
    float w_tick;
};

uniform vec3 camera_pos;

const int diffuse_code = CONST.DIFFUSE_CODE;
const int normals_code = CONST.NORMALS_CODE;
const int emission_code = CONST.EMISSION_CODE;
const int metallic_roughness_code = CONST.METALLIC_ROUGHNESS_CODE;

uniform float opacity;
uniform vec4 diffuse_color;
uniform vec3 emissive_color;
uniform float metallic_factor;
uniform float roughness_factor;
uniform uvec2 diffuse_map;
uniform uvec2 normals_map;
uniform uvec2 emissive_map;
uniform uvec2 metallic_roughness_map;
uniform int texturing_code;
uniform vec2 texture_scaling;

#include "/assets/shaders/libs/shadows"
#include "/assets/shaders/libs/lighting"
#include "/assets/shaders/libs/fog"
#include "/assets/shaders/libs/oit"
#include "/assets/shaders/libs/cubemap_reflections"
#include "/assets/shaders/libs/light_fragment_calc"

vec2 getScaledTexture(float speed) {
    float wave1 = sin(uv_coordinates.x * 20.0 + (w_tick * speed)) * 0.01;
    float wave2 = cos(uv_coordinates.y * 25.0 + (w_tick * speed) * 0.5) * 0.01;
    vec2 sincosFactor = vec2(wave1, wave2);
    return (uv_coordinates) * (texture_scaling) + sincosFactor + (vec2(w_tick) * 2.5e-2);
}

bool checkCode(int i1, int i2) {
    int i3 = i1 & i2;
    return bool(i3 != 0);
}

vec3 calc_normal_map() {
    vec3 normal = texture(sampler2D(normals_map), getScaledTexture(4.5)).rgb;
    normal = normalize(normal * 2.0 - 1.0);
    normal = normalize(TBN * normal);
    return normal;
}

void main()
{
    bool useDiffuseTexture = checkCode(texturing_code, diffuse_code);
    bool useEmissionTexture = checkCode(texturing_code, emission_code);
    bool useNormalsTexture = checkCode(texturing_code, normals_code);
    bool useRoughnessMetallicTexture = checkCode(texturing_code, metallic_roughness_code);

    vec4 diffuse = vec4(diffuse_color);
    vec3 normals = vec3(modelview_vertex_normal);
    vec3 emission = vec3(emissive_color);
    vec2 metallic_roughness = vec2(metallic_factor, roughness_factor);

    if (useDiffuseTexture) {
        diffuse *= texture(sampler2D(diffuse_map), getScaledTexture(4.5));
    }
    if (useEmissionTexture) {
        emission *= texture(sampler2D(emissive_map), getScaledTexture(4.5)).rgb;
    }
    if (useNormalsTexture) {
        normals = mix(normals, calc_normal_map(), 0.25);
    }
    if (useRoughnessMetallicTexture) {
        vec4 mr = texture(sampler2D(metallic_roughness_map), getScaledTexture(4.5));
        metallic_roughness *= vec2(mr.b, mr.g);
    }

    vec3 gPosition = modelview_vertex_pos;
    vec3 gNormal = normals;
    vec4 gColor = diffuse;
    vec3 gEmission = emission;
    vec2 gMetallicRoughness = vec2(0.75 + metallic_roughness.x * 0.25, 1. - metallic_roughness.y);

    if (useCubeMap) {
        vec3 refracted_color = refract_cubemap(-normals * vec3(-1), 1.25, model_vertex_pos);
        gColor.rgb = mix(gColor.rgb, refracted_color, gMetallicRoughness.r * 0.625);
    }

    vec3 lights = calc_light(gPosition, gNormal, gMetallicRoughness.g, model_vertex_pos);
    vec4 frag_color = gColor * vec4(lights + gEmission, 1.0);
    frag_color = calc_fog(gPosition, frag_color, 1.);
    frag_color.a *= opacity;

    accumulated = calc_accumulated(frag_color);
    reveal = calc_fog_float(gPosition, frag_color.a);

    float brightness = dot(frag_color.rgb + (gEmission), vec3(0.2126, 0.7152, 0.0722));
    bright_color = brightness >= 2.0 ? vec4(frag_color.xyz, 1.) : vec4(0., 0., 0., 1.);
}