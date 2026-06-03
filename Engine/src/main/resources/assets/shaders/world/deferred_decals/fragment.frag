layout (location = 2) out vec4 gColorOut;
layout (location = 3) out vec3 gEmissionOut;

uniform sampler2D gPositions;
uniform sampler2D gNormals;
uniform sampler2D gObjLayersID;

in vec4 box_model_frag_pos;

#include "/assets/shaders/libs/deferred_decals_calc"

void main()
{
    vec3 ndc = box_model_frag_pos.xyz / box_model_frag_pos.w;
    vec2 uv_coordinates = ndc.xy * 0.5 + 0.5;

    vec4 frag_pos = texture(gPositions, uv_coordinates);
    vec3 normal = texture(gNormals, uv_coordinates).rgb;
    ivec2 tex_size = textureSize(gObjLayersID, 0);
    ivec2 texel_coord = ivec2(uv_coordinates * vec2(tex_size));
    texel_coord = clamp(texel_coord, ivec2(0), tex_size - 1);
    float objLayer = texelFetch(gObjLayersID, texel_coord, 0).r;

    gColorOut = vec4(0.);
    gEmissionOut = vec3(0.);
    if (objLayer * min(decal_ent_layerID, 1) == float(decal_ent_layerID / 65535.))
    {
        vec3 localDecalCoord = calcDecalCoord(frag_pos);
       // if (abs(localDecalCoord.x) > 1.) {
       //     discard;
       // }
       // if (abs(localDecalCoord.y) > 1.) {
       //     discard;
       // }
       // if (abs(localDecalCoord.z) > 1.) {
       //     discard;
       // }
        vec3 d = 1.0 - abs(localDecalCoord);
        float volumeFade = clamp(min(min(d.x, d.y), d.z) / 0.05, 0.0, 1.0);

        vec3 decalNormal = normalize(abs(toDecalNormal(normal)));
        vec3 comps = calcComponents(decalNormal);

        vec4 decalTexture = sampleTriplanarDiffTexture(comps, localDecalCoord);
        decalTexture.a *= volumeFade;

        gColorOut = decalTexture;
        gEmissionOut = (decalTexture.rgb * decalTexture.a * emissiveFactor);
    }
}