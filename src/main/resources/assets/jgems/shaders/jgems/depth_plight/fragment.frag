layout (location = 0) out vec4 frag_color0;

in vec4 frag_pos;
uniform vec3 lightPos;
uniform float far_plane;

void main()
{
    float lightDistance = length(frag_pos.xyz - lightPos);
    lightDistance /= far_plane;

    float d = lightDistance;
    float dx = dFdx(d);
    float dy = dFdy(d);
    float moment2 = d * d + 0.25 * (dx * dx + dy * dy);
    frag_color0 = vec4(d, moment2, 0.0, 1.0);
}
