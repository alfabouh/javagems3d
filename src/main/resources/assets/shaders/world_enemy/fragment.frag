layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 frag_color2;

in vec2 texture_coordinates;
uniform sampler2D diffuse_map;

void main()
{
    vec4 t_col = texture(diffuse_map, vec2(texture_coordinates.x, 1.0 - texture_coordinates.y));
    t_col = pow(t_col, vec4(4.0));
    t_col = vec4(vec3(dot(t_col.rgb, vec3(0.299, 0.587, 0.114))), t_col.a);


    frag_color = t_col;
    frag_color2 = vec4(0.0);
}