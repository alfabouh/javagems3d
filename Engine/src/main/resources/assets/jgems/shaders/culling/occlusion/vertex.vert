layout (location=0) in vec3 aPosition;

out flat uint ent_id;

uniform mat4 view_matrix;
uniform mat4 projection_matrix;

layout(std430, binding = 1) buffer IndirectBufferData {
    mat4 modelMatrices[2048];
    int entityIds[2048];
    int materialIds[2048];
    int animationOffset[2048];
};

void main()
{
    uint idx = gl_BaseInstance + gl_InstanceID;
    ent_id = entityIds[idx];
    mat4 model = modelMatrices[ent_id];

    mat4 model_view_matrix = view_matrix * model;
    vec4 mv_pos = model_view_matrix * vec4(aPosition, 1.0f);
    gl_Position = (projection_matrix * mv_pos);
}