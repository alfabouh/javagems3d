#extension GL_ARB_bindless_texture : require
precision highp float;
precision highp int;

in vec2 uv_coordinates;

layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 bright_color;

uniform vec3 camera_pos;
uniform uvec2 ambient_cubemap;
uniform bool useCubeMap;
uniform bool isSsaoValid;
uniform sampler2D gPositions;
uniform sampler2D gNormals;
uniform sampler2D gTexture;
uniform sampler2D gEmission;
uniform sampler2D gMetallicRoughness;
uniform sampler2D ssao_map;
uniform bool showCascades;
uniform mat4 view_matrix;

#include "/assets/shaders/libs/shadows"
#include "/assets/shaders/libs/lighting"
#include "/assets/shaders/libs/fog"

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
        float shadow = p_id >= 0 ? calculate_point_light_shadows(sampleShadowPl(int(p_id)), world_position.xyz, p.position.xyz) : 1.;
        point_light_factor += calc_point_light(p, frag_pos, normal, params.x, params.y, params.z, p_brightness, specularFactor) * shadow;
    }

    float brightness = dot(point_light_factor.rgb, vec3(0.2126, 0.7152, 0.0722)) * 5.0;
    lightFactors += sunFactor * clamp(sun_shadow + brightness, 0.0, 1.0);
    lightFactors += point_light_factor;

    return lightFactors;
}

vec3 refract_cubemap(vec3 normal, float cnst, vec4 world_position) {
    float ratio = 1.0 / cnst;
    vec3 I = normalize(world_position.xyz - camera_pos);
    vec3 R = refract(I, normalize(normal), ratio);
    return texture(samplerCube(ambient_cubemap), R).rgb;
}

void main()
{
    vec3 frag_pos = texture(gPositions, uv_coordinates).xyz;
    vec3 normals = texture(gNormals, uv_coordinates).xyz;
    vec4 g_texture = texture(gTexture, uv_coordinates);
    vec3 emission = texture(gEmission, uv_coordinates).rgb;
    vec2 metallic_roughness = texture(gMetallicRoughness, uv_coordinates).rg;

    mat4 inversed_view = inverse(view_matrix);

    vec4 view_pos = vec4(frag_pos, 1.0);
    vec4 world_position = inversed_view * view_pos;
    world_position /= world_position.w;

    if (useCubeMap) {
        vec4 model_normal_pos = vec4(normals, 1.0);
        vec4 world_normal = inversed_view * model_normal_pos;
        world_normal /= world_normal.w;
        vec3 refracted_color = refract_cubemap(world_normal.xyz, 1.73, world_position);
        g_texture.rgb = mix(g_texture.rgb, refracted_color, metallic_roughness.r * 0.5);
    }

    float f1 = 1.0;
    if (isSsaoValid) {
        float gray = dot(g_texture.rgb, vec3(0.299, 0.587, 0.114));
        float AO = texture(ssao_map, uv_coordinates).r;
        f1 = pow(AO, (1.0 - gray) * 3.);
    }

    vec3 lights = calc_light(frag_pos, normals, metallic_roughness.g, world_position) * vec3(f1);

    frag_color = g_texture * vec4(lights + emission, 1.0);
    frag_color = calc_fog(frag_pos.xyz, frag_color, 1.);

    float brightness = dot(frag_color.rgb + emission, vec3(0.2126, 0.7152, 0.0722));
    bright_color = brightness >= 2.0 ? vec4(frag_color.xyz, 1.) : vec4(0., 0., 0., 1.);

    if (showCascades) {
        int cascadeIndex = int(frag_pos.z < cascade_shadow_split_distance_0) + int(frag_pos.z < cascade_shadow_split_distance_1);
        switch (cascadeIndex) {
            case 0:
                frag_color.rgb *= vec3(1.0f, 0.75f, 0.75f);
                break;
            case 1:
                frag_color.rgb *= vec3(0.75f, 1.0f, 0.75f);
                break;
            case 2:
                frag_color.rgb *= vec3(0.75f, 0.75f, 1.0f);
                break;
            default :
                frag_color.rgb *= vec3(1.0f, 1.0f, 0.25f);
                break;
        }
    }
}