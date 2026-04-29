layout (location=0) in vec3 aPosition;

out flat uint ent_id;

uniform mat4 view_matrix;
uniform mat4 projection_matrix;


layout(std430, binding = 1) buffer IndirectBufferData {
    int entityId[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
    int materialId[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
    mat4 modelMatrix[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
    int animationOffset[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
    int animationOffsetPrev[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
    float animationFrameDelta[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
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