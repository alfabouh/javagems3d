in vec2 texture_coordinates;

in vec3 m_vertex_normal;
in vec4 m_vertex_pos;
in vec3 mv_vertex_normal;
in vec3 mv_vertex_pos;

in mat3 TBN;
in mat4 out_view_matrix;

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

uniform int texturing_code;
uniform int lighting_code;

const int light_opacity_code = 1 << 2;
const int light_bright_code = 1 << 3;

const int diffuse_code = 1 << 2;
const int emission_code = 1 << 3;
const int metallic_code = 1 << 4;
const int normals_code = 1 << 5;
const int specular_code = 1 << 6;

uniform samplerCube ambient_cubemap;
uniform vec4 diffuse_color;
uniform sampler2D diffuse_map;
uniform sampler2D normals_map;
uniform sampler2D emissive_map;
uniform sampler2D specular_map;
uniform sampler2D metallic_map;

uniform float alpha_discard;

vec4 calc_sun_light(vec3, vec3, vec3);
vec4 calc_light_factor(vec3, float, vec3, vec3, vec3);
vec4 calc_light(vec3, vec3);
vec4 calc_fog(vec3, vec4);
float calc_fog_float(vec3, float);

bool checkCode(int i1, int i2) {
    int i3 = i1 & i2;
    return bool(i3 != 0);
}

vec4 refract_cubemap(vec3 normal, float cnst) {
    float fogFactor = fogDensity * 100;
    float f = 1.0 - clamp(fogFactor, 0.0, 0.7);

    float ratio = 1.0 / cnst;
    vec3 I = normalize(m_vertex_pos.xyz - camera_pos);
    vec3 R = refract(I, normalize(normal), ratio);
    return f * vec4(texture(ambient_cubemap, R).rgb, 1.0);
}

vec3 calc_normal_map() {
    vec3 normal = texture(normals_map, texture_coordinates).rgb;
    normal = normalize(normal * 2.0 - 1.0);
    normal = normalize(TBN * normal);
    return normal;
}

void main()
{
    vec3 frag_pos = mv_vertex_pos;
    vec3 normals = normalize(checkCode(texturing_code, normals_code) ? calc_normal_map() : mv_vertex_normal);
    vec4 g_texture = checkCode(texturing_code, diffuse_code) ? texture(diffuse_map, texture_coordinates) : diffuse_color;
    vec4 emission = checkCode(lighting_code, light_bright_code) ? vec4(1.0) : checkCode(texturing_code, emission_code) ? texture(emissive_map, texture_coordinates) : vec4(vec3(0.0), 1.0);
    vec4 metallic = (checkCode(texturing_code, metallic_code) ? texture(metallic_map, texture_coordinates) : vec4(vec3(0.0), 1.0)) * refract_cubemap(m_vertex_normal, 1.73);

    vec4 lights = calc_light(frag_pos, normals);

    if (g_texture.a < alpha_discard) {
        discard;
    }

    frag_color = (g_texture + vec4(metallic.xyz, 0.)) * (lights + emission);
    frag_color = calc_fog(frag_pos.xyz, frag_color);

    bright_color = vec4(0.);
}

vec4 calc_light(vec3 frag_pos, vec3 normal) {
    vec4 lightFactors = vec4(sunColor.xyz * sunMeta.x, 1.0);
    vec3 sunPos = normalize(sunPos.xyz);
    vec4 sunFactor = calc_sun_light(sunPos, frag_pos, normal);
    return lightFactors + sunFactor;
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

    vec4 specularFactor = checkCode(texturing_code, specular_code) ? vec4(vec3(1.0) - texture(specular_map, texture_coordinates).rgb, 1.0) : vec4(1.);
    return diffuseC + (specularC * specularFactor);
}

vec4 calc_sun_light(vec3 sunPos, vec3 vPos, vec3 vNormal) {
    return calc_light_factor(sunColor.xyz, sunMeta.y, vPos, normalize(sunPos), vNormal);
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