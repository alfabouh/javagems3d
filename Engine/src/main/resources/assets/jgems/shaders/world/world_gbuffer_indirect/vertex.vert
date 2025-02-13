layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 aTexture;
layout (location=2) in vec3 aNormal;
layout (location=3) in vec3 aTangent;
layout (location=4) in vec3 aBitangent;
layout (location=5) in ivec4 aBoneIndexes;
layout (location=6) in vec4 aBoneWeights;

const int MAX_WEIGHTS = 4;

out vec2 uv_coordinates;
out vec3 model_vertex_normal;
out vec4 model_vertex_pos;
out vec3 modelview_vertex_normal;
out vec3 modelview_vertex_pos;
out mat3 TBN;

out flat uint matertial_id;
out flat uint ent_id;

uniform mat4 view_matrix;
uniform mat4 projection_matrix;

uniform sampler2D animationsMatrix;

layout(std430, binding = 1) buffer IndirectBufferData {
    int entityId[2048];
    int materialId[2048];
    mat4 modelMatrix[2048];
    int animationOffset[2048];
    int animationOffsetPrev[2048];
    float animationFrameDelta[2048];
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
    vec4 normal = vec4(0.);
    vec4 tangent = vec4(0.);
    vec4 bitanget = vec4(0.);

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
                vec4 tempNormal = mixVec4(matrixBone, matrixBonePrev, vec4(aNormal, 0.), deltaFrame);
                vec4 tempTangent = mixVec4(matrixBone, matrixBonePrev, vec4(aTangent, 0.), deltaFrame);
                vec4 tempBiTangent = mixVec4(matrixBone, matrixBonePrev, vec4(aBitangent, 0.), deltaFrame);

                position += weight * tempPos;
                normal += weight * tempNormal;
                tangent += weight * tempTangent;
                bitanget += weight * tempBiTangent;
            }
        }
    }
    if (j == 0) {
        position = vec4(aPosition, 1.0);
        normal = vec4(aNormal, 0.0);
        tangent = vec4(aTangent, 0.0);
        bitanget = vec4(aBitangent, 0.0);
    }

    mat4 view = view_matrix;
    mat4 model_view_matrix = view_matrix * model;
    vec4 mv_pos = model_view_matrix * position;
    gl_Position = projection_matrix * mv_pos;

    vec3 T = normalize(vec3(model_view_matrix * tangent));
    vec3 B = normalize(vec3(model_view_matrix * bitanget));
    vec3 N = normalize(vec3(model_view_matrix * normal));
    TBN = mat3(T, B, N);

    uv_coordinates = aTexture;
    modelview_vertex_normal = normalize(model_view_matrix * normal).xyz;
    model_vertex_normal = normalize(model * normal).xyz;
    modelview_vertex_pos = mv_pos.xyz;

    model_vertex_pos = model * position;
}