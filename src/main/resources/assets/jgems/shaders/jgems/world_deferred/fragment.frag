in vec2 out_texture;
in mat4 out_view_matrix;
in mat4 out_inversed_view_matrix;

layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 bright_color;

struct CascadeShadow {
    float split_distance;
    mat4 projection_view;
};

uniform CascadeShadow cascade_shadow[3];
uniform samplerCube point_light_cubemap[3];
uniform sampler2D shadow_map0;
uniform sampler2D shadow_map1;
uniform sampler2D shadow_map2;
uniform float far_plane;

struct PointLight
{
    float plPosX;
    float plPosY;
    float plPosZ;
    float plR;
    float plG;
    float plB;
    float brightness;
    float shadowMapId;
};

layout (std140, binding = 0) uniform SunLight {
    float ambient;
    float sunBright;
    float sunX;
    float sunY;
    float sunZ;
    float sunColorR;
    float sunColorG;
    float sunColorB;
};

layout (std140, binding = 1) uniform PointLights {
    PointLight p_l[128];
    int total_plights;
};

layout (std140, binding = 3) uniform Fog {
    float fogDensity;
    float fogColorR;
    float fogColorG;
    float fogColorB;
};

uniform sampler2D gPositions;
uniform sampler2D gNormals;
uniform sampler2D gTexture;
uniform sampler2D gEmission;
uniform sampler2D gSpecular;
uniform sampler2D gMetallic;

vec4 calc_sun_light(vec3, vec3, vec3);
vec4 calc_point_light(PointLight, vec3, vec3, float, float, float, float);
vec4 calc_light_factor(vec3, float, vec3, vec3, vec3);
vec4 calc_light(vec3, vec3);
vec4 calc_fog(vec3, vec4);

void main()
{
    vec3 frag_pos = texture(gPositions, out_texture).xyz;
    vec3 normals = texture(gNormals, out_texture).xyz;
    vec4 g_texture = texture(gTexture, out_texture);
    vec4 emission = vec4(texture(gEmission, out_texture).rgb, 0.0) * vec4(5.);
    vec4 metallic = texture(gMetallic, out_texture);

    frag_color = mix(g_texture, metallic, 0.5) * (calc_light(frag_pos, normals) + emission);
    frag_color = fogDensity > 0 ? calc_fog(frag_pos.xyz, frag_color) : frag_color;

    float brightness = dot(frag_color.rgb + (emission.rgb), vec3(0.2126, 0.7152, 0.0722));
    bright_color = brightness > 1.0 ? frag_color : vec4(0., 0., 0., g_texture.a);
}

float vsmFixLightBleed(float pMax, float amount)
{
    return clamp((pMax - amount) / (1.0 - amount), 0.0, 1.0);
}

float calcVSM(int idx, vec4 shadow_coord, float bias) {
    vec4 tex = shadow_coord / shadow_coord.w;
    vec4 vsm = texture(idx == 0 ? shadow_map0 : idx == 1 ? shadow_map1 : shadow_map2, tex.xy);
    float mu = vsm.x;
    float s2 = max(vsm.y - mu * mu, bias);
    float pmax = s2 / (s2 + (tex.z - mu) * (tex.z - mu));

    return tex.z >= vsm.x ? vsmFixLightBleed(pmax, 0.7) : 1.0;
}

float calculate_shadow_vsm(vec4 worldPosition, int idx, float bias) {
    vec4 shadowMapPos = cascade_shadow[idx].projection_view * worldPosition;
    vec4 shadow_coord = (shadowMapPos / shadowMapPos.w) * 0.5 + 0.5;
    return calcVSM(idx, shadow_coord, bias);
}

float calculate_point_light_shadow(samplerCube vsmCubemap, vec3 fragPosition, vec3 lightPos)
{
    vec3 fragToLight = fragPosition - lightPos;
    float currentDepth = length(fragToLight);
    currentDepth /= far_plane;

    vec3 lightPosViewSpace = (out_view_matrix * vec4(lightPos, 1.0)).xyz;

    vec4 vsmValues = texture(vsmCubemap, normalize(fragToLight));
    float mu = vsmValues.r;
    float s2 = max(vsmValues.g - mu * mu, 1.0e-5f);
    float pmax = s2 / (s2 + (currentDepth - mu) * (currentDepth - mu));

    return vsmFixLightBleed(pmax, 0.2);
}

