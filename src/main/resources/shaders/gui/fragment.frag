layout (location = 0) out vec4 frag_color;
in vec2 out_texture;

uniform vec4 colour;
uniform sampler2D texture_sampler;
uniform int use_texture;

void main()
{
    frag_color = colour * texture(texture_sampler, out_texture);
}
