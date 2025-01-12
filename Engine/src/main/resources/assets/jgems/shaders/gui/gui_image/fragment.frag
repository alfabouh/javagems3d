layout (location = 0) out vec4 frag_color;
in vec2 uv_coordinates;

uniform sampler2D texture_sampler;

void main()
{
    vec4 tex = texture(texture_sampler, uv_coordinates);
    frag_color = tex;
}
