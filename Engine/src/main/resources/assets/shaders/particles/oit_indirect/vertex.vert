#extension GL_ARB_bindless_texture : require
precision highp float;
precision highp int;

layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 aTexture;

out vec2 uv_coordinates;
out flat uint ent_id;
out vec3 lightFactor;
out vec4 frag_pos;

uniform mat4 view_matrix;
uniform mat4 projection_matrix;

layout(std430, binding = 30) buffer IndirectBufferData {
    mat4 modelMatrix[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
};

#include "/assets/shaders/libs/shadows_simple"
#include "/assets/shaders/libs/lighting_simple"

vec3 calc_light(vec4 world_position, float zDepth) {
    vec3 particle_coord = world_position.xyz;
    vec3 lightFactors = sun.color * sun.ambient;

    float sun_shadow = calculate_sun_shadow_simple(world_position, zDepth);

    vec3 point_light_factor = vec3(0.0);
    for (int i = 0; i < total_plights; i++) {
        PointLight p = p_l[i];
        vec3 delta = p.view_position - particle_coord;
        float distSq = dot(delta, delta);
        if (distSq <= p.clipRadius * p.clipRadius) {
            float p_id = p.attachedShadowSceneId;
            float shadow = p_id >= 0 ? calculate_point_light_shadows_simple(sampleShadowPl(int(p_id)), world_position.xyz, p.position.xyz) : 1.;
            point_light_factor += calc_point_light_simple(p, particle_coord) * shadow;
        }
    }

    float brightness = dot(point_light_factor.rgb, vec3(0.2126, 0.7152, 0.0722)) * 5.0;
    lightFactors += sun.brightness * clamp(sun_shadow, 0.0, 1.0);
    lightFactors += point_light_factor;

    return lightFactors;
}

void main()
{
    uint idx = gl_BaseInstance + gl_InstanceID;
    ent_id = idx;
    mat4 model = modelMatrix[ent_id];

    vec4 world_pos = model * vec4(aPosition, 1.0);
    vec4 view_pos = view_matrix * world_pos;

    frag_pos = view_pos;

    gl_Position = projection_matrix * view_pos;
    lightFactor = calc_light(world_pos, view_pos.z);
    uv_coordinates = aTexture;
}