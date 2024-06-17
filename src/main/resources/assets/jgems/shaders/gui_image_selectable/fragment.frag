layout (location = 0) out vec4 frag_color;
in vec2 out_texture;

uniform sampler2D texture_sampler;
uniform bool selected;

void main()
{
    vec4 sel = selected ? vec4(vec3(0.5), 1.0) : vec4(1.0);
    frag_color = texture(texture_sampler, out_texture) * sel;
}
