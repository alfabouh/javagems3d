in vec2 out_texture;
out vec4 frag_color;

uniform sampler2D texture_sampler;

void main()
{
    frag_color = texture(texture_sampler, out_texture);
}
