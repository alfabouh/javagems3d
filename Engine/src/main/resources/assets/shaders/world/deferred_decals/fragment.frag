layout (location = 2) out vec4 gColorOut;
layout (location = 3) out vec3 gEmissionOut;

uniform sampler2D gPositions;
uniform sampler2D gTexture;
uniform sampler2D gEmission;
uniform sampler2D gNormals;
uniform sampler2D gObjLayersID;

in vec2 uv_coordinates;

#include "/assets/shaders/libs/deferred_decals_calc"

void main()
{
    vec4 frag_pos = texture(gPositions, uv_coordinates);
    vec4 g_texture = texture(gTexture, uv_coordinates);
    vec3 emission = texture(gEmission, uv_coordinates).rgb;
    vec3 normal = texture(gNormals, uv_coordinates).rgb;
    float objLayer = texture(gObjLayersID, uv_coordinates).r;

    gColorOut = g_texture;
    gEmissionOut = emission;
    if (int(objLayer * 255.) * min(decal_ent_layerID, 1) == decal_ent_layerID) {
        vec3 localDecalCoord = calcDecalCoord(frag_pos);
        if (abs(localDecalCoord.x) > 1.) {
            discard;
        }
        if (abs(localDecalCoord.y) > 1.) {
            discard;
        }
        if (abs(localDecalCoord.z) > 1.) {
            discard;
        }
        vec3 d = 1.0 - abs(localDecalCoord);
        float volumeFade = clamp(min(min(d.x, d.y), d.z) / 0.05, 0.0, 1.0);

        vec3 decalNormal = normalize(abs(toDecalNormal(normal)));
        vec3 comps = calcComponents(decalNormal);

        vec4 decalTexture = sampleTriplanarDiffTexture(comps, localDecalCoord);
        gColorOut = mix(gColorOut, decalTexture, decalTexture.a * volumeFade);
        gEmissionOut = gEmissionOut + (decalTexture.rgb * decalTexture.a * emissiveFactor);
    }
}