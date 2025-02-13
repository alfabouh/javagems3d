layout (location=0) in vec3 aPosition;

out flat uint ent_id;

uniform mat4 view_matrix;
uniform mat4 projection_matrix;


layout(std430, binding = 1) buffer IndirectBufferData {
    int entityId[2048];
    int materialId[2048];
    mat4 modelMatrix[2048];
    int animationOffset[2048];
    int animationOffsetPrev[2048];
    float animationFrameDelta[2048];
};

void main()
{
    uint idx = gl_BaseInstance + gl_InstanceID;
    MeshData meshData = meshDataArr[idx];
    ent_id = meshData.entityId;
    ObjData objData = objDataArr[ent_id];
    mat4 model = objData.modelMatrix;

    mat4 model_view_matrix = view_matrix * model;
    vec4 mv_pos = model_view_matrix * vec4(aPosition, 1.0f);
    gl_Position = (projection_matrix * mv_pos);
}