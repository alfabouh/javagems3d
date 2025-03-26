#extension GL_ARB_bindless_texture : require

in vec3 uv_coordinates_cube;

layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 bright_color;

struct Sun {
    vec3 position;
    float _padding0;
    vec3 color;
    float ambient;
    float brightness;
};
layout (std430, binding = 5) buffer SunLight {
    Sun sun;
};

struct Fog {
    vec3 color;
    float density;
};
layout (std430, binding = 7) buffer WorldFog {
    Fog fog;
};

uniform sampler2D skybox_background;
uniform samplerCube skybox_cube;
uniform mat4 view_mat_inverted;
uniform bool covered_by_fog;

void main()
{
    const float brightness = sun.brightness * 2.;

    vec4 diffuse = texture(skybox_cube, uv_coordinates_cube);

    vec3 sunDirection = (view_mat_inverted * vec4(normalize(sun.position), 0.0)).rgb;

    float scos = dot(normalize(uv_coordinates_cube), sunDirection);
    float sunFactor = pow(smoothstep(0.98, 1.0, scos), 32.);

    vec4 color = vec4(sun.color, 1.0);

    float fogFactor = fog.density * 100.0;
    float f = covered_by_fog ? clamp(fogFactor, 0.0, 1.0) : 0.0;

    vec2 texel_size = textureSize(skybox_background, 0);
    vec4 background = texture(skybox_background, gl_FragCoord.xy / texel_size);

    vec3 sunEffect = color.xyz * brightness * sunFactor;
    vec4 tex2d_colors = vec4((color.rgb * f) + (diffuse.rgb * (1.0 - f) * brightness) + sunEffect, 1.0);
    frag_color = background + tex2d_colors * (1. - background.a);

    bright_color = vec4(sunEffect, 1.) * (1. - background.a);
}