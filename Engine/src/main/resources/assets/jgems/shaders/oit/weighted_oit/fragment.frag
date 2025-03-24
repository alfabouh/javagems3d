#extension GL_ARB_bindless_texture : require

in vec2 uv_coordinates;
in mat4 out_view_matrix;
in mat4 out_inversed_view_matrix;

layout (location = 0) out vec4 accumulated;
layout (location = 1) out float reveal;
layout (location = 2) out vec4 bright_color;

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

struct Fog {
    vec3 color;
    float density;
};
layout (std430, binding = 7) buffer WorldFog {
    Fog fog;
};

uniform vec3 camera_pos;
uniform uvec2 ambient_cube_bindless;
uniform bool useCubeMap;
uniform bool isSsaoValid;
uniform uvec2 gPositions;
uniform uvec2 gNormals;
uniform uvec2 gTexture;
uniform uvec2 gEmission;
uniform uvec2 gMetallicRoughness;
uniform uvec2 ssaoSampler;

#include "assets/jgems/shaders/libs/shadows"

vec4 calc_light(vec3 frag_pos, vec3 normal, float specularFactor, vec4 world_position) {
    vec4 lightFactors = vec4(sun.color * sun.ambient, 1.0);
    vec3 position = normalize(sun.position);

    float sun_shadow = calc_sun_shadows(world_position, frag_pos);
    vec4 sunFactor = calc_sun_light(position, frag_pos, normal);

    vec4 point_light_factor = vec4(0.0);
    for (int i = 0; i < total_plights; i++) {
        PointLight p = p_l[i];
        float p_brightness = p.brightness;
        float at_base = 1.0;
        float linear = 0.1 * p_brightness;
        float expo = 0.032 / sqrt(p_brightness);
        float p_id = p.attachedShadowSceneId;
        vec4 shadow = p_id >= 0 ? vec4(calculate_point_light_shadows(point_light_cubemap[int(p_id)], world_position.xyz, p.position.xyz)) : vec4(1.0);
        point_light_factor += calc_point_light(p, frag_pos, normal, at_base, linear, expo, p_brightness) * 1.;
    }

    float brightness = dot(point_light_factor.rgb, vec3(0.2126, 0.7152, 0.0722)) * 5.0;
    lightFactors += sunFactor * clamp(sun_shadow + brightness, 0.0, 1.0);
    lightFactors += point_light_factor;

    return lightFactors;
}

vec4 calc_light_factor(vec3 colors, float brightness, vec3 vPos, vec3 light_dir, vec3 vNormal, float specularFactor) {
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

    return diffuseC + (specularC * vec4(specularFactor, specularFactor, specularFactor, 1.));
}

vec4 calc_sun_light(vec3 position, vec3 vPos, vec3 vNormal, float specularFactor) {
    return calc_light_factor(sun.color, sun.brightness, vPos, normalize(position), vNormal, specularFactor);
}

vec4 calc_point_light(PointLight light, vec3 vPos, vec3 vNormal, float at_base, float linear, float expo, float bright, float specularFactor) {
    vec3 pos = light.view_position;

    vec3 light_dir = pos - vPos;
    vec3 to_light = normalize(light_dir);
    vec4 light_c = calc_light_factor(light.color, bright, vPos, to_light, vNormal, specularFactor);

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

vec4 refract_cubemap(vec3 normal, float cnst, vec4 world_position) {
    float ratio = 1.0 / cnst;
    vec3 I = normalize(world_position.xyz - camera_pos);
    vec3 R = refract(I, normalize(normal), ratio);
    return vec4(texture(ambient_cube_bindless, R).rgb, 1.0);
}

void main()
{
    vec3 frag_pos = texture(sampler2D(gPositions), uv_coordinates).xyz;
    vec3 normals = texture(sampler2D(gNormals), uv_coordinates).xyz;
    vec4 g_texture = texture(sampler2D(gTexture), uv_coordinates);
    vec3 emission = texture(sampler2D(gEmission), uv_coordinates).rgb * vec3(5.);
    vec2 metallic_roughness = texture(sampler2D(gMetallicRoughness), uv_coordinates).rg;

    vec4 view_pos = vec4(frag_pos, 1.0);
    vec4 world_position = out_inversed_view_matrix * view_pos;
    world_position /= world_position.w;

    if (useCubeMap) {
        g_texture *= (refract_cubemap(normals, 1.73, world_position) * metallic_roughness.r);
    }

    float f1 = 1.0;
    if (isSsaoValid) {
        float gray = dot(g_texture.rgb, vec3(0.299, 0.587, 0.114));
        float AO = texture(ssaoSampler, uv_coordinates).r;
        float f1 = pow(AO, (1.0 - gray) * 3.);
    }

    vec4 lights = calc_light(frag_pos, normals, metallic_roughness.g) * vec4(f1);
    vec4 frag_color = g_texture * (lights + emission);
    frag_color = calc_fog(frag_pos.xyz, frag_color);

    float weight = max(min(1.0, max(max(frag_color.r, frag_color.g), frag_color.b) * frag_color.a), frag_color.a) * clamp(0.03 / (1.0e-5f + pow(gl_FragCoord.z / 200.0, 4.0)), 1.0e-2f, 3.0e+3f);
    accumulated = vec4(frag_color.rgb * frag_color.a, frag_color.a) * weight;
    reveal = frag_color.a;
    reveal = calc_fog_float(frag_pos.xyz, frag_color.a);

    float brightness = dot(frag_color.rgb + (emission.rgb), vec3(0.2126, 0.7152, 0.0722));
    bright_color = brightness >= 2.0 ? vec4(frag_color.xyz, 1.) : vec4(0., 0., 0., 1.);
}