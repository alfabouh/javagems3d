vec3 calc_light(vec3 frag_pos, vec3 normal, float specularFactor, vec4 frag_world_position) {
    vec3 lightFactors = vec3(sun.color) * sun.ambient;
    vec3 position = normalize(sun.position);

    float sun_shadow = calc_sun_shadows(frag_world_position, frag_pos);
    vec3 sunFactor = calc_sun_light(position, frag_pos, normal, specularFactor);

    vec3 point_light_factor = vec3(0.0);
    for (int i = 0; i < total_plights; i++) {
        PointLight p = p_l[i];
        vec3 delta = p.view_position - frag_pos;
        float distSq = dot(delta, delta);
        if (distSq <= p.clipRadius * p.clipRadius) {
            float p_brightness = p.brightness;
            vec3 params = getParams(p_brightness);
            float p_id = p.attachedShadowSceneId;
            float shadow = p_id >= 0 ? calculate_point_light_shadows(sampleShadowPl(int(p_id)), frag_world_position.xyz, p.position.xyz) : 1.;
            point_light_factor += calc_point_light(p, frag_pos, normal, params.x, params.y, params.z, p_brightness, specularFactor) * shadow;
        }
    }

    vec3 spot_light_factor = vec3(0.0);
    for (int i = 0; i < total_slights; i++) {
        SpotLight s = s_l[i];
        //vec3 delta = p.view_position - frag_pos;
        //float distSq = dot(delta, delta);
        //if (distSq <= p.clipRadius * p.clipRadius) {
        float s_brightness = s.brightness;
        vec3 params = getParams(s_brightness);
        float s_id = s.attachedShadowSceneId;
        float shadow = s_id >= 0 ? calculate_spot_light_shadows(int(s_id), frag_world_position, s.position.xyz) : 1.;
        spot_light_factor += calc_spot_light(s, frag_pos, normal, params.x, params.y, params.z, s_brightness, specularFactor) * shadow;
        //}
    }

    float brightness = dot(point_light_factor.rgb + spot_light_factor.rgb, vec3(0.2126, 0.7152, 0.0722)) * 5.0;
    lightFactors += sunFactor * clamp(sun_shadow + brightness, 0.0, 1.0);
    lightFactors += point_light_factor;
    lightFactors += spot_light_factor;

    return lightFactors;
}