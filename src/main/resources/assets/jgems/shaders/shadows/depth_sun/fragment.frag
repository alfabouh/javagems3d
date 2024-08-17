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

float ESM(int idx, vec4 shadow_coord, float bias) {
    float esm = texture(sun_shadow_map[idx], shadow_coord.xy).x;
    float currentDepth = exp(80. * shadow_coord.z);
    float shadowFactor = esm / currentDepth;
    return clamp(shadowFactor, 0.0, 1.0);
}

float ESM(int idx, vec4 shadow_coord, float bias) {
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
    //  float d = gl_FragCoord.z;
    //
    //  float depth1 = exp(80. * d);
    //  float depth2 = exp(80. * d * d);
    //
    //  frag_color0 = vec4(depth1, depth2, 0., 0.);

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
*/

layout (location = 0) out vec4 frag_color0;

uniform float alpha_discard;
uniform sampler2D texture_sampler;
uniform bool use_texture;

in vec2 out_texture;

void VSM() {
    float d = gl_FragCoord.z;
    float dx = dFdx(d);
    float dy = dFdy(d);
    float moment2 = d * d + 0.25 * (dx * dx + dy * dy);
    frag_color0 = vec4(d, moment2, 0., 0.);
}

void main()
{
    vec4 v = !use_texture ? vec4(1.0) : texture(texture_sampler, out_texture);
    if (v.a < alpha_discard) {
        discard;
    }

    VSM();
}