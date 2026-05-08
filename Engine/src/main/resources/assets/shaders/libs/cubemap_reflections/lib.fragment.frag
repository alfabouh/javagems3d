uniform uvec2 ambient_cubemap;
uniform bool useCubeMap;

vec3 refract_cubemap(vec3 normal, float cnst, vec4 world_position) {
    float ratio = 1.0 / cnst;
    vec3 I = normalize(world_position.xyz - camera_pos);
    vec3 R = refract(I, normalize(normal), ratio);
    return texture(samplerCube(ambient_cubemap), R).rgb;
}
