//in vec2 out_texture;
//
//layout (location = 0) out vec4 accumulated;
//layout (location = 1) out float reveal;
//
//in vec2 texture_coordinates;
//
//uniform float alpha_factor;
//
//void main()
//{
//    //vec4 color = texture(diffuse_map, texture_coordinates);
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

in vec2 texture_coordinates;

in vec4 m_vertex_pos;
in vec3 mv_vertex_pos;
in mat4 out_view_matrix;

layout (location = 0) out vec4 frag_col;

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
    vec3 frag_pos = mv_vertex_pos;
    vec4 g_texture = use_texture ? texture(diffuse_map, texture_coordinates) : vec4(1.);

    vec4 lights = calc_light(frag_pos);

    vec4 color = (g_texture * vec4(color_mask, 1.)) * (lights + vec4(vec3(brightness), 0.));
    color = fogDensity > 0 ? calc_fog(frag_pos.xyz, color) : color;
    color = vec4(color.xyz, g_texture.a);

    float a_factor = alpha_factor * color.a;

    frag_col = vec4(color.xyz, a_factor);
}

float vsmFixLightBleed(float pMax, float amount)
{
    return clamp((pMax - amount) / (1.0 - amount), 0.0, 1.0);
}

float calcVSM(int idx, vec4 shadow_coord, float bias) {
    vec4 tex = shadow_coord / shadow_coord.w;
    vec4 vsm = texture(idx == 0 ? shadow_map0 : idx == 1 ? shadow_map1 : shadow_map2, tex.xy);

    if (vsm.g <= 0) {
        return 1.0;
    }

    float E_x2 = vsm.y;
    float Ex_2 = vsm.x * vsm.x;
    float var = max(E_x2 - Ex_2, bias);
    float mD = vsm.x - tex.z;
    float mD_2 = mD * mD;
    float p = var / (var + mD_2);

    return max(vsmFixLightBleed(p, 0.7), int(tex.z <= vsm.x));
}

float calculate_shadow_vsm(vec4 worldPosition, int idx, float bias) {
    vec4 shadowMapPos = cascade_shadow[idx].projection_view * worldPosition;
    vec4 shadow_coord = (shadowMapPos / shadowMapPos.w) * 0.5 + 0.5;
    float c0 = calcVSM(idx, shadow_coord, bias);
    return c0;
}

float calcSunShineVSM(vec4 world_position, vec3 frag_pos) {
    const float bias = 1.0e-5f;
    const float bias_f = 3.0;
    const float half_bias_f = bias_f / 2.0;
    const int max_cascades = 3;
    int cascadeIndex = int(frag_pos.z < cascade_shadow[0].split_distance - half_bias_f) + int(frag_pos.z < cascade_shadow[1].split_distance - half_bias_f);
    float f0 = calculate_shadow_vsm(world_position, cascadeIndex, bias);
    if (cascadeIndex >= 0 && cascadeIndex < max_cascades) {
        int cascadeIndex2 = int(frag_pos.z < cascade_shadow[cascadeIndex].split_distance + half_bias_f) + cascadeIndex;
        float f1 = calculate_shadow_vsm(world_position, cascadeIndex2, bias);
        float p2 = (cascade_shadow[cascadeIndex].split_distance + half_bias_f) - frag_pos.z;
        return mix(f0, f1, p2 / bias_f);
    }
    return f0;
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

vec4 calc_light(vec3 frag_pos) {
    vec4 lightFactors = vec4(vec3(sunColorR, sunColorG, sunColorB) * ambient, 1.0);

    vec3 sunPos = normalize(vec3(sunX, sunY, sunZ));

    float sun_shadow = calcSunShineVSM(m_vertex_pos, frag_pos);

    vec4 sunFactor = calc_sun_light(sunPos, frag_pos);

    vec4 point_light_factor = vec4(0.0);
    for (int i = 0; i < total_plights; i++) {
        PointLight p = p_l[i];
        float p_brightness = p.brightness;
        float at_base = 1.0;
        float linear = 0.09 * p_brightness;
        float expo = 0.032 * p_brightness;
        float p_id = p.shadowMapId;
        vec4 shadow = p_id >= 0 ? vec4(calculate_point_light_shadow(point_light_cubemap[int(p_id)], m_vertex_pos.xyz, vec3(p.plPosX, p.plPosY, p.plPosZ))) : vec4(1.0);
        point_light_factor += calc_point_light(p, frag_pos, at_base, linear, expo, p_brightness) * shadow;
    }

    float brightness = dot(point_light_factor.rgb, vec3(0.2126, 0.7152, 0.0722)) * 5.0;
    lightFactors += sunFactor * clamp(sun_shadow + brightness, 0.0, 1.0);

    lightFactors += point_light_factor;

    return lightFactors;
}

vec4 calc_sun_light(vec3 sunPos, vec3 vPos) {
    return vec4(vec3(sunColorR, sunColorG, sunColorB), 1.);
}

vec4 calc_point_light(PointLight light, vec3 vPos, float at_base, float linear, float expo, float bright) {
    vec3 pos = (out_view_matrix * vec4(vec3(light.plPosX, light.plPosY, light.plPosZ), 1.0)).xyz;

    vec3 light_dir = pos - vPos;
    vec4 light_c = vec4(vec3(light.plR, light.plG, light.plB), 1.);

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

float calc_fog_float(vec3 frag_pos, float f) {
    float distance = length(frag_pos);
    float fogFactor = 1. / exp((distance * fogDensity) * (distance * fogDensity));
    fogFactor = clamp(fogFactor, 0., 1.);
    return f * fogFactor;
}