//TODO ARRAYS
uniform mat4 cascade_shadow_projection_view_0;
uniform mat4 cascade_shadow_projection_view_1;
uniform mat4 cascade_shadow_projection_view_2;
//TODO ARRAYS
uniform float cascade_shadow_split_distance_0;
uniform float cascade_shadow_split_distance_1;
uniform float cascade_shadow_split_distance_2;
//TODO ARRAYS
uniform sampler2D sun_shadow_map_0;
uniform sampler2D sun_shadow_map_1;
uniform sampler2D sun_shadow_map_2;
//TODO ARRAYS
uniform samplerCube point_light_cubemap_0;
uniform samplerCube point_light_cubemap_1;
uniform samplerCube point_light_cubemap_2;
//TODO ARRAYS
uniform sampler2D spot_light_shadow_map_0;
uniform sampler2D spot_light_shadow_map_1;
uniform sampler2D spot_light_shadow_map_2;
//TODO ARRAYS
uniform mat4 spot_light_shadow_projection_view_0;
uniform mat4 spot_light_shadow_projection_view_1;
uniform mat4 spot_light_shadow_projection_view_2;

uniform float pl_far_plane;
uniform float sl_far_plane;
uniform float PosExp;
uniform float NegExp;

vec4 sampleShadowSun(int idx, vec2 uv) {
    if (idx == 0) {
        return texture(sun_shadow_map_0, uv);
    }
    if (idx == 1) {
        return texture(sun_shadow_map_1, uv);
    }
    return texture(sun_shadow_map_2, uv);
}

samplerCube sampleShadowPl(int idx) {
    if (idx == 0) {
        return point_light_cubemap_0;
    }
    if (idx == 1) {
        return point_light_cubemap_1;
    }
    return point_light_cubemap_2;
}

float sampleSplitDist(int idx) {
    if (idx == 0) {
        return cascade_shadow_split_distance_0;
    }
    if (idx == 1) {
        return cascade_shadow_split_distance_1;
    }
    return cascade_shadow_split_distance_2;
}

mat4 sampleProjView(int idx) {
    if (idx == 0) {
        return cascade_shadow_projection_view_0;
    }
    if (idx == 1) {
        return cascade_shadow_projection_view_1;
    }
    return cascade_shadow_projection_view_2;
}

sampler2D sampleShadowSl(int idx) {
    if (idx == 0) {
        return spot_light_shadow_map_0;
    }
    if (idx == 1) {
        return spot_light_shadow_map_1;
    }
    return spot_light_shadow_map_2;
}

mat4 sampleProjViewSl(int idx) {
    if (idx == 0) {
        return spot_light_shadow_projection_view_0;
    }
    if (idx == 1) {
        return spot_light_shadow_projection_view_1;
    }
    return spot_light_shadow_projection_view_2;
}

vec2 warp(vec2 exponents, float depth) {
    depth = 2.0f * depth - 1.0f;
    float pos = exp(exponents.x * depth);
    float neg = -exp(-exponents.y * depth);
    vec2 wDepth = vec2(pos, neg);
    return wDepth;
}

float variance(vec2 moments, float mean, float minVariance) {
    if (mean <= moments.x) {
        return 1.0f;
    } else {
        float variance = moments.y - (moments.x * moments.x);
        variance = max(variance, minVariance);
        float d = mean - moments.x;
        return variance / (variance + (d * d));
    }
}

float calcShadowDepth_simple(int idx, vec3 shadow_coord)
{
    float positiveExponent = PosExp;
    float negativeExponent = NegExp;
    vec2 exponents = vec2(positiveExponent, negativeExponent);

    vec4 moments = sampleShadowSun(idx, shadow_coord.xy).xyzw;
    vec2 posMoments = vec2(moments.x, moments.z);
    vec2 negMoments = vec2(moments.y, moments.w);
    vec2 wDepth = warp(exponents, shadow_coord.z);

    vec2 depthScale = 8.e-4f * exponents * wDepth;
    vec2 minVariance = depthScale * depthScale;
    float posResult = variance(posMoments, wDepth.x, minVariance.x);
    float negResult = variance(negMoments, wDepth.y, minVariance.y);
    return min(posResult, negResult);
}

float calculate_sun_shadow_simple(vec4 worldPosition, float depthZ) {
    int cascadeIndex = int(depthZ < sampleSplitDist(0)) + int(depthZ < sampleSplitDist(1));
    vec4 shadowMapPos = sampleProjView(cascadeIndex) * worldPosition;
    if (abs(shadowMapPos.w) < 1e-5) {
        return 1.0;
    }
    vec3 shadow_coord = (shadowMapPos.xyz / shadowMapPos.w) * 0.5 + 0.5;
    if (shadow_coord.x < 0.0 || shadow_coord.x > 1.0 || shadow_coord.y < 0.0 || shadow_coord.y > 1.0) {
        return 1.0;
    }
    if (shadow_coord.z > 1.0) {
        return 1.0;
    }
    return calcShadowDepth_simple(cascadeIndex, shadow_coord);
}

float calculate_spot_light_shadows_simple(int idx, vec4 frag_worldPosition, vec3 lightPos)
{
    vec4 clip = sampleProjViewSl(idx) * frag_worldPosition;
    float currentDepth = clip.z / sl_far_plane;
    vec3 uv = clip.xyz / clip.w;
    uv = uv * 0.5 + 0.5;
    vec4 vsm = texture(sampleShadowSl(idx), uv.xy);

    return currentDepth <= vsm.r ? 1.0 : 0.0;
}

float calculate_point_light_shadows_simple(samplerCube vsmCubemap, vec3 fragPosition, vec3 lightPos)
{
    vec3 fragToLight = fragPosition - lightPos;
    float currentDepth = length(fragToLight);
    currentDepth /= pl_far_plane;
    vec4 vsm = texture(vsmCubemap, normalize(fragToLight));
    return currentDepth <= vsm.r ? 1.0 : 0.0;
}