layout (location = 0) out vec3 frag_color;

uniform sampler2D gPositions;
uniform sampler2D gNormals;
uniform sampler2D gMetallicRoughness;
uniform mat4 view_mat_inverted;

#include "/assets/shaders/libs/shadows"
#include "/assets/shaders/libs/lighting"

uniform PointLight plight;
in vec4 box_model_frag_pos;

void main()
{
    vec3 ndc = box_model_frag_pos.xyz / box_model_frag_pos.w;
    vec2 uv_coordinates = ndc.xy * 0.5 + 0.5;

    vec3 frag_pos = texture(gPositions, uv_coordinates).xyz;
    vec3 normal = texture(gNormals, uv_coordinates).xyz;
    vec2 metallic_roughness = texture(gMetallicRoughness, uv_coordinates).rg;

    vec4 view_pos = vec4(frag_pos, 1.0);
    vec4 world_position = view_mat_inverted * view_pos;
    world_position /= world_position.w;

    float p_brightness = plight.brightness;
    vec3 params = getParams(p_brightness);
    float p_id = plight.attachedShadowSceneId;
    float shadow = p_id >= 0 ? calculate_point_light_shadows(sampleShadowPl(int(p_id)), world_position.xyz, plight.position.xyz) : 1.;
    frag_color = calc_point_light(plight, frag_pos, normal, params.x, params.y, params.z, p_brightness, metallic_roughness.g) * shadow;
}