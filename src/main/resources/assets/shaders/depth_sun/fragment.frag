layout (location = 0) out vec4 frag_color0;

uniform sampler2D texture_sampler;
in vec2 out_texture;

void main()
{
    vec4 v = texture(texture_sampler, out_texture);
    if (v.a < 0.5) {
       // discard;
    }

    float d = gl_FragCoord.z;
    float dx = dFdx(d);
    float dy = dFdy(d);
    float moment2 = d * d + 0.25 * (dx * dx + dy * dy);
    frag_color0 = vec4(d, moment2, 0.0, 0.0);
}