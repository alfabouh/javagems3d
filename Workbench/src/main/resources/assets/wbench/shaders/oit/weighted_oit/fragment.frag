//in vec2 uv_coordinates;
//
//layout (location = 0) out vec4 accumulated;
//layout (location = 1) out float reveal;
//
//in vec2 uv_coordinates;
//
//uniform float alpha_factor;
//
//void main()
//{
//    //vec4 color = texture(diffuse_map, uv_coordinates);
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
in vec2 uv_coordinates;

in vec3 model_vertex_normal;
in vec4 model_vertex_pos;
in vec3 modelview_vertex_normal;
in vec3 modelview_vertex_pos;

in mat3 TBN;
in mat4 out_view_matrix;

layout (location = 0) out vec4 accumulated;
layout (location = 1) out float reveal;

struct Sun {
    vec3 position;
    float _padding0;
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

struct Fog {
    vec3 color;
    float density;
};
layout (std430, binding = 7) buffer WorldFog {
    Fog fog;
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

uniform samplerCube ambient_cube_map;
uniform vec4 diffuse_color;
uniform sampler2D diffuse_map;
uniform sampler2D normals_map;
uniform sampler2D emissive_map;
uniform sampler2D specular_map;
uniform sampler2D metallic_map;

uniform float alpha_factor;

#include "assets/wbench/shaders/libs/shadows"

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
    float fogFactor = fog.density * 100;
    float f = 1.0 - clamp(fogFactor, 0.0, 0.7);

    float ratio = 1.0 / cnst;
    vec3 I = normalize(model_vertex_pos.xyz - camera_pos);
    vec3 R = refract(I, normalize(normal), ratio);
    return f * vec4(texture(ambient_cube_map, R).rgb, 1.0);
}

vec3 calc_normal_map() {
    vec3 normal = texture(normals_map, uv_coordinates).rgb;
    normal = normalize(normal * 2.0 - 1.0);
    normal = normalize(TBN * normal);
    return normal;
}

void main()
{
    vec3 frag_pos = modelview_vertex_pos;
    vec3 normals = normalize(checkCode(texturing_code, normals_code) ? calc_normal_map() : modelview_vertex_normal);
    vec4 g_texture = checkCode(texturing_code, diffuse_code) ? texture(diffuse_map, uv_coordinates) : diffuse_color;
    vec4 emission = checkCode(lighting_code, light_bright_code) ? vec4(1.0) : checkCode(texturing_code, emission_code) ? texture(emissive_map, uv_coordinates) : vec4(vec3(0.0), 1.0);
    vec4 metallic = (checkCode(texturing_code, metallic_code) ? texture(metallic_map, uv_coordinates) : vec4(vec3(0.0), 1.0)) * refract_cubemap(model_vertex_normal, 1.73);

    vec4 lights = calc_light(frag_pos, normals);

    vec4 frag_color = (g_texture + vec4(metallic.xyz, 0.)) * (lights + emission);
    frag_color = calc_fog(frag_pos.xyz, frag_color);
    frag_color = vec4(frag_color.xyz, g_texture.a);

    float a_factor = alpha_factor * frag_color.a;

    float weight = max(min(1.0, max(max(frag_color.r, frag_color.g), frag_color.b) * a_factor), a_factor) * clamp(0.03 / (1.0e-5f + pow(gl_FragCoord.z / 200.0, 4.0)), 1.0e-2f, 3.0e+3f);

    accumulated = vec4(frag_color.rgb * a_factor, a_factor) * weight;

    reveal = a_factor;
    reveal = calc_fog_float(frag_pos.xyz, a_factor);
}

vec4 calc_light(vec3 frag_pos, vec3 normal) {
    vec4 lightFactors = vec4(sun.color * sun.ambient, 1.0);

    vec3 position = normalize(sun.position);

    float sun_shadow = calc_sun_shadows(model_vertex_pos, frag_pos);

    vec4 sunFactor = calc_sun_light(position, frag_pos, normal);

    vec4 point_light_factor = vec4(0.0);
    for (int i = 0; i < total_plights; i++) {
        PointLight p = p_l[i];
        float p_brightness = p.brightness;
        float at_base = 1.0;
        float linear = 0.1 * p_brightness;
        float expo = 0.032 / sqrt(p_brightness);
        float p_id = p.attachedShadowSceneId;
        vec4 shadow = p_id >= 0 ? vec4(calculate_point_light_shadows(point_light_cubemap[int(p_id)], model_vertex_pos.xyz, p.position.xyz)) : vec4(1.0);
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

    vec4 specularFactor = checkCode(texturing_code, specular_code) ? vec4(vec3(1.0) - texture(specular_map, uv_coordinates).rgb, 1.0) : vec4(1.);
    return diffuseC + (specularC * specularFactor);
}

vec4 calc_sun_light(vec3 position, vec3 vPos, vec3 vNormal) {
    return calc_light_factor(sun.color, sun.brightness, vPos, normalize(position), vNormal);
}

vec4 calc_point_light(PointLight light, vec3 vPos, vec3 vNormal, float at_base, float linear, float expo, float bright) {
    vec3 pos = light.view_position;

    vec3 light_dir = pos - vPos;
    vec3 to_light = normalize(light_dir);
    vec4 light_c = calc_light_factor(light.color, bright, vPos, to_light, vNormal);

    float dist = length(light_dir);
    float attenuation_factor = at_base + linear * dist + expo * pow(dist, 2);
    return light_c / attenuation_factor;
}

vec4 calc_fog(vec3 frag_pos, vec4 color) {
    if (fog.density <= 0) {
        return color;
    }
    vec3 fog_color = fog.color;
    float distance = length(frag_pos);
    float fogFactor = 1. / exp((distance * fog.density) * (distance * fog.density));
    fogFactor = clamp(fogFactor, 0., 1.);

    vec3 result = mix(fog_color, color.xyz, fogFactor);
    return vec4(result.xyz, color.w);
}

float calc_fog_float(vec3 frag_pos, float f) {
    if (fog.density <= 0) {
        return f;
    }
    float distance = length(frag_pos);
    float fogFactor = 1. / exp((distance * fog.density) * (distance * fog.density));
    fogFactor = clamp(fogFactor, 0., 1.);
    return f * fogFactor;
}