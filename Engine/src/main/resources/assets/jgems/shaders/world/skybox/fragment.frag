in vec3 out_texture_cube;

layout (location = 0) out vec4 frag_color;
layout (location = 1) out vec4 bright_color;

uniform sampler2D skybox_background_sampler;

uniform mat4 view_mat_inverted;
uniform samplerCube skybox;
uniform bool covered_by_fog;

layout (std140, binding = 0) uniform SunLight {
    vec4 sunPos;
    vec4 sunColor;
    vec2 sunMeta;
};

layout (std140, binding = 3) uniform Fog {
    vec4 fogColor;
    float fogDensity;
};

void main()
{
    vec4 diffuse = texture(skybox, out_texture_cube);

    vec3 sunDirection = (view_mat_inverted * vec4(normalize(sunPos.xyz), 0.0)).rgb;

    float scos = dot(normalize(out_texture_cube), sunDirection);
    float sunFactor = pow(smoothstep(0.98, 1.0, scos), 32.);

    vec4 color = vec4(fogColor.xyz, 1.0);

    float fogFactor = fogDensity * 100.0;
    float f = covered_by_fog ? clamp(fogFactor, 0.0, 1.0) : 0.0;

    vec2 texel_size = textureSize(skybox_background_sampler, 0);
    vec4 background = texture(skybox_background_sampler, gl_FragCoord.xy / texel_size);

    vec3 sunEffect = sunColor.xyz * sunMeta.y * sunFactor;
    vec4 tex2d_colors = vec4((color.rgb * f) + (diffuse.rgb * (1.0 - f) * 2.) + sunEffect, 1.0);
    frag_color = background + tex2d_colors * (1. - background.a);

    bright_color = vec4(sunEffect, 1.) * (1. - background.a);
}
