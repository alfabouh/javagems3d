#extension GL_ARB_bindless_texture : require


in mat3 TBN;
in vec2 uv_coordinates;
in vec3 modelview_vertex_normal;
in vec3 modelview_vertex_pos;
in vec3 model_vertex_normal;
in vec4 model_vertex_pos;

layout (location = 0) out vec3 gPosition;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gColor;
layout (location = 3) out vec3 gEmission;
layout (location = 4) out vec2 gMetallicRoughness;
layout (location = 5) out float gDecal_layerID;

const int diffuse_code = CONST.DIFFUSE_CODE;
const int normals_code = CONST.NORMALS_CODE;
const int emission_code = CONST.EMISSION_CODE;
const int metallic_roughness_code = CONST.METALLIC_ROUGHNESS_CODE;

uniform float alpha_discard;
uniform vec4 diffuse_color;
uniform vec3 emissive_color;
uniform float metallic_factor;
uniform float roughness_factor;
uniform uvec2 diffuse_map;
uniform uvec2 normals_map;
uniform uvec2 emissive_map;
uniform uvec2 metallic_roughness_map;
uniform int texturing_code;
uniform uint decalLayerID;

bool checkCode(int i1, int i2) {
    int i3 = i1 & i2;
    return bool(i3 != 0);
}

vec3 calc_normal_map() {
    vec3 normal = texture(sampler2D(normals_map), uv_coordinates).rgb;
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
        diffuse *= texture(sampler2D(diffuse_map), uv_coordinates);
    }
    if (diffuse.a < alpha_discard) {
        discard;
    }
    if (useEmissionTexture) {
        emission *= texture(sampler2D(emissive_map), uv_coordinates).rgb;
    }
    if (useNormalsTexture) {
        normals = calc_normal_map();
    }
    if (useRoughnessMetallicTexture) {
        vec4 mr = texture(sampler2D(metallic_roughness_map), uv_coordinates);
        metallic_roughness *= vec2(mr.b, mr.g);
    }

    gPosition = modelview_vertex_pos;
    gNormal = normals;
    gColor = diffuse;
    gEmission = emission;
    gMetallicRoughness = vec2(metallic_roughness.x, 1. - metallic_roughness.y);
    gDecal_layerID = float(decalLayerID / 65535.);
}