vec4 calc_light(vec3 frag_pos, vec3 normal) {
    vec4 lightFactors = vec4(vec3(sunColorR, sunColorG, sunColorB) * ambient, 1.0);

    vec3 sunPos = normalize(vec3(sunX, sunY, sunZ));
    int cascadeIndex = int(frag_pos.z < cascade_shadow[0].split_distance) + int(frag_pos.z < cascade_shadow[1].split_distance);

    vec4 view_pos = vec4(frag_pos, 1.0);
    vec4 world_position = out_inversed_view_matrix * view_pos;
    world_position /= world_position.w;

    float sun_shadow = calculate_shadow_vsm(world_position, cascadeIndex, 1.0e-7f);

    vec4 sunFactor = abs(dot(normal, sunPos)) < 0.001 ? vec4(0.0) : calc_sun_light(sunPos, frag_pos, normal);

    vec4 point_light_factor = vec4(0.0);
    for (int i = 0; i < total_plights; i++) {
        PointLight p = p_l[i];
        float p_brightness = p.brightness;
        float at_base = 1.0;
        float linear = 0.09 * p_brightness;
        float expo = 0.032 * p_brightness;
        float p_id = p.shadowMapId;
        vec4 shadow = p_id >= 0 ? vec4(calculate_point_light_shadow(point_light_cubemap[int(p_id)], world_position.xyz, vec3(p.plPosX, p.plPosY, p.plPosZ))) : vec4(1.0);
        point_light_factor += calc_point_light(p, frag_pos, normal, at_base, linear, expo, p_brightness) * shadow;
    }

    float brightness = dot(point_light_factor.rgb, vec3(0.2126, 0.7152, 0.0722)) * 5.0;
    lightFactors += sunFactor * clamp(sun_shadow + brightness, 0.0, 1.0);

    lightFactors += point_light_factor;

    return lightFactors;
}

vec4 calc_light_factor(vec3 colors, float brightness, vec3 vPos, vec3 light_dir, vec3 vNormal) {
    vec4 diffuseC = vec4(0.);
    vec4 specularC = vec4(0.);

    float specularF = 0.;
    float diffuseF = max(dot(vNormal, light_dir), 0.);
    diffuseC = vec4(colors, 1.) * brightness * diffuseF;

    vec3 camDir = normalize(-vPos);
    vec3 from_light = light_dir;
    vec3 reflectionF = normalize(from_light + camDir);
    specularF = max(dot(vNormal, reflectionF), 0.);
    specularF = pow(specularF, 8.0);
    specularC = brightness * specularF * vec4(colors, 1.);

    vec4 specularFactor = vec4(vec3(1.0) - texture(gSpecular, out_texture).rgb, 1.0);
    return dot(vNormal, from_light) + 0.0001 >= 0 ? (diffuseC + (specularC * specularFactor)) : vec4(0.);
}

vec4 calc_sun_light(vec3 sunPos, vec3 vPos, vec3 vNormal) {
    return calc_light_factor(vec3(sunColorR, sunColorG, sunColorB), sunBright, vPos, normalize(sunPos), vNormal);
}

vec4 calc_point_light(PointLight light, vec3 vPos, vec3 vNormal, float at_base, float linear, float expo, float bright) {
    vec3 pos = (out_view_matrix * vec4(vec3(light.plPosX, light.plPosY, light.plPosZ), 1.0)).xyz;

    vec3 light_dir = pos - vPos;
    vec3 to_light = normalize(light_dir);
    vec4 light_c = calc_light_factor(vec3(light.plR, light.plG, light.plB), bright, vPos, to_light, vNormal);

    float dist = length(light_dir);
    float attenuation_factor = at_base + linear * dist + expo * pow(dist, 2);
    return light_c / attenuation_factor;
}

vec4 calc_fog(vec3 frag_pos, vec4 color) {
    vec3 fog_color = vec3(fogColorR, fogColorG, fogColorB) * vec3(sunColorR, sunColorG, sunColorB) * sunBright;
    float distance = length(frag_pos);
    float fogFactor = 1. / exp((distance * fogDensity) * (distance * fogDensity));
    fogFactor = clamp(fogFactor, 0., 1.);

    vec3 result = mix(fog_color, color.xyz, fogFactor);
    return vec4(result.xyz, color.w);
}