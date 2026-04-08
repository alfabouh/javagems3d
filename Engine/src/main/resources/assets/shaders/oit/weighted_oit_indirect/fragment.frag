#extension GL_ARB_bindless_texture : require
precision highp float;
precision highp int;

in mat3 TBN;
in vec2 uv_coordinates;
in vec3 modelview_vertex_normal;
in vec3 modelview_vertex_pos;
in vec4 model_vertex_pos;
in vec3 model_vertex_normal;
in flat uint matertial_id;
in flat uint ent_id;

layout (location = 0) out vec4 accumulated;
layout (location = 1) out float reveal;
layout (location = 2) out vec4 bright_color;

struct Fog {
    vec3 color;
    float density;
};
layout (std430, binding = 7) buffer WorldFog {
    Fog fog;
};

uniform vec3 camera_pos;
uniform uvec2 ambient_cubemap;
uniform bool useCubeMap;

struct Properties {
    float alpha_discard;
};

struct Material {
    vec4 diffuse_color;
    vec3 emission_color;
    float _padding000; //PADDING
    float metallic_factor;
    float roughness_factor;
    int diffuse_map_id;
    int normals_map_id;
    int emission_map_id;
    int metallic_roughness_map_id;
    int texturing_code;
};

layout(std430, binding = 2) buffer BindlessTextures {
    uvec2 textures[CONST.MAX_BINDLESS_TEXTURES];
};

layout(std430, binding = 3) buffer MaterialsData {
    Material materials[256];
};

layout(std430, binding = 4) buffer RenderPropertiesData {
    Properties properties[512];
};

const int diffuse_code = CONST.DIFFUSE_CODE;
const int normals_code = CONST.NORMALS_CODE;
const int emission_code = CONST.EMISSION_CODE;
const int metallic_roughness_code = CONST.METALLIC_ROUGHNESS_CODE;

#include "/assets/shaders/libs/shadows"
#include "/assets/shaders/libs/lighting"

vec3 calc_light(vec3 frag_pos, vec3 normal, float specularFactor, vec4 world_position) {
    vec3 lightFactors = vec3(sun.color) * sun.ambient;
    vec3 position = normalize(sun.position);

    float sun_shadow = calc_sun_shadows(world_position, frag_pos);
    vec3 sunFactor = calc_sun_light(position, frag_pos, normal, specularFactor);

    vec3 point_light_factor = vec3(0.0);
    for (int i = 0; i < total_plights; i++) {
        PointLight p = p_l[i];
        float p_brightness = p.brightness;
        vec3 params = getParams(p_brightness);
        float p_id = p.attachedShadowSceneId;
        float shadow = p_id >= 0 ? calculate_point_light_shadows(point_light_cubemap[int(p_id)], world_position.xyz, p.position.xyz) : 1.;
        point_light_factor += calc_point_light(p, frag_pos, normal, params.x, params.y, params.z, p_brightness, specularFactor) * shadow;
    }

    float brightness = dot(point_light_factor.rgb, vec3(0.2126, 0.7152, 0.0722)) * 5.0;
    lightFactors += sunFactor * clamp(sun_shadow + brightness, 0.0, 1.0);
    lightFactors += point_light_factor;

    return lightFactors;
}

float calc_fog_float(vec3 frag_pos, float f) {
    float distance = length(frag_pos);
    float fogFactor = 1. / exp((distance * fog.density) * (distance * fog.density));
    fogFactor = clamp(fogFactor, 0., 1.);
    return f * fogFactor;
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

vec3 refract_cubemap(vec3 normal, float cnst, vec4 world_position) {
    float ratio = 1.0 / cnst;
    vec3 I = normalize(world_position.xyz - camera_pos);
    vec3 R = refract(I, normalize(normal), ratio);
    return texture(samplerCube(ambient_cubemap), R).rgb;
}

bool checkCode(int i1, int i2) {
    int i3 = i1 & i2;
    return bool(i3 != 0);
}

vec3 calc_normal_map(int normalsMapId) {
    vec3 normal = texture(sampler2D(textures[normalsMapId]), uv_coordinates).rgb;
    normal = normalize(normal * 2.0 - 1.0);
    normal = normalize(TBN * normal);
    return normal;
}

void main()
{
    Material mat = materials[matertial_id];
    Properties property = properties[ent_id];
    float alpha_discard = property.alpha_discard;
    int texturing_code = mat.texturing_code;

    bool useDiffuseTexture = checkCode(texturing_code, diffuse_code);
    bool useEmissionTexture = checkCode(texturing_code, emission_code);
    bool useNormalsTexture = checkCode(texturing_code, normals_code);
    bool useRoughnessMetallicTexture = checkCode(texturing_code, metallic_roughness_code);

    vec4 diffuse = vec4(mat.diffuse_color);
    vec3 normals = vec3(modelview_vertex_normal);
    vec3 emission = vec3(mat.emission_color);
    vec2 metallic_roughness = vec2(mat.metallic_factor, mat.roughness_factor);

    if (useDiffuseTexture) {
        diffuse *= texture(sampler2D(textures[mat.diffuse_map_id]), uv_coordinates);
    }
    if (useEmissionTexture) {
        emission *= texture(sampler2D(textures[mat.emission_map_id]), uv_coordinates).rgb;
    }
    if (useNormalsTexture) {
        normals = calc_normal_map(mat.normals_map_id);
    }
    if (useRoughnessMetallicTexture) {
        vec4 mr = texture(sampler2D(textures[mat.metallic_roughness_map_id]), uv_coordinates);
        metallic_roughness *= vec2(mr.b, mr.g);
    }

    vec3 gPosition = modelview_vertex_pos;
    vec3 gNormal = normals;
    vec4 gColor = diffuse;
    vec3 gEmission = emission;
    vec2 gMetallicRoughness = metallic_roughness;

    if (useCubeMap) {
        vec3 refracted_color = refract_cubemap(model_vertex_normal, 1.73, model_vertex_pos);
        gColor.rgb = mix(gColor.rgb, refracted_color, metallic_roughness.r * 0.5);
    }

    vec3 lights = calc_light(gPosition, gNormal, gMetallicRoughness.g, model_vertex_pos);
    vec4 frag_color = gColor * vec4(lights + gEmission, 1.0);
    frag_color = calc_fog(gPosition, frag_color);

    float weight = max(min(1.0, max(max(frag_color.r, frag_color.g), frag_color.b) * frag_color.a), frag_color.a) * clamp(0.03 / (1.0e-5f + pow(gl_FragCoord.z / 200.0, 4.0)), 1.0e-2f, 3.0e+3f);
    accumulated = vec4(frag_color.rgb * frag_color.a, frag_color.a) * weight;
    reveal = frag_color.a;
    reveal = calc_fog_float(gPosition, frag_color.a);

    float brightness = dot(frag_color.rgb + (gEmission), vec3(0.2126, 0.7152, 0.0722));
    bright_color = brightness >= 2.0 ? vec4(frag_color.xyz, 1.) : vec4(0., 0., 0., 1.);
}