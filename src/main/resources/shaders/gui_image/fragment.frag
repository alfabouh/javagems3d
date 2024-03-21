layout (location = 0) out vec4 frag_color;
in vec2 out_texture;

uniform sampler2D texture_sampler;

void main()
{
    frag_color = texture(texture_sampler, out_texture);
}
