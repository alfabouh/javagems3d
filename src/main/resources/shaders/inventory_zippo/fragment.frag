layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 frag_color2;
in vec2 texture_coordinates;

uniform sampler2D diffuse_map;
uniform samplerCube ambient_cubemap;
uniform sampler2D emissive_map;
in vec3 mv_vertex_normal;
in vec3 mv_vertex_pos;

void main()
{
    frag_color = texture(diffuse_map, 1.0 - texture_coordinates);
    vec4 evec4 = texture(emissive_map, 1.0 - texture_coordinates);
    frag_color2 = (evec4.a <= 0 || (evec4.x + evec4.y + evec4.z) <= 0.0) ? vec4(0.0, 0.0, 0.0, frag_color.a) : vec4(evec4.xyz * evec4.a, 1.0);
}