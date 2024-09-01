in vec2 out_texture;

layout (location = 0) out vec4 frag_color;

uniform sampler2D texture_sampler;

uniform sampler2D accumulated_alpha;
uniform sampler2D reveal_alpha;

bool isApproximatelyEqual(float a, float b) {
    return abs(a - b) <= (abs(a) < abs(b) ? abs(b) : abs(a)) * 1.0e-8f;
}

float max3(vec3 v) {
    return max(max(v.x, v.y), v.z);
}

void main() {
    vec4 accum = texture(accumulated_alpha, out_texture);
    float reveal = texture(reveal_alpha, out_texture).r;

    accum.rgb = isinf(max3(abs(accum.rgb))) ? vec3(accum.a) : accum.rgb;

    vec3 accumColor = accum.rgb / max(accum.a, 1e-5f);
    vec4 mixedTransparency = vec4(accumColor, 1. - reveal);

    mixedTransparency = isApproximatelyEqual(reveal, 1.0f) ? vec4(0.) : mixedTransparency;

    vec4 sceneColor = texture(texture_sampler, out_texture) * (1.0 - mixedTransparency.a) + mixedTransparency * mixedTransparency.a;

    frag_color = sceneColor;
}