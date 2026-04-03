#extension GL_ARB_bindless_texture : enable

layout (location = 0) out vec4 frag_color;

in vec3 normals;
in vec3 pos;

in vec2 uv_texture;
uniform bool use_texture;
uniform uvec2 diffuse_map;
uniform vec4 diffuse_color;

vec4 calc_light_factor(vec3 colors, float brightness, vec3 vPos, vec3 light_dir, vec3 vNormal) {
    vec4 diffuseC = vec4(0.);
    vec4 specularC = vec4(0.);

    float specularF = 0.;
    float diffuseF = max(dot(vNormal, light_dir), 0.);
    diffuseC = vec4(colors, 1.) * brightness * diffuseF;

    vec3 camDir = normalize(-vPos);
    vec3 reflectionF = normalize(light_dir + camDir);
    specularF = max(dot(vNormal, reflectionF), 0.);
    specularF = pow(specularF, 8.0);
    specularC = brightness * specularF * vec4(colors, 1.);

    return diffuseC + specularC + vec4(0.7);
}

vec4 calc_sun_light(vec3 vPos, vec3 vNormal) {
    return calc_light_factor(vec3(1.), 0.95, vPos, normalize(vec3(1, 1, 1)), vNormal);
}

void main()
{
    float f1 = min(uv_texture.y, uv_texture.x) + 0.5;
    frag_color = use_texture ? texture(sampler2D(diffuse_map), uv_texture) : vec4(vec3(f1), 1.0);
    frag_color *= vec4(diffuse_color.rgb, 1.);
    frag_color *= calc_sun_light(pos, normals);
    if (frag_color.a < 0.5) {
        frag_color = vec4(vec3(1., 0., 1.), 1.);
    }
}