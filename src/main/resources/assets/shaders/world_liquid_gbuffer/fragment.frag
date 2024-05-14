in mat3 TBN;
in vec4 out_model_position;

in vec2 texture_coordinates;
in vec3 m_vertex_normal;
in vec3 mv_vertex_normal;
in vec3 mv_vertex_pos;

uniform vec2 texture_scaling;

uniform sampler2D diffuse_map;
uniform sampler2D normals_map;

uniform int use_cubemap;
uniform int use_normals;
uniform vec3 camera_pos;

layout (location = 0) out vec4 gPosition;
layout (location = 1) out vec4 gNormal;
layout (location = 2) out vec4 gColor;
layout (location = 5) out vec4 gMetallic;

layout (std140, binding = 2) uniform Misc {
    float w_tick;
};

vec2 scaled_coordinates() {
    return (texture_coordinates + w_tick * 0.05) * texture_scaling;
}

bool checkCode(int i1, int i2) {
    int i3 = i1 & i2;
    return bool(i3 != 0);
}

vec3 calc_normal_map() {
    vec3 normal = texture(normals_map, scaled_coordinates()).rgb;
    normal = normalize(normal * 2.0 - 1.0);
    normal = normalize(TBN * normal);
    return normal;
}

void main()
{
    vec4 diffuse_texture = texture(diffuse_map, scaled_coordinates());

    vec4 diffuse = diffuse_texture;

    vec3 normals = normalize((use_normals == 1) ? calc_normal_map() : mv_vertex_normal);
    gNormal = vec4(normals, 1.0);

    bool b1 = (use_cubemap == 1);
    gMetallic = b1 ? vec4(0.5) : vec4(0.0);
    gPosition = vec4(mv_vertex_pos, 1.0);
    gColor = diffuse;
}