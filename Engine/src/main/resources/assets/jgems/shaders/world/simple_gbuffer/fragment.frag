layout (early_fragment_tests) in;
layout (location = 0) out vec4 gPosition;
layout (location = 1) out vec4 gNormal;
layout (location = 2) out vec4 gColor;
layout (location = 3) out vec4 gEmission;
layout (location = 4) out vec4 gSpecular;

in vec3 modelview_vertex_pos;

void main()
{
    gNormal = vec4(vec3(1.), 1.0);
    gPosition = vec4(modelview_vertex_pos, 1.0);
    gColor = vec4(vec3(gl_FragCoord.x, 1.0, 1.0), 1.);
    gEmission = vec4(0.);
    gSpecular = vec4(0.);
}