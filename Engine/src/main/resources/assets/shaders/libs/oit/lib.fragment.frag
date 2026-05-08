vec4 calc_accumulated(vec4 frag_color0) {
    float weight = max(min(1.0, max(max(frag_color0.r, frag_color0.g), frag_color0.b) * frag_color0.a), frag_color0.a) * clamp(0.03 / (1.0e-5f + pow(gl_FragCoord.z / 200.0, 4.0)), 1.0e-2f, 3.0e+3f);
    return vec4(frag_color0.rgb * frag_color0.a, frag_color0.a) * weight;
}

float calc_alpha(vec4 frag_color0) {
    return frag_color0.a;
}

/*
vec4 calc_accumulated() {
    float weight = max(min(1.0, max(max(frag_color0.r, frag_color0.g), frag_color0.b) * frag_color0.a), frag_color0.a) * clamp(0.03 / (1.0e-5f + pow(gl_FragCoord.z / 200.0, 4.0)), 1.0e-2f, 3.0e+3f);
    return vec4(frag_color0.rgb * frag_color0.a, frag_color0.a) * weight;
}
*/