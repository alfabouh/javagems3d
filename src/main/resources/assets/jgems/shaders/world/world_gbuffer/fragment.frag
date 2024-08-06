in mat3 TBN;
in vec4 out_model_position;

in vec2 texture_coordinates;
in vec3 m_vertex_normal;
in vec3 mv_vertex_normal;
in vec3 mv_vertex_pos;

uniform float alpha_discard;

uniform vec4 diffuse_color;
uniform sampler2D diffuse_map;

uniform samplerCube ambient_cubemap;

uniform sampler2D normals_map;
uniform sampler2D emissive_map;
uniform sampler2D specular_map;
uniform sampler2D metallic_map;

const int diffuse_code = 1 << 2;
const int emissive_code = 1 << 3;
const int metallic_code = 1 << 4;
const int normals_code = 1 << 5;
const int specular_code = 1 << 6;

const int light_bright_code = 1 << 2;

uniform int texturing_code;
uniform int lighting_code;
uniform vec3 camera_pos;

layout (location = 0) out vec4 gPosition;
layout (location = 1) out vec4 gNormal;
layout (location = 2) out vec4 gColor;
layout (location = 3) out vec4 gEmission;
layout (location = 4) out vec4 gSpecular;
layout (location = 5) out vec4 gMetallic;

bool checkCode(int i1, int i2) {
    int i3 = i1 & i2;
    return bool(i3 != 0);
}

vec3 calc_normal_map() {
    vec3 normal = texture(normals_map, texture_coordinates).rgb;
    normal = normalize(normal * 2.0 - 1.0);
    normal = normalize(TBN * normal);
    return normal;
}

vec4 refract_cubemap(vec3 normal, float cnst) {
    float ratio = 1.0 / cnst;
    vec3 I = normalize(out_model_position.xyz - camera_pos);
    vec3 R = refract(I, normalize(normal), ratio);
    return vec4(texture(ambient_cubemap, R).rgb, 1.0);
}

void main()
{
    vec4 diffuse_texture = texture(diffuse_map, texture_coordinates);
    vec4 emissive_texture = texture(emissive_map, texture_coordinates);

    vec4 diffuse = checkCode(texturing_code, diffuse_code) ? diffuse_texture : diffuse_color;

    if (alpha_discard > 0 && diffuse.a < alpha_discard) {
        discard;
    }

    diffuse += vec4(1.0 - diffuse.a) * alpha_discard + vec4(1.0 - diffuse.a) * diffuse;

    vec3 normals = normalize(checkCode(texturing_code, normals_code) ? calc_normal_map() : mv_vertex_normal);
    gNormal = vec4(normals, 1.0);

    gPosition = vec4(mv_vertex_pos, 1.0);
    gColor = diffuse;
    gEmission = checkCode(lighting_code, light_bright_code) ? vec4(1.0) : checkCode(texturing_code, emissive_code) ? emissive_texture : vec4(vec3(0.0), 1.0);
    gSpecular = checkCode(texturing_code, specular_code) ? texture(specular_map, texture_coordinates) : vec4(vec3(0.0), 1.0);
    gMetallic = (checkCode(texturing_code, metallic_code) ? texture(metallic_map, texture_coordinates) : vec4(vec3(0.0), 1.0)) * refract_cubemap(m_vertex_normal, 1.73);
}