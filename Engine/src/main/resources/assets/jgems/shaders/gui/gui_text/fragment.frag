#extension GL_ARB_bindless_texture : require
precision highp float;
precision highp int;

layout (location = 0) out vec4 frag_color;
in vec2 uv_coordinates;

uniform vec4 color;
uniform uvec2 texture_map;

void main()
{
  frag_color = color * texture(sampler2D(texture_map), uv_coordinates);
}
