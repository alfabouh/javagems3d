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

void ESM() {
    //  float d = gl_FragCoord.z;
    //
    //  float depth1 = exp(80. * d);
    //  float depth2 = exp(80. * d * d);
    //
    //  frag_color0 = vec4(depth1, depth2, 0., 0.);

    float positiveExponent = 80.0f;
    float negativeExponent = 5.0f;
    float depth = gl_FragCoord.z;
    vec2 exponents = vec2(positiveExponent, negativeExponent);
    depth = 2.0f * depth - 1.0f;
    float pos = exp(exponents.x * depth);
    float neg = -exp(-exponents.y * depth);
    vec2 warpDepth = vec2(pos, neg);
    frag_color0 = vec4(warpDepth, warpDepth * warpDepth);
}

void main()
{
    vec4 v = !use_texture ? vec4(1.0) : texture(texture_sampler, out_texture);
    if (v.a < alpha_discard) {
        discard;
    }

    ESM();
}