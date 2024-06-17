in vec3 out_texture;
layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 bright_color;
uniform samplerCube cube_map_sampler;
uniform bool covered_by_fog;

layout (std140, binding = 0) uniform SunLight {
    float ambient;
    float sunBright;
    float sunX;
    float sunY;
    float sunZ;
    float sunColorR;
    float sunColorG;
    float sunColorB;
};

layout (std140, binding = 3) uniform Fog {
    float fogDensity;
    float fogColorR;
    float fogColorG;
    float fogColorB;
};

void main()
{
    vec4 diffuse = texture(cube_map_sampler, out_texture);
    vec4 color = vec4(vec3(fogColorR, fogColorG, fogColorB) * vec3(sunColorR, sunColorG, sunColorB), 1.0);

    float fogFactor = fogDensity * 100;
    float f = covered_by_fog ? clamp(fogFactor, 0.0, 1.0) : 0.0;

    frag_color = (color * f) + (diffuse * (1.0 - f));

    bright_color = vec4(0.0);
}
