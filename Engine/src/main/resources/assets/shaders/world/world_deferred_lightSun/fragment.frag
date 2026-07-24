in vec2 uv_coordinates;

layout (location = 0) out vec3 frag_color;

uniform sampler2D gPositions;
uniform sampler2D gNormals;
uniform sampler2D gMetallicRoughness;
uniform mat4 view_mat_inverted;

#include "/assets/shaders/libs/shadows"
#include "/assets/shaders/libs/lighting"

void main()
{
    vec3 frag_pos = texture(gPositions, uv_coordinates).xyz;
    vec3 normal = texture(gNormals, uv_coordinates).xyz;
    vec2 metallic_roughness = texture(gMetallicRoughness, uv_coordinates).rg;

    vec4 view_pos = vec4(frag_pos, 1.0);
    vec4 world_position = view_mat_inverted * view_pos;
    world_position /= world_position.w;

    vec3 lightFactors = vec3(sun.color) * sun.ambient;
    float sun_shadow = calc_sun_shadows(world_position, frag_pos);
    vec3 sunFactor = calc_sun_light(normalize(sun.position), frag_pos, normal, metallic_roughness.g);
    lightFactors += sunFactor * clamp(sun_shadow, 0.0, 1.0);

    frag_color = lightFactors;
}