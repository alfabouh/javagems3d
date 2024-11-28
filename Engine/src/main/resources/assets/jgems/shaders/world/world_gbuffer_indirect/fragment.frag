in mat3 TBN;
in vec4 out_model_position;

in vec2 texture_coordinates;
in vec3 m_vertex_normal;
in vec3 mv_vertex_normal;
in vec3 mv_vertex_pos;

layout (location = 0) out vec4 gPosition;
layout (location = 1) out vec4 gNormal;
layout (location = 2) out vec4 gColor;
layout (location = 3) out vec4 gEmission;
layout (location = 4) out vec4 gSpecular;

bool checkCode(int i1, int i2) {
    int i3 = i1 & i2;
    return bool(i3 != 0);
}

void main()
{
    vec3 normals = mv_vertex_normal;
    gNormal = vec4(normals, 1.0);
    gPosition = vec4(mv_vertex_pos, 1.0);
    gColor = vec4(1.);
    gEmission = vec4(vec3(0.0), 1.0);
    gSpecular = vec4(vec3(0.0), 1.0);
}