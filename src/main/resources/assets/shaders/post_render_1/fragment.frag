in vec2 out_texture;
layout (location = 0) out vec4 frag_color;

uniform sampler2D texture_sampler;
uniform sampler2D blur_sampler;

vec4 hdr(vec4, float, float);

void main()
{
    frag_color = hdr(texture(texture_sampler, out_texture), 2.5, 0.6);
}

vec4 hdr(vec4 in_col, float exposure, float gamma) {
    vec3 rgb = in_col.rgb;
    vec4 blurSampler = texture(blur_sampler, out_texture);
    vec3 bl_c = blurSampler.rgb;
    rgb += bl_c;
    vec3 mapped = vec3(1.) - exp(-rgb * exposure);
    mapped = pow(mapped, vec3(1.0 / gamma));
    return vec4(mapped, in_col.a);
}