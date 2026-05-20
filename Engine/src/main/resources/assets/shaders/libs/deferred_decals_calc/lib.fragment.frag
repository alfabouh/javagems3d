uniform mat4 decal_invModelMatrix;
uniform sampler2D decal_diffuse_map;
uniform vec4 decal_diffuse_color;
uniform uint decal_ent_layerID;
uniform float emissiveFactor;
uniform mat4 view_mat_inverted;

vec3 calcDecalCoord(vec4 modelview_vertex_pos) {
    vec4 world_pos = view_mat_inverted * modelview_vertex_pos;
    return (decal_invModelMatrix * world_pos).xyz;
}

vec3 toDecalNormal(vec3 worldNormal)
{
    return mat3(decal_invModelMatrix) * mat3(view_mat_inverted) * worldNormal;
}

vec3 calcComponents(vec3 worldNormal) {
    float wx = worldNormal.x * worldNormal.x * worldNormal.x;
    float wy = worldNormal.y * worldNormal.y * worldNormal.y;
    float wz = worldNormal.z * worldNormal.z * worldNormal.z;
    float sum = wx + wy + wz;
    wx /= sum;
    wy /= sum;
    wz /= sum;
    return vec3(wx, wy, wz);
}

vec4 sampleTriplanarDiffTexture(vec3 components, vec3 localPos) {
    vec4 xz = texture(decal_diffuse_map, localPos.xz * 0.5 + 0.5);
    vec4 yz = texture(decal_diffuse_map, localPos.yz * 0.5 + 0.5);
    vec4 xy = texture(decal_diffuse_map, localPos.xy * 0.5 + 0.5);
    //xz * wy + yz * wx + xy * wz
    return (xz * components.y + yz * components.x + xy * components.z) * decal_diffuse_color;
}

/*
vec4 sampleTriplanarEmTexture(vec3 components, vec3 localPos) {
    vec4 xz = texture(decal_emissive_map, localPos.xz * 0.5 + 0.5);
    vec4 yz = texture(decal_emissive_map, localPos.yz * 0.5 + 0.5);
    vec4 xy = texture(decal_emissive_map, localPos.xy * 0.5 + 0.5);
    //xz * wy + yz * wx + xy * wz
    return (xz * components.y + yz * components.x + xy * components.z) * decal_diffuse_color;
}
*/