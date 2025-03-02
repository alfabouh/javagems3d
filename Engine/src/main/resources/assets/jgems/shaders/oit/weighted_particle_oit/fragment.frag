//in vec2 uv_coordinates;
//
//layout (location = 0) out vec4 accumulated;
//layout (location = 1) out float reveal;
//
//in vec2 uv_coordinates;
//
//uniform float alpha_factor;
//
//void main()
//{
//    //vec4 color = texture(diffuse_map, uv_coordinates);
//    //if (color.a >= 1.) {
//    //    discard;
//    //}
//    vec4 color = vec4(1., 0., 0., 0.5);
//    float weight = max(min(1.0, max(max(color.r, color.g), color.b) * color.a), color.a) * clamp(0.03 / (1.0e-5f + pow(gl_FragCoord.z / 200, 4.0)), 1.0e-2f, 3.0e+3f);
//
//    accumulated = vec4(color.rgb * color.a, color.a) * weight;
//    reveal = color.a;
//}

/////////////////////////////////

in vec2 uv_coordinates;

in vec4 model_vertex_pos;
in vec3 modelview_vertex_pos;
in mat4 out_view_matrix;

layout (location = 0) out vec4 accumulated;
layout (location = 1) out float reveal;
layout (location = 2) out vec4 bright_color;

struct CascadeShadow {
    float split_distance;
    mat4 projection_view;
};

uniform CascadeShadow cascade_shadow[3];
uniform samplerCube point_light_cubemap[3];
uniform sampler2D sun_shadow_map[3];
uniform float far_plane;

struct Sun {
    vec3 position;
    float _padding0;
    vec3 color;
    float ambient;
    float brightness;
};
layout (std430, binding = 5) buffer SunLight {
    Sun sun;
};

struct PointLight
{
    vec3 position;
    float _padding0;
    vec3 view_position;
    float _padding00;
    vec3 color;
    float brightness;
    int attachedShadowSceneId;
    int _padding000;
    int _padding0000;
    int _padding00000;
};
layout (std430, binding = 6) buffer PointLights {
    PointLight p_l[CONST.MAX_POINT_LIGHTS];
    int total_plights;
};

struct Fog {
    vec3 color;
    float density;
};
layout (std430, binding = 7) buffer WorldFog {
    Fog fog;
};

uniform vec3 camera_pos;

uniform vec3 color_mask;
uniform sampler2D diffuse_map;
uniform bool use_texture;

uniform float brightness;
uniform float alpha_factor;

vec4 calc_sun_light(vec3, vec3);
vec4 calc_point_light(PointLight, vec3, float, float, float, float);
vec4 calc_light_factor(vec3, float, vec3, vec3, vec3);
vec4 calc_light(vec3);
vec4 calc_fog(vec3, vec4);
float calc_fog_float(vec3, float);

bool checkCode(int i1, int i2) {
    int i3 = i1 & i2;
    return bool(i3 != 0);
}

void main()
{
    vec3 frag_pos = modelview_vertex_pos;
    vec4 g_texture = use_texture ? texture(diffuse_map, uv_coordinates) : vec4(1.);

    vec4 lights = calc_light(frag_pos);

    vec4 frag_color = (g_texture * vec4(color_mask, 1.)) * (lights + vec4(vec3(brightness), 0.));
    frag_color = calc_fog(frag_pos.xyz, frag_color);
    frag_color = vec4(frag_color.xyz, g_texture.a);

    float a_factor = alpha_factor * frag_color.a;

    float weight = max(min(1.0, max(max(frag_color.r, frag_color.g), frag_color.b) * a_factor), a_factor) * clamp(0.03 / (1.0e-5f + pow(gl_FragCoord.z / 200, 4.0)), 1.0e-2f, 3.0e+3f);
    accumulated = vec4(frag_color.rgb * a_factor, a_factor) * weight;

    reveal = fog.density > 0 ? calc_fog_float(frag_pos.xyz, a_factor) : a_factor;

    float brightness = dot(frag_color.rgb, vec3(0.2126, 0.7152, 0.0722)) + brightness;
    bright_color = brightness >= 0.75 ? accumulated : vec4(0.);
}

float calc_sun_shadows(vec4 world_position, int idx, vec3 frag_pos) {
    vec4 shadowMapPos = cascade_shadow[idx].projection_view * world_position;
    vec4 shadow_coord = (shadowMapPos / shadowMapPos.w) * 0.5 + 0.5;
    float closest = texture(sun_shadow_map[idx], world_position.xy).r;
    float currD = shadow_coord.z;
    return currD - 0.005 > closest ? 0. : 1.;
}

float calculate_point_light_shadows(samplerCube vsmCubemap, vec3 fragPosition, vec3 lightPos) {
    vec3 fragToLight = fragPosition - lightPos;
    float currentDepth = length(fragToLight);
    currentDepth /= far_plane;

    vec4 vsm = texture(vsmCubemap, normalize(fragToLight));

    return currentDepth - 0.005 > vsm.r ? 0. : 1.;
}

vec4 calc_light(vec3 frag_pos) {
    vec4 lightFactors = vec4(sun.color * sun.ambient, 1.0);

    vec3 position = normalize(sun.position);

    int cascadeIndex = int(frag_pos.z < cascade_shadow[0].split_distance) + int(frag_pos.z < cascade_shadow[1].split_distance);
    float sun_shadow = calc_sun_shadows(model_vertex_pos, cascadeIndex, frag_pos);

    vec4 sunFactor = calc_sun_light(position, frag_pos);

    vec4 point_light_factor = vec4(0.0);
    for (int i = 0; i < total_plights; i++) {
        PointLight p = p_l[i];
        float p_brightness = p.brightness;
        float at_base = 1.0;
        float linear = 0.1 * p_brightness;
        float expo = 0.032 / sqrt(p_brightness);
        float p_id = p.attachedShadowSceneId;
        vec4 shadow = p_id >= 0 ? vec4(calculate_point_light_shadows(point_light_cubemap[int(p_id)], model_vertex_pos.xyz, p.position.xyz)) : vec4(1.0);
        point_light_factor += calc_point_light(p, frag_pos, at_base, linear, expo, p_brightness) * shadow;
    }

    float brightness = dot(point_light_factor.rgb, vec3(0.2126, 0.7152, 0.0722)) * 5.0;
    lightFactors += sunFactor * clamp(sun_shadow + brightness, 0.0, 1.0);

    lightFactors += point_light_factor;

    return lightFactors;
}

vec4 calc_sun_light(vec3 position, vec3 vPos) {
    return vec4(sun.color, 1.);
}

vec4 calc_point_light(PointLight light, vec3 vPos, float at_base, float linear, float expo, float bright) {
    vec3 pos = light.view_position;

    vec3 light_dir = pos - vPos;
    vec4 light_c = vec4(light.color, 1.);

    float dist = length(light_dir);
    float attenuation_factor = at_base + linear * dist + expo * pow(dist, 2);
    return light_c / attenuation_factor;
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

float calc_fog_float(vec3 frag_pos, float f) {
    float distance = length(frag_pos);
    float fogFactor = 1. / exp((distance * fog.density) * (distance * fog.density));
    fogFactor = clamp(fogFactor, 0., 1.);
    return f * fogFactor;
}