#extension GL_ARB_bindless_texture : require

in mat3 TBN;
in vec2 uv_coordinates;
in vec3 modelview_vertex_normal;
in vec3 modelview_vertex_pos;
in vec4 model_vertex_pos;
in flat uint matertial_id;
in flat uint ent_id;

layout (location = 0) out vec3 gPosition;
layout (location = 1) out vec3 gNormal;
layout (location = 2) out vec4 gColor;
layout (location = 3) out vec3 gEmission;
layout (location = 4) out vec2 gMetallicRoughness;

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

vec3 calc_normal_map(int normalsMapId) {
    vec3 normal = texture(sampler2D(textures[normalsMapId]), uv_coordinates).rgb;
    normal = normalize(normal * 2.0 - 1.0);
    normal = normalize(TBN * normal);
    return normal;
}

bool checkCode(int i1, int i2) {
    int i3 = i1 & i2;
    return bool(i3 != 0);
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
  // if (diffuse.a < alpha_discard) {
  //     discard;
  // }
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

    gPosition = modelview_vertex_pos;
    gNormal = normals;
    gColor = diffuse;
    gEmission = emission;
    gMetallicRoughness = metallic_roughness;
}