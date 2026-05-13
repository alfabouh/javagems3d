vec3 calc_light(mat4 viewMat, vec4 world_position, float zDepth) {
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

    vec3 spot_light_factor = vec3(0.0);
    for (int i = 0; i < total_slights; i++) {
        SpotLight s = s_l[i];
        //vec3 delta = p.view_position - frag_pos;
        //float distSq = dot(delta, delta);
        //if (distSq <= p.clipRadius * p.clipRadius) {
        float s_brightness = s.brightness;
        float s_id = s.attachedShadowSceneId;
        float shadow = s_id >= 0 ? calculate_spot_light_shadows_simple(int(s_id), world_position, s.position.xyz) : 1.;
        spot_light_factor += calc_spot_light_simple(viewMat, s, particle_coord) * shadow;
        //}
    }

    float brightness = dot(point_light_factor.rgb + spot_light_factor.rgb, vec3(0.2126, 0.7152, 0.0722)) * 5.0;
    lightFactors += sun.brightness * clamp(sun_shadow, 0.0, 1.0);
    lightFactors += point_light_factor;
    lightFactors += spot_light_factor;

    return lightFactors;
}