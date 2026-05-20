/*
vec2 warpDepth(vec2 exponents, float depth)
{
    depth = 2.0f * depth - 1.0f;
    float pos = exp(exponents.x * depth);
    float neg = -exp(-exponents.y * depth);
    vec2 wDepth = vec2(pos, neg);
    return wDepth;
}

float Chebyshev(vec2 moments, float mean, float minVariance)
{
    float shadow = 1.0f;
    if(mean <= moments.x)
    {
        shadow = 1.0f;
        return shadow;
    }
    else
    {
        float variance = moments.y - (moments.x * moments.x);
        variance = max(variance, minVariance);
        float d = mean - moments.x;
        shadow = variance / (variance + (d * d));
        return shadow;
    }
}

float vsmFixLightBleed(float pMax, float amount) {
    return clamp((pMax - amount) / (1.0 - amount), 0.0, 1.0);
}

float VSM(int idx, vec4 shadow_coord, float bias) {
    vec4 vsm = texture(sun_shadow_map[idx], shadow_coord.xy);

    float E_x2 = vsm.y;
    float Ex_2 = vsm.x * vsm.x;
    float var = max(E_x2 - Ex_2, bias);
    float mD = vsm.x - shadow_coord.z;
    float mD_2 = mD * mD;
    float p = var / (var + mD_2);

    return max(vsmFixLightBleed(p, 0.7), int(shadow_coord.z <= vsm.x));
}

float ESM(int idx, vec4 shadow_coord, float bias) {
    float esm = texture(sun_shadow_map[idx], shadow_coord.xy).x;
    float currentDepth = exp(80. * shadow_coord.z);
    float shadowFactor = esm / currentDepth;
    return clamp(shadowFactor, 0.0, 1.0);
}

float EVSM(int idx, vec4 shadow_coord, float bias) {
    float positiveExponent = 60.0f;
    float negativeExponent = 5.0f;
    vec2 exponents = vec2(positiveExponent, negativeExponent);

    vec4 moments = texture(sun_shadow_map[idx], shadow_coord.xy).xyzw;
    vec2 posMoments = vec2(moments.x, moments.z);
    vec2 negMoments = vec2(moments.y, moments.w);
    vec2 wDepth = warpDepth(exponents, shadow_coord.z);

    vec2 depthScale = 0.0001f * exponents * wDepth;
    vec2 minVariance = depthScale * depthScale;
    float posResult = Chebyshev(posMoments, wDepth.x, minVariance.x);
    float negResult = Chebyshev(negMoments, wDepth.y, minVariance.y);
    return min(posResult, negResult);
}

void ESM() {
    // float d = gl_FragCoord.z;
    //
    // float depth1 = exp(80. * d);
    // float depth2 = exp(80. * d * d);
    //
    // frag_color0 = vec4(depth1, depth2, 0., 0.);

    float positiveExponent = 60.0f;
    float negativeExponent = 5.0f;
    float depth = gl_FragCoord.z;
    vec2 exponents = vec2(positiveExponent, negativeExponent);
    depth = 2.0f * depth - 1.0f;
    float pos = exp(exponents.x * depth);
    float neg = -exp(-exponents.y * depth);
    vec2 warpDepth = vec2(pos, neg);
    frag_color0 = vec4(warpDepth, warpDepth * warpDepth);
}

void Shadows() {
    float d = gl_FragCoord.z;
    float dx = dFdx(d);
    float dy = dFdy(d);
    float moment2 = d * d + 0.25 * (dx * dx + dy * dy);
    frag_color0 = vec4(d, moment2, 0., 0.);
}
*/

#extension GL_ARB_bindless_texture : require
precision highp float;
precision highp int;

layout (location = 0) out vec4 frag_color0;

uniform float PosExp;
uniform float NegExp;

in vec2 uv_coordinates;
in flat uint matertial_id;
in flat uint ent_id;

struct Properties {
    float alpha_discard;
};

struct Material {
    vec4 diffuse_color;
    vec3 emissive_color;
    float _padding000; //PADDING

    float metallic_factor;
    float roughness_factor;
    int diffuse_map_id;
    float _padding001; //PADDING2

    int normals_map_id;
    int emissive_map_id;
    int metallic_roughness_map_id;
    int texturing_code;
};

layout(std430, binding = 2) readonly restrict buffer BindlessTextures {
    uvec2 textures[CONST.MAX_BINDLESS_TEXTURES];
};

layout(std430, binding = 3) readonly restrict buffer MaterialsData {
    Material materials[CONST.MAX_INDIRECT_RENDERING_MATERIALS];
};

layout(std430, binding = 4) readonly restrict buffer RenderPropertiesData {
    Properties properties[CONST.MAX_INDIRECT_RENDERING_PROPERIES];
};

void Shadows() {
    float positiveExponent = PosExp;
    float negativeExponent = NegExp;
    float depth = gl_FragCoord.z;
    vec2 exponents = vec2(positiveExponent, negativeExponent);
    depth = 2.0f * depth - 1.0f;
    float pos = exp(exponents.x * depth);
    float neg = -exp(-exponents.y * depth);
    vec2 warpDepth = vec2(pos, neg);
    frag_color0 = vec4(warpDepth, warpDepth * warpDepth);
}

const int diffuse_code = 1 << 2;

bool checkCode(int i1, int i2) {
    int i3 = i1 & i2;
    return bool(i3 != 0);
}

void main()
{
    Material mat = materials[matertial_id];
    Properties property = properties[ent_id];
    float alpha_discard = property.alpha_discard;

    float diffuse_a = mat.diffuse_color.a;
    if (checkCode(mat.texturing_code, diffuse_code)) {
        diffuse_a *= texture(sampler2D(textures[mat.diffuse_map_id]), uv_coordinates).a;
    }
    if (diffuse_a < alpha_discard) {
        discard;
    }

    Shadows();
}