#extension GL_ARB_bindless_texture : require

layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 bright_color;

void main()
{
    frag_color = vec4(0.);
    bright_color = vec4(0.);
}