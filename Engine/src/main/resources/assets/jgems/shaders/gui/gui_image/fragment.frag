layout (location = 0) out vec4 frag_color;
in vec2 out_texture;

uniform sampler2D texture_sampler;

void main()
{
    vec4 tex = texture(texture_sampler, out_texture);
    frag_color = tex;
}
