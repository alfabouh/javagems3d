layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 aTexture;
layout (location=5) in ivec4 aBoneIndexes;
layout (location=6) in vec4 aBoneWeights;

out vec2 uv_coordinates;
out flat uint matertial_id;
out flat uint ent_id;

uniform mat4 projection_view_matrix;
uniform sampler2D animationsMatrix;

const int MAX_WEIGHTS = CONST.ANIM_MAX_WEIGHTS;

layout(std430, binding = 1) buffer IndirectBufferData {
    int entityId[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
    int materialId[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
    mat4 modelMatrix[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
    int animationOffset[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
    int animationOffsetPrev[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
    float animationFrameDelta[CONST.MAX_INDIRECT_RENDERING_MESH_DATASETS];
};

ivec2 pickUV(int globalOffset, int arrI, int textureWidth) {
    int texX = (globalOffset + arrI) % textureWidth;
    int texY = (globalOffset + arrI) / textureWidth;
    return ivec2(texX, texY);
}

mat4 getBoneMatrix(int baseOffset, int boneIndex) {
    int textureWidth = textureSize(animationsMatrix, 0).x;
    int globalOffset = (baseOffset + boneIndex) * 4;
    vec4 row0 = texelFetch(animationsMatrix, pickUV(globalOffset, 0, textureWidth), 0);
    vec4 row1 = texelFetch(animationsMatrix, pickUV(globalOffset, 1, textureWidth), 0);
    vec4 row2 = texelFetch(animationsMatrix, pickUV(globalOffset, 2, textureWidth), 0);
    vec4 row3 = texelFetch(animationsMatrix, pickUV(globalOffset, 3, textureWidth), 0);
    return mat4(row0, row1, row2, row3);
}

vec4 mixVec4(mat4 matrixA, mat4 matrixB, vec4 value, float t) {
    vec4 v1 = matrixA * value;
    vec4 v2 = matrixB * value;
    return mix(v1, v2, t);
}

void main()
{
    uint idx = gl_BaseInstance + gl_InstanceID;
    ent_id = entityId[idx];
    matertial_id = materialId[idx];
    mat4 model = modelMatrix[ent_id];
    int currAnimationOffset = animationOffset[ent_id];

    vec4 position = vec4(0.);

    int j = 0;
    if (currAnimationOffset >= 0) {
        int currAnimationOffsetPrev = animationOffsetPrev[ent_id];
        float deltaFrame = animationFrameDelta[ent_id];

        for (int i = 0; i < MAX_WEIGHTS; i++) {
            float weight = aBoneWeights[i];
            if (weight > 0.) {
                j += 1;
                int boneId = aBoneIndexes[i];
                mat4 matrixBone = getBoneMatrix(currAnimationOffset, boneId);
                mat4 matrixBonePrev = getBoneMatrix(currAnimationOffsetPrev, boneId);

                vec4 tempPos = mixVec4(matrixBone, matrixBonePrev, vec4(aPosition, 1.), deltaFrame);

                position += weight * tempPos;
            }
        }
    }
    if (j == 0) {
        position = vec4(aPosition, 1.0);
    }

    gl_Position = projection_view_matrix * model * position;
    uv_coordinates = aTexture;
}