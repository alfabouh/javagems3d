#extension GL_ARB_bindless_texture : require


layout (location = 0) out vec4 frag_color0;
layout (location = 1) out vec4 bright_color0;

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

layout(std430, binding = 13) readonly restrict buffer ParticleRenderDataArray {
    RenderData renderData[CONST.MAX_PARTICLE_INDIRECT_RENDERING_DATA];
};

#include "/assets/shaders/libs/fog"

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
    color *= (lightFactor * (1. - min(enRenderData.emissionStrength, 1.)) + min(enRenderData.emissionStrength, 1.));

    float alpha_discard = enRenderData.alpha_discard;
    frag_color0 = vec4(color, 1.);
    frag_color0 = calc_fog(frag_pos.xyz, frag_color0, 1.);

    if (diffuseInterpolated.a * enRenderData.diffuse_color.a < enRenderData.alpha_discard) {
        discard;
    }

    float emissionConst = 1.75 - min(enRenderData.emissionStrength, 0.75);
    float brightness = dot(vec3(frag_color0.xyz * textureDiffuse.a), vec3(0.2126, 0.7152, 0.0722));
    bright_color0 = (brightness >= emissionConst ? vec4(frag_color0.xyz, textureDiffuse.a) : vec4(vec3(0.), textureDiffuse.a));
    bright_color0.rgb = bright_color0.rgb * enRenderData.emissionStrength * calc_fog_float(frag_pos.xyz, 1.);
}