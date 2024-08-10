in vec3 out_texture;
layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 bright_color;

uniform mat4 view_mat_inverted;
uniform samplerCube skybox;
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
    vec4 diffuse = texture(skybox, out_texture);

    vec3 sunDirection = (view_mat_inverted * vec4(normalize(vec3(sunX, sunY, sunZ)), 0.0)).rgb;
    vec3 sunColor = vec3(sunColorR, sunColorG, sunColorB);
    float sunBrightness = sunBright;

    float cos = dot(normalize(out_texture), sunDirection);
    float sunFactor = pow(smoothstep(0.98, 1.0, cos), 32.);

    vec4 color = vec4(vec3(fogColorR, fogColorG, fogColorB) * sunColor, 1.0);

    float fogFactor = fogDensity * 100.0;
    float f = covered_by_fog ? clamp(fogFactor, 0.0, 1.0) : 0.0;

    vec3 sunEffect = sunColor * sunBrightness * sunFactor;
    frag_color = vec4((color.rgb * f) + (diffuse.rgb * (1.0 - f) * 2.) + sunEffect, 1.0);

    bright_color = vec4(sunEffect, 1.);
}
