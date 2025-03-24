#extension GL_ARB_bindless_texture : require

layout (location = 0) out vec4 frag_color;
in vec2 uv_coordinates;

uniform uvec2 texture_bindless;

void main()
{
    vec4 tex = texture(sampler2D(texture_bindless), uv_coordinates);
    frag_color = tex;
}
