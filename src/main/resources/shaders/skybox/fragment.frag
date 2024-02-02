in vec3 out_texture;
layout (location = 0) out vec4 frag_color;
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
    frag_color = vec4(texture(cube_map_sampler, out_texture)) * sunBright;
}
