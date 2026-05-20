#extension GL_ARB_bindless_texture : require
precision highp float;
precision highp int;

layout (location = 0) out vec4 accumulated;
layout (location = 1) out float reveal;
layout (location = 2) out vec4 bright_color;

in vec2 uv_coordinates;
in flat uint ent_id;
in vec3 lightFactor;
in vec4 frag_pos;

struct RenderData {
    vec4 diffuse_color;
    vec3 emissive_color;
    int textureId;
    float emissionStrength;
    float alpha_discard;
    uvec2 diffuse_map;
    ivec2 cellsXY;
    float interpolation;
    int nextFrameId;
};

layout(std430, binding = 31) readonly restrict buffer ParticleRenderDataArray {
    RenderData renderData[CONST.MAX_PARTICLE_INDIRECT_RENDERING_DATA];
};

#include "/assets/shaders/libs/fog"
#include "/assets/shaders/libs/oit"

vec2 pickFrame(RenderData enRenderData, int id) {
    vec2 grid = vec2(enRenderData.cellsXY);
    vec2 id_uv = vec2(float(id % int(grid.x)), float(id / int(grid.x)));
    vec2 base_uv = uv_coordinates / grid;
    vec2 UV = base_uv + id_uv / grid;
    return UV;
}

void main()
{
    RenderData enRenderData = renderData[ent_id];
    vec2 UV = pickFrame(enRenderData, enRenderData.textureId);
    vec2 UV2 = pickFrame(enRenderData, enRenderData.nextFrameId);
    vec4 textureDiffuse = texture(sampler2D(enRenderData.diffuse_map), UV);
    vec4 textureDiffuseInterpolation = texture(sampler2D(enRenderData.diffuse_map), UV2);

    vec4 diffuseInterpolated = mix(textureDiffuse, textureDiffuseInterpolation, enRenderData.interpolation);

    vec3 color = diffuseInterpolated.rgb * enRenderData.diffuse_color.xyz;
    color *= (lightFactor * (1. - enRenderData.emissionStrength) + enRenderData.emissionStrength);

    vec4 frag_color0 = vec4(color, diffuseInterpolated.a * enRenderData.diffuse_color.a);
    frag_color0 = calc_fog(frag_pos.xyz, frag_color0, 1.);

    accumulated = calc_accumulated(frag_color0);
    reveal = calc_alpha(frag_color0);
    reveal = calc_fog_float(frag_pos.xyz, frag_color0.a);

    float brightness = dot(frag_color0.rgb, vec3(0.2126, 0.7152, 0.0722));
    bright_color = (brightness >= 1.75 ? vec4(frag_color0.xyz, 1.) : vec4(vec3(frag_color0.xyz * enRenderData.emissive_color) * enRenderData.emissionStrength * calc_fog_float(frag_pos.xyz, 1.), 1.));
    if (textureDiffuse.a < 0.1) {
        bright_color = vec4(0.);
    }
}