#extension GL_ARB_bindless_texture : require

in vec2 uv_coordinates;
in vec3 model_vertex_normal;
in vec4 model_vertex_pos;
in vec3 modelview_vertex_normal;
in vec3 modelview_vertex_pos;
in mat3 TBN;
in mat4 out_view_matrix;

in flat uint matertial_id;
in flat uint ent_id;

const int light_opacity_code = 1 << 2;
const int light_bright_code = 1 << 3;
const int diffuse_code = 1 << 2;
const int emission_code = 1 << 3;
const int metallic_code = 1 << 4;
const int normals_code = 1 << 5;
const int specular_code = 1 << 6;

layout (early_fragment_tests) in;
layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 bright_color;

layout (std140, binding = 0) uniform SunLight {
    vec4 sunPos;
    vec4 sunColor;
    vec2 sunMeta;
};

layout (std140, binding = 3) uniform Fog {
    vec4 fogColor;
    float fogDensity;
};

uniform vec3 camera_pos;
uniform samplerCube ambient_cube_map;

vec4 calc_sun_light(vec3, vec3, vec3, vec4);
vec4 calc_light_factor(vec3, float, vec3, vec3, vec3, vec4);
vec4 calc_light(vec3, vec3, vec4);
vec4 calc_fog(vec3, vec4);
float calc_fog_float(vec3, float);

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

bool checkCode(int i1, int i2) {
    int i3 = i1 & i2;
    return bool(i3 != 0);
}

vec4 refract_cubemap(vec3 normal, float cnst) {
    float fogFactor = fogDensity * 100;
    float f = 1.0 - clamp(fogFactor, 0.0, 0.7);

    float ratio = 1.0 / cnst;
    vec3 I = normalize(model_vertex_pos.xyz - camera_pos);
    vec3 R = refract(I, normalize(normal), ratio);
    return f * vec4(texture(ambient_cube_map, R).rgb, 1.0);
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
    int lighting_code = property.lighting_code;

    vec3 frag_pos = modelview_vertex_pos;
    vec4 diffuse = checkCode(texturing_code, diffuse_code) ? texture(sampler2D(textures[mat.diffuse_map_id]), uv_coordinates) : mat.diffuse_color;
    if (diffuse.a < alpha_discard) {
        discard;
    }
    diffuse += vec4(1.0 - diffuse.a) * alpha_discard + vec4(1.0 - diffuse.a) * diffuse;
    vec3 normals = normalize(checkCode(texturing_code, normals_code) ? calc_normal_map(mat.normals_map_id) : modelview_vertex_normal);
    vec4 emission = checkCode(lighting_code, light_bright_code) ? vec4(1.0) : checkCode(texturing_code, emission_code) ? texture(sampler2D(textures[mat.emissive_map_id]), uv_coordinates) : vec4(vec3(0.0), 1.0);
    vec4 specular = checkCode(texturing_code, specular_code) ? texture(sampler2D(textures[mat.specular_map_id]), uv_coordinates) : vec4(vec3(0.0), 1.0);
    vec4 metallic = (checkCode(texturing_code, metallic_code) ? texture(sampler2D(textures[mat.metallic_map_id]), uv_coordinates) : vec4(0.)) * refract_cubemap(model_vertex_normal, 1.73);
    diffuse += vec4(metallic.xyz, 0.0);

    vec4 lights = calc_light(frag_pos, normals, specular);
    frag_color = diffuse * (lights + emission);
    frag_color = calc_fog(frag_pos.xyz, frag_color);
    frag_color.a = 1.0;

    float brightness = dot(frag_color.rgb + (emission.rgb), vec3(0.2126, 0.7152, 0.0722));
    bright_color = brightness >= 2.0 ? vec4(frag_color.xyz, 1.) : vec4(0., 0., 0., 1.);
}

vec4 calc_light(vec3 frag_pos, vec3 normal, vec4 specularFactor) {
    vec4 lightFactors = vec4(sunColor.xyz * sunMeta.x, 1.0);
    vec3 sunPos = normalize(sunPos.xyz);
    vec4 sunFactor = calc_sun_light(sunPos, frag_pos, normal, specularFactor);
    return lightFactors + sunFactor;
}

vec4 calc_light_factor(vec3 colors, float brightness, vec3 vPos, vec3 light_dir, vec3 vNormal, vec4 specularFactor) {
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

    return diffuseC + (specularC * specularFactor);
}

vec4 calc_sun_light(vec3 sunPos, vec3 vPos, vec3 vNormal, vec4 specularFactor) {
    return calc_light_factor(sunColor.xyz, sunMeta.y, vPos, normalize(sunPos), vNormal, specularFactor);
}

vec4 calc_fog(vec3 frag_pos, vec4 color) {
    if (fogDensity <= 0) {
        return color;
    }
    vec3 fog_color = fogColor.xyz;
    float distance = length(frag_pos);
    float fogFactor = 1. / exp((distance * fogDensity) * (distance * fogDensity));
    fogFactor = clamp(fogFactor, 0., 1.);

    vec3 result = mix(fog_color, color.xyz, fogFactor);
    return vec4(result.xyz, color.w);
}