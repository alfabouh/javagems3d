in vec2 out_texture;
layout (location = 0) out vec4 frag_color;

uniform sampler2D texture_sampler;
uniform sampler2D blur_sampler;
uniform int post_mode;

vec4 negate_c(vec4);
vec4 hdr(vec4, float, float);
vec4 choose_mode(int);
vec4 simple_texture();
vec4 negative_texture();
vec4 ps1_test();
vec4 ps1_test2();

layout (std140, binding = 2) uniform Misc {
    float w_tick;
};

float rand(vec2 co)
{
    return fract(sin(dot(co.xy + w_tick, vec2(12.9898, 78.233) + w_tick)) * 43758.5453 + w_tick);
}

void main()
{
    frag_color = hdr(choose_mode(post_mode), 2.5, 0.6) * min(w_tick, 1.0);
}

vec4 hdr(vec4 in_col, float exposure, float gamma) {
    vec3 rgb = in_col.rgb;
    vec4 blurSampler = texture(blur_sampler, out_texture);
    vec3 bl_c = blurSampler.rgb;
    rgb += bl_c;
    vec3 mapped = vec3(1.) - exp(-rgb * exposure);
    mapped = pow(mapped, vec3(1.0 / gamma));
    return vec4(mapped, in_col.w);
}

vec4 choose_mode(int i) {
    return i == 0 ? simple_texture() : i == 1 ? negative_texture() : i == 2 ? ps1_test() : i == 3 ? ps1_test2() : vec4(1, 0, 0, 1);
}

vec4 simple_texture() {
    return texture(texture_sampler, out_texture);
}

vec4 negative_texture() {
    return negate_c(simple_texture());
}

vec4 ps1_test() {
    vec2 tex = gl_FragCoord.xy / textureSize(texture_sampler, 0);
    vec4 colors = simple_texture();
    float grain = rand(tex) * 0.25;
    return simple_texture() + vec4(grain, grain, grain, 1.);
}

vec4 ps1_test2() {
    vec4 tex = simple_texture();
    float average = (tex.r + tex.g + tex.b) / 3.0;
    vec3 retroColor = vec3(average, average * 0.7, average * 0.4);
    return vec4(retroColor, tex.a);
}

vec4 negate_c(vec4 in_col) {
    return vec4(1.) - in_col;
}