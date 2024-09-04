struct CascadeShadow {
    float split_distance;
    mat4 projection_view;
};

uniform CascadeShadow cascade_shadow[3];
uniform samplerCube point_light_cubemap[3];
uniform sampler2D sun_shadow_map[3];
uniform float far_plane;

uniform float PosExp;
uniform float NegExp;

vec2 warpDepth(vec2 exponents, float depth) {
    depth = 2.0f * depth - 1.0f;
    float pos = exp(exponents.x * depth);
    float neg = -exp(-exponents.y * depth);
    vec2 wDepth = vec2(pos, neg);
    return wDepth;
}

float Chebyshev(vec2 moments, float mean, float minVariance) {
    if(mean <= moments.x) {
        return 1.0f;
    } else {
        float variance = moments.y - (moments.x * moments.x);
        variance = max(variance, minVariance);
        float d = mean - moments.x;
        return variance / (variance + (d * d));
    }
}

float EVSM(int idx, vec4 shadow_coord, float bias) {
    float positiveExponent = PosExp;
    float negativeExponent = NegExp;
    vec2 exponents = vec2(positiveExponent, negativeExponent);

    vec4 moments = texture(sun_shadow_map[idx], shadow_coord.xy).xyzw;
    vec2 posMoments = vec2(moments.x, moments.z);
    vec2 negMoments = vec2(moments.y, moments.w);
    vec2 wDepth = warpDepth(exponents, shadow_coord.z);

    vec2 depthScale = 2.e-3f * exponents * wDepth;
    vec2 minVariance = depthScale * depthScale;
    float posResult = Chebyshev(posMoments, wDepth.x, minVariance.x);
    float negResult = Chebyshev(negMoments, wDepth.y, minVariance.y);
    return min(posResult, negResult);
}

float calcShadowDepth(int idx, vec4 shadow_coord, float bias) {
    return EVSM(idx, shadow_coord, bias);
}

float calculate_shadow_vsm(vec4 worldPosition, int idx, float bias) {
    vec4 shadowMapPos = cascade_shadow[idx].projection_view * worldPosition;
    vec4 shadow_coord = (shadowMapPos / shadowMapPos.w) * 0.5 + 0.5;
    float c0 = calcShadowDepth(idx, shadow_coord, bias);
    return c0;
}

float calc_sun_shadows(vec4 world_position, vec3 frag_pos) {
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

float vsmFixLightBleed(float pMax, float amount) {
    return clamp((pMax - amount) / (1.0 - amount), 0.0, 1.0);
}

float calculate_point_light_shadows(samplerCube vsmCubemap, vec3 fragPosition, vec3 lightPos)
{
    vec3 fragToLight = fragPosition - lightPos;
    float currentDepth = length(fragToLight);
    currentDepth /= far_plane;

    vec4 vsm = texture(vsmCubemap, normalize(fragToLight));

    float E_x2 = vsm.y;
    float Ex_2 = vsm.x * vsm.x;
    float var = max(E_x2 - Ex_2, 1.0e-5f);
    float mD = vsm.x - currentDepth;
    float mD_2 = mD * mD;
    float p = var / (var + mD_2);

    return max(vsmFixLightBleed(p, 0.7), int(currentDepth <= vsm.x));
}