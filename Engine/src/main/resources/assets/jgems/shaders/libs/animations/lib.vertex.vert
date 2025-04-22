uniform sampler2D animations_matrix;
const int MAX_WEIGHTS = CONST.ANIM_MAX_WEIGHTS;

ivec2 pickUV(int globalOffset, int arrI, int textureWidth) {
    int texX = (globalOffset + arrI) % textureWidth;
    int texY = (globalOffset + arrI) / textureWidth;
    return ivec2(texX, texY);
}

mat4 getBoneMatrix(int baseOffset, int boneIndex) {
    int textureWidth = textureSize(sampler2D(animations_matrix), 0).x;
    int globalOffset = (baseOffset + boneIndex) * 4;
    vec4 row0 = texelFetch(animations_matrix, pickUV(globalOffset, 0, textureWidth), 0);
    vec4 row1 = texelFetch(animations_matrix, pickUV(globalOffset, 1, textureWidth), 0);
    vec4 row2 = texelFetch(animations_matrix, pickUV(globalOffset, 2, textureWidth), 0);
    vec4 row3 = texelFetch(animations_matrix, pickUV(globalOffset, 3, textureWidth), 0);
    return mat4(row0, row1, row2, row3);
}

vec4 mixVec4(mat4 matrixA, mat4 matrixB, vec4 value, float t) {
    vec4 v1 = matrixA * value;
    vec4 v2 = matrixB * value;
    return mix(v1, v2, t);
}

void perform_animation(inout vec4 tempPos, inout vec4 tempNormal, inout vec4 tempTangent, inout vec4 tempBiTangent, ivec4 aBoneIndexes, vec4 aBoneWeights, int currAnimationOffset, int currAnimationOffsetPrev, float deltaFrame) {
    vec4 position = vec4(0.);
    vec4 normal = vec4(0.);
    vec4 tangent = vec4(0.);
    vec4 bitangent = vec4(0.);

    if (currAnimationOffset >= 0) {
        for (int i = 0; i < MAX_WEIGHTS; i++) {
            float weight = aBoneWeights[i];
            if (weight > 0.) {
                int boneId = aBoneIndexes[i];
                mat4 matrixBone = getBoneMatrix(currAnimationOffset, boneId);
                mat4 matrixBonePrev = getBoneMatrix(currAnimationOffsetPrev, boneId);

                vec4 newPos = mixVec4(matrixBone, matrixBonePrev, tempPos, deltaFrame);
                vec4 newNormal = mixVec4(matrixBone, matrixBonePrev, tempNormal, deltaFrame);
                vec4 newTangent = mixVec4(matrixBone, matrixBonePrev, tempTangent, deltaFrame);
                vec4 newBiTangent = mixVec4(matrixBone, matrixBonePrev, tempBiTangent, deltaFrame);

                position += weight * newPos;
                normal += weight * newNormal;
                tangent += weight * newTangent;
                bitangent += weight * newBiTangent;
            }
        }
    }
    if (length(position) > 0) {
        tempPos = position;
        tempNormal = normal;
        tempTangent = tangent;
        tempBiTangent = bitangent;
    }
}