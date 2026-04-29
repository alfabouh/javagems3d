#extension GL_ARB_bindless_texture : require
precision highp float;
precision highp int;

in vec2 uv_coordinates;
in vec4 frag_pos;

layout (location = 0) out vec4 frag_color0;

uniform vec3 lightPos;
uniform float far_plane;

in flat uint matertial_id;
in flat uint ent_id;

struct Properties {
    float alpha_discard;
};

struct Material {
    vec4 diffuse_color;
    vec3 emission_color;
    float _padding000; //PADDING
    float metallic_factor;
    float roughness_factor;
    int diffuse_map_id;
    int normals_map_id;
    int emission_map_id;
    int metallic_roughness_map_id;
    int texturing_code;
};

layout(std430, binding = 2) buffer BindlessTextures {
    uvec2 textures[CONST.MAX_BINDLESS_TEXTURES];
};

layout(std430, binding = 3) buffer MaterialsData {
    Material materials[256];
};

layout(std430, binding = 4) buffer RenderPropertiesData {
    Properties properties[512];
};

const int diffuse_code = 1 << 2;

bool checkCode(int i1, int i2) {
    int i3 = i1 & i2;
    return bool(i3 != 0);
}

void main()
{
    Material mat = materials[matertial_id];
    Properties property = properties[ent_id];
    float alpha_discard = property.alpha_discard;

    float diffuse_a = mat.diffuse_color.a;
    if (checkCode(mat.texturing_code, diffuse_code)) {
        diffuse_a *= texture(sampler2D(textures[mat.diffuse_map_id]), uv_coordinates).a;
    }
    if (diffuse_a < alpha_discard) {
        discard;
    }

    float lightDistance = length(frag_pos.xyz - lightPos);
    lightDistance /= far_plane;

    float d = lightDistance;
    float dx = dFdx(d);
    float dy = dFdy(d);
    float moment2 = d * d + 0.25 * (dx * dx + dy * dy);
    frag_color0 = vec4(d, moment2, 0.0, 1.0);
}