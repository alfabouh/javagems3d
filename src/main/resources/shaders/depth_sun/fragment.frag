layout (location = 0) out vec4 frag_color0;

void main()
{
    float d = gl_FragCoord.z;
    float dx = dFdx(d);
    float dy = dFdy(d);
    float moment2 = d * d + 0.25 * (dx * dx + dy * dy);
    frag_color0 = vec4(d, moment2, 0.0, 0.0);
}