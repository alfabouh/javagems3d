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

uniform vec3 camera_pos;

struct Properties {
    float alpha_discard;
};

struct Material {
    vec4 diffuse_color;
    vec3 emissive_color;
    float _padding000; //PADDING

    float metallic_factor;
    float roughness_factor;
    int diffuse_map_id;
    float _padding001; //PADDING2

    int normals_map_id;
    int emissive_map_id;
    int metallic_roughness_map_id;
    int texturing_code;
};

layout(std430, binding = 2) readonly restrict buffer BindlessTextures {
    uvec2 textures[CONST.MAX_BINDLESS_TEXTURES];
};

layout(std430, binding = 3) readonly restrict buffer MaterialsData {
    Material materials[CONST.MAX_INDIRECT_RENDERING_MATERIALS];
};

layout(std430, binding = 4) readonly restrict buffer RenderPropertiesData {
    Properties properties[CONST.MAX_INDIRECT_RENDERING_PROPERIES];
};

const int diffuse_code = CONST.DIFFUSE_CODE;
const int normals_code = CONST.NORMALS_CODE;
const int emission_code = CONST.EMISSION_CODE;
const int metallic_roughness_code = CONST.METALLIC_ROUGHNESS_CODE;

#include "/assets/shaders/libs/shadows"
#include "/assets/shaders/libs/lighting"
#include "/assets/shaders/libs/fog"
#include "/assets/shaders/libs/oit"
#include "/assets/shaders/libs/cubemap_reflections"
#include "/assets/shaders/libs/light_fragment_calc"

bool checkCode(int i1, int i2) {
    return bool((i1 & i2) != 0);
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
    vec3 emission = vec3(mat.emissive_color);
    vec2 metallic_roughness = vec2(mat.metallic_factor, mat.roughness_factor);

    if (useDiffuseTexture) {
        diffuse *= texture(sampler2D(textures[mat.diffuse_map_id]), uv_coordinates);
    }
    if (useEmissionTexture) {
        emission *= texture(sampler2D(textures[mat.emissive_map_id]), uv_coordinates).rgb;
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
    vec2 gMetallicRoughness = vec2(0.5 + metallic_roughness.x * 0.5, 1. - metallic_roughness.y);

    if (useCubeMap) {
        vec3 refracted_color = refract_cubemap(-model_vertex_normal, 1.73, model_vertex_pos);
        gColor.rgb = mix(gColor.rgb, refracted_color, gMetallicRoughness.x * 0.5);
    }

    vec3 lights = calc_light(gPosition, gNormal, gMetallicRoughness.g, model_vertex_pos);
    vec4 frag_color = gColor * vec4(lights + gEmission, 1.0);
    frag_color = calc_fog(gPosition, frag_color, 1.);

    accumulated = calc_accumulated(frag_color);
    reveal = calc_fog_float(gPosition, frag_color.a);

    float brightness = dot(frag_color.rgb + (gEmission), vec3(0.2126, 0.7152, 0.0722));
    bright_color = brightness >= 2.0 ? vec4(frag_color.xyz, 1.) : vec4(0., 0., 0., 1.);
}