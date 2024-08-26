layout (location = 0) out vec4 frag_color;
in vec2 texture_coordinates;

uniform sampler2D diffuse_map;

void main()
{
    frag_color = texture(diffuse_map, texture_coordinates);
}