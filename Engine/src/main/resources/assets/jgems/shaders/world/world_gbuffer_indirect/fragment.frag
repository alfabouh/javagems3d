#extension GL_ARB_bindless_texture : require

in mat3 TBN;
in vec2 uv_coordinates;
in vec3 modelview_vertex_normal;
in vec3 modelview_vertex_pos;
in vec4 model_vertex_pos;
in vec3 model_vertex_normal;
in flat uint matertial_id;
in flat uint ent_id;

layout (early_fragment_tests) in;
layout (location = 0) out vec4 gPosition;
layout (location = 1) out vec4 gNormal;
layout (location = 2) out vec4 gColor;
layout (location = 3) out vec4 gEmission;
layout (location = 4) out vec4 gSpecular;

struct Properties {
    float alpha_discard;
    int lighting_code;
};

struct Material {
    vec4 diffuse_color;
    int diffuse_map_id;
    int normals_map_id;
    int emissive_map_id;
    int specular_map_id;
    int metallic_map_id;
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

const int diffuse_code = 1 << 2;
const int normals_code = 1 << 3;
const int emission_code = 1 << 4;
const int specular_code = 1 << 5;
const int metallic_code = 1 << 6;
const int light_bright_code = 1 << 2;

uniform vec3 camera_pos;
uniform samplerCube ambient_cube_map;

vec3 calc_normal_map(int normalsMapId) {
    vec3 normal = texture(sampler2D(textures[normalsMapId]), uv_coordinates).rgb;
    normal = normalize(normal * 2.0 - 1.0);
    normal = normalize(TBN * normal);
    return normal;
}

vec4 refract_cubemap(vec3 normal, float cnst) {
    float ratio = 1.0 / cnst;
    vec3 I = normalize(model_vertex_pos.xyz - camera_pos);
    vec3 R = refract(I, normalize(normal), ratio);
    return vec4(texture(ambient_cube_map, R).rgb, 1.0);
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
    int lighting_code = property.lighting_code;

    vec4 diffuse = checkCode(texturing_code, diffuse_code) ? texture(sampler2D(textures[mat.diffuse_map_id]), uv_coordinates) : mat.diffuse_color;
    if (diffuse.a < alpha_discard) {
        discard;
    }
    diffuse += vec4(1.0 - diffuse.a) * alpha_discard + vec4(1.0 - diffuse.a) * diffuse;

    vec3 normals = normalize(checkCode(texturing_code, normals_code) ? calc_normal_map(mat.normals_map_id) : modelview_vertex_normal);

    gNormal = vec4(normals, 1.0);
    gPosition = vec4(modelview_vertex_pos, 1.0);
    gColor = diffuse;
    gEmission = checkCode(lighting_code, light_bright_code) ? vec4(1.0) : checkCode(texturing_code, emission_code) ? texture(sampler2D(textures[mat.emissive_map_id]), uv_coordinates) : vec4(vec3(0.0), 1.0);
    gSpecular = checkCode(texturing_code, specular_code) ? texture(sampler2D(textures[mat.specular_map_id]), uv_coordinates) : vec4(vec3(0.0), 1.0);

    vec4 gMetallic = (checkCode(texturing_code, metallic_code) ? texture(sampler2D(textures[mat.metallic_map_id]), uv_coordinates) : vec4(0.)) * refract_cubemap(model_vertex_normal, 1.73);
    gColor += vec4(gMetallic.xyz, 0.0);
}