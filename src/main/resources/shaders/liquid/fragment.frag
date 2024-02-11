in vec2 texture_coordinates;
in vec3 mv_vertex_normal;
in vec3 mv_vertex_pos;
in mat3 TBN;
in vec4 out_world_position;
in mat4 out_view_matrix;

layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 bright_color;

uniform vec3 camera_pos;

uniform int texturing_code;
uniform int lighting_code;

const vec3 sampleOffsetDirections[20] = vec3[]
(
    vec3( 1,  1,  1), vec3( 1, -1,  1), vec3(-1, -1,  1), vec3(-1,  1,  1),
    vec3( 1,  1, -1), vec3( 1, -1, -1), vec3(-1, -1, -1), vec3(-1,  1, -1),
    vec3( 1,  1,  0), vec3( 1, -1,  0), vec3(-1, -1,  0), vec3(-1,  1,  0),
    vec3( 1,  0,  1), vec3(-1,  0,  1), vec3( 1,  0, -1), vec3(-1,  0, -1),
    vec3( 0,  1,  1), vec3( 0, -1,  1), vec3( 0, -1, -1), vec3( 0,  1, -1)
);

uniform samplerCube ambient_cubemap;
uniform vec2 texture_scaling;
uniform sampler2D diffuse_map;
uniform sampler2D normals_map;

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
    int shadowMapId;
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
};

layout (std140, binding = 2) uniform Misc {
    float w_tick;
};

layout (std140, binding = 3) uniform Fog {
    float fogDensity;
    float fogColorR;
    float fogColorG;
    float fogColorB;
};

vec4 calc_sun_light(vec3, vec3, vec3);
vec4 calc_point_light(PointLight, vec3, vec3, float, float, float, float);
vec4 calc_light_factor(vec3, float, vec3, vec3, vec3);
vec4 calc_light();
vec4 calc_fog(vec3, vec4);

vec2 scaled_coordinates() {
    return (texture_coordinates + w_tick * 0.05) * texture_scaling;
}

vec4 refract_cubemap(vec3 normal, float cnst) {
    float fogFactor = fogDensity * 100;
    float f = 1.0 - clamp(fogFactor, 0.0, 0.7);

    float ratio = 1.0 / cnst;
    vec3 I = normalize(out_world_position.xyz - camera_pos);
    vec3 R = refract(I, normalize(normal), ratio);
    return f * vec4(texture(ambient_cubemap, R).rgb, 1.0);
}

vec3 calc_normal_map() {
    vec3 normal = texture(normals_map, scaled_coordinates() + w_tick * 0.015).rgb;
    normal = normalize(normal * 2.0 - 1.0);
    normal = normalize(TBN * normal);
    return normal;
}

void main()
{
    vec4 lightFactor = calc_light();
    vec4 diffuse_texture = texture(diffuse_map, scaled_coordinates());
    diffuse_texture += refract_cubemap(calc_normal_map(), 1.1);

    vec4 final = diffuse_texture * lightFactor;
    frag_color = vec4(final.xyz, 1.0);
    frag_color = fogDensity > 0 ? calc_fog(mv_vertex_pos, frag_color) : frag_color;
    float brightness = frag_color.r + frag_color.g + frag_color.b;

    bright_color = brightness >= 8. ? frag_color : vec4(0., 0., 0., 1.0);
}

float per_cascade_bias_shadow[3] = float[](5.0e-7f, 5.0e-7f, 5.0e-7f);
float per_cascade_linear_shadow[3] = float[](0.5, 0.5, 0.75);

float calcVSM(int idx, vec4 shadow_coord, vec2 offset, float bias, float linear) {
    vec2 moments = texture(idx == 0 ? shadow_map0 : idx == 1 ? shadow_map1 : shadow_map2, shadow_coord.xy + offset).rg;
    float variance = moments.y - (moments.x * moments.x);

    variance = max(variance, bias);
    float d = shadow_coord.z - moments.x;
    float shadowPCT = smoothstep(linear, 1.0, variance / (variance + d * d));

    return shadowPCT > 1.0e-18f || shadow_coord.z <= moments.x ? 1.0 : shadowPCT;
}

float calculate_shadow_no_pcf(vec4 worldPosition, int idx, float bias, float linear) {
    vec4 shadowMapPos = cascade_shadow[idx].projection_view * worldPosition;
    vec4 shadow_coord = (shadowMapPos / shadowMapPos.w) * 0.5 + 0.5;
    return calcVSM(idx, shadow_coord, vec2(0.0), bias, linear);
}

