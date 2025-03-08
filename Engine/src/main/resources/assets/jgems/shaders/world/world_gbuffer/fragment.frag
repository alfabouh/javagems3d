in mat3 TBN;
in vec2 uv_coordinates;
in vec3 modelview_vertex_normal;
in vec3 modelview_vertex_pos;
in vec3 model_vertex_normal;
in vec4 model_vertex_pos;

layout (location = 0) out vec4 gPosition;
layout (location = 1) out vec4 gNormal;
layout (location = 2) out vec4 gColor;
layout (location = 3) out vec4 gEmission;
layout (location = 4) out vec4 gSpecular;

const int diffuse_code = 1 << 2;
const int emission_code = 1 << 3;
const int metallic_code = 1 << 4;
const int normals_code = 1 << 5;
const int specular_code = 1 << 6;
const int light_bright_code = 1 << 2;

uniform bool use_cubemap;

uniform float alpha_discard;
uniform vec4 diffuse_color;
uniform samplerCube ambient_cube_map;
uniform sampler2D diffuse_map;
uniform sampler2D normals_map;
uniform sampler2D emissive_map;
uniform sampler2D specular_map;
uniform sampler2D metallic_map;
uniform int texturing_code;
uniform int lighting_code;
uniform vec3 camera_pos;

bool checkCode(int i1, int i2) {
    int i3 = i1 & i2;
    return bool(i3 != 0);
}

vec3 calc_normal_map() {
    vec3 normal = texture(normals_map, uv_coordinates).rgb;
    normal = normalize(normal * 2.0 - 1.0);
    normal = normalize(TBN * normal);
    return normal;
}

vec3 refract_cubemap(vec3 normal, float cnst) {
    float ratio = 1.0 / cnst;
    vec3 I = normalize(model_vertex_pos.xyz - camera_pos);
    vec3 R = refract(I, normalize(normal), ratio);
    return texture(ambient_cube_map, R).rgb;
}

void main()
{
    vec4 diffuse_texture = texture(diffuse_map, uv_coordinates);
    vec4 emissive_texture = texture(emissive_map, uv_coordinates);

    vec4 diffuse = checkCode(texturing_code, diffuse_code) ? diffuse_texture : diffuse_color;

    if (diffuse.a < alpha_discard) {
        discard;
    }

    diffuse += vec4(1.0 - diffuse.a) * alpha_discard + vec4(1.0 - diffuse.a) * diffuse;

    vec3 normals = normalize(checkCode(texturing_code, normals_code) ? calc_normal_map() : modelview_vertex_normal);
    gNormal = vec4(normals, 1.0);

    gPosition = vec4(modelview_vertex_pos, 1.0);
    gColor = diffuse;
    gEmission = checkCode(lighting_code, light_bright_code) ? vec4(1.0) : checkCode(texturing_code, emission_code) ? emissive_texture : vec4(vec3(0.0), 1.0);
    gSpecular = checkCode(texturing_code, specular_code) ? texture(specular_map, uv_coordinates) : vec4(vec3(0.0), 1.0);

    vec3 metallicColor = texture(metallic_map, uv_coordinates).rgb;
    vec3 refractColor = vec3(1.);
    vec4 gMetallic = checkCode(texturing_code, metallic_code) ? vec4(metallicColor * refractColor, 1.0) : vec4(0.);
    gColor += vec4(gMetallic.xyz, 0.0);
}