in vec3 out_texture;
layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 bright_color;
uniform samplerCube cube_map_sampler;

layout (std140, binding = 0) uniform SunLight {
    float ambient;
    float sunBright;
    float sunX;
    float sunY;
    float sunZ;
};

void main()
{
    frag_color = vec4(texture(cube_map_sampler, out_texture)) * max(sunBright, 0.5);

    float brightness = frag_color.r + frag_color.g + frag_color.b;
    bright_color = brightness >= 3.0 ? frag_color : vec4(0., 0., 0., 1.);
}
