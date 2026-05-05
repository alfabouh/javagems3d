#extension GL_ARB_bindless_texture : require
precision highp float;
precision highp int;

layout (location = 0) out vec4 frag_color0;
layout (location = 1) out vec4 bright_color0;

in vec2 uv_coordinates;
in flat uint ent_id;
in vec3 lightFactor;
in vec4 frag_pos;

struct RenderData {
    vec4 diffuse_color;
    vec3 emission_color;
    int textureId;
    float emissionStrength;
    float alpha_discard;
    uvec2 diffusion_map;
    ivec2 cellsXY;
};

layout(std430, binding = 31) buffer ParticleRenderDataArray {
    RenderData renderData[CONST.MAX_PARTICLE_INDIRECT_RENDERING_DATA];
};

#include "/assets/shaders/libs/fog"

void main()
{
    RenderData enRenderData = renderData[ent_id];

    vec4 textureDiffuse = texture(sampler2D(enRenderData.diffusion_map), uv_coordinates);
    vec3 color = textureDiffuse.rgb * enRenderData.diffuse_color.xyz;
    color *= enRenderData.emissionStrength + lightFactor * (1. - enRenderData.emissionStrength);

    float alpha_discard = enRenderData.alpha_discard;
    frag_color0 = vec4(color, 1.);
    frag_color0 = calc_fog(frag_pos.xyz, frag_color0, 1.);

    if (textureDiffuse.a * enRenderData.diffuse_color.a < enRenderData.alpha_discard) {
        discard;
    }

    float brightness = dot(frag_color0.rgb, vec3(0.2126, 0.7152, 0.0722));
    bright_color0 = (brightness >= 1.75 ? vec4(frag_color0.xyz, 1.) : vec4(0., 0., 0., 1.)) + vec4((vec4(enRenderData.emission_color, 0.)) * enRenderData.emissionStrength);
}