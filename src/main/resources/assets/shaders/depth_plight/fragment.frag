layout (depth_less) out float gl_FragDepth;

in vec4 frag_pos;
uniform vec3 lightPos;
uniform float far_plane;

void main()
{
    float lightDistance = length(frag_pos.xyz - lightPos);
    lightDistance /= far_plane;
    gl_FragDepth = lightDistance;
}
