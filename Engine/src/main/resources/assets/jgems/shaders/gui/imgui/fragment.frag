#extension GL_ARB_bindless_texture : require

layout (location = 0) out vec4 frag_color;

in vec2 uv_coordinates;
in vec4 out_color;

uniform uvec2 texture_bindless;

void main()
{
    frag_color = out_color * texture(sampler2D(texture_bindless), uv_coordinates);
}
