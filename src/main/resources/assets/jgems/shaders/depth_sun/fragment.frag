layout (location = 0) out vec4 frag_color0;

uniform float alpha_discard;
uniform sampler2D texture_sampler;
uniform bool use_texture;
in vec2 out_texture;

void main()
{
    vec4 v = !use_texture ? vec4(1.0) : texture(texture_sampler, out_texture);
    if (v.a < alpha_discard) {
        discard;
    }

    float d = gl_FragCoord.z;
    float dx = dFdx(d);
    float dy = dFdy(d);
    float moment2 = d * d + 0.25 * (dx * dx + dy * dy);
    frag_color0 = vec4(d, moment2, 0.0, 1.0);
}