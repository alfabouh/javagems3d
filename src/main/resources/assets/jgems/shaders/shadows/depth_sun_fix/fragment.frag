layout (location = 0) out vec4 frag_color;

uniform sampler2D texture_sampler;
in vec2 out_texture;

void main()
{
    vec4 v = texture(texture_sampler, out_texture);
    frag_color = v.r <= 0 ? vec4(1., 1., 0., 0.) : v;
}