float calculate_shadow_blur5x5(vec4 worldPosition, int idx, float bias, float linear) {
    vec4 shadowMapPos = cascade_shadow[idx].projection_view * worldPosition;
    vec4 shadow_coord = (shadowMapPos / shadowMapPos.w) * 0.5 + 0.5;

    float kernel[5][5] = float[5][5](
        float[5](1.0,  4.0,  6.0,  4.0, 1.0),
        float[5](4.0, 16.0, 24.0, 16.0, 4.0),
        float[5](6.0, 24.0, 36.0, 24.0, 6.0),
        float[5](4.0, 16.0, 24.0, 16.0, 4.0),
        float[5](1.0,  4.0,  6.0,  4.0, 1.0)
    );

    float shadow = 0.0;
    vec2 texelSize = 1.0 / textureSize(idx == 0 ? shadow_map0 : idx == 1 ? shadow_map1 : shadow_map2, 0);

    for (int i = -2; i <= 2; i++) {
        for (int j = -2; j <= 2; j++) {
            vec2 offset = vec2(float(i), float(j)) * texelSize;
            shadow += calcVSM(idx, shadow_coord, offset, bias, linear) * kernel[i + 2][j + 2];
        }
    }

    return shadow / 256.0;
}

float calculate_point_light_shadow(samplerCube cubemap, vec3 fragPosition, vec3 lightPos)
{
    float shadow = 0.0;
    float bias = 0.1;
    float samples = 9;
    float viewDistance = length(camera_pos - fragPosition);
    float diskRadius = 0.075;
    vec3 fragToLight = fragPosition - lightPos;
    float currentDepth = length(fragToLight);
    for (int i = 0; i < samples; i++) {
        float closestDepth = texture(cubemap, fragToLight + sampleOffsetDirections[i] * diskRadius).r;
        closestDepth *= far_plane;
        shadow += currentDepth - bias > closestDepth ? 0.0 : 1.0;
    }
    return shadow / float(samples);
}

vec4 calc_light() {
    vec4 lightFactors = vec4(0.);
    vec3 normal = normalize(mv_vertex_normal);
    vec3 sunPos = normalize(vec3(sunX, sunY, sunZ));
    int cascadeIndex = int(mv_vertex_pos.z < cascade_shadow[0].split_distance) + int(mv_vertex_pos.z < cascade_shadow[1].split_distance);
    float bias = per_cascade_bias_shadow[cascadeIndex];
    bias *= tan(acos(dot(normal, sunPos)));
    bias = clamp(bias, 0.0, 1.0e-5f);

    float linear = per_cascade_linear_shadow[cascadeIndex];
    float sun_shadow = calculate_shadow_blur5x5(out_world_position, cascadeIndex, bias, linear);

    vec4 calcSunFactor = abs(dot(normal, sunPos)) < 0.001 ? vec4(0.0) : calc_sun_light(sunPos, mv_vertex_pos, normal);

    int i = 0;
    vec4 point_light_factor = vec4(0.0);
    while (p_l[i].brightness > 0) {
        PointLight p = p_l[i++];
        float bright = p.brightness;
        float at_base = 1.8 / (bright * 0.5);
        float linear = 2.25 / (bright * 2.75);
        float expo = 0.6 / (bright * 0.25f);
        vec4 shadow = p.shadowMapId >= 0 ? vec4(calculate_point_light_shadow(point_light_cubemap[p.shadowMapId], out_world_position.xyz, vec3(p.plPosX, p.plPosY, p.plPosZ))) : vec4(1.0);
        point_light_factor += calc_point_light(p, mv_vertex_pos, normal, at_base, linear, expo, bright) * shadow;
    }

    float prgb = point_light_factor.r + point_light_factor.g + point_light_factor.b;
    lightFactors += calcSunFactor * clamp(sun_shadow + prgb, 0.0, 1.0);
    lightFactors += point_light_factor;
    lightFactors += vec4(vec3(sunColorR, sunColorG, sunColorB) * ambient, 0.0);
    return lightFactors;
}

vec4 calc_light_factor(vec3 colors, float brightness, vec3 vPos, vec3 light_dir, vec3 vNormal) {
    vec3 new_normal = calc_normal_map();
    vec4 diffuseC = vec4(0.);
    vec4 specularC = vec4(0.);

    float specularF = 0.;
    float diffuseF = max(dot(new_normal, light_dir), 0.);
    diffuseC = vec4(colors, 1.) * brightness * diffuseF;

    vec3 camDir = normalize(-vPos);
    vec3 from_light = light_dir;
    vec3 reflectionF = normalize(from_light + camDir);
    specularF = max(dot(new_normal, reflectionF), 0.);
    specularF = pow(specularF, 12.0);
    specularC = brightness * specularF * vec4(colors, 1.);

    return dot(vNormal, from_light) + 0.0001 >= 0 ? (diffuseC + specularC) : vec4(0.);
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