#extension GL_ARB_bindless_texture : require

in mat3 TBN;
in vec4 out_model_position;

in vec2 texture_coordinates;
in vec3 m_vertex_normal;
in vec3 mv_vertex_normal;
in vec3 mv_vertex_pos;
in flat uint matertial_id;

layout (location = 0) out vec4 gPosition;
layout (location = 1) out vec4 gNormal;
layout (location = 2) out vec4 gColor;
layout (location = 3) out vec4 gEmission;
layout (location = 4) out vec4 gSpecular;

struct Properties {
    float alpha_discard;
    int texturing_code;
    int lighting_code;
};

struct Material {
    vec4 diffuse_color;
    int diffuse_map_id;
    int normals_map_id;
    int emissive_map_id;
    int specular_map_id;
    int metallic_map_id;
};

layout(std430, binding = 2) buffer BindlessTextures {
    sampler2D textures[1024];
};

layout(std430, binding = 3) buffer MaterialsData {
    Material materials[256];
};

layout(std430, binding = 4) buffer RenderPropertiesData {
    Properties properties[1024];
};

bool checkCode(int i1, int i2) {
    int i3 = i1 & i2;
    return bool(i3 != 0);
}

void main()
{
    Material mat = materials[matertial_id];

    vec3 normals = mv_vertex_normal;
    gNormal = vec4(normals, 1.0);
    gPosition = vec4(mv_vertex_pos, 1.0);
    if (mat.diffuse_color.a > 0) {
        gColor = vec4(mat.diffuse_color.rgb, 1.0);
    } else {
        gColor = texture2D(textures[mat.diffuse_map_id], texture_coordinates);
    }
    gEmission = vec4(vec3(0.0), 1.0);
    gSpecular = vec4(vec3(0.0), 1.0);
}