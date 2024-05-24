layout (location = 0) out vec4 frag_color;

in vec2 out_texture;
in vec4 out_color;

uniform sampler2D texture_sampler;

void main()
{
    frag_color = out_color * texture(texture_sampler, out_texture);
}
