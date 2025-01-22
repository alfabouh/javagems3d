layout (location = 0) out vec4 frag_color;
in vec2 uv_coordinates;

uniform vec4 color;
uniform sampler2D texture_sampler;

void main()
{
    frag_color = color * texture(texture_sampler, uv_coordinates);
}
