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
    mat4 modelMatrices[2048];
    int entityIds[2048];
    int materialIds[2048];
    int animationOffset[2048];
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

   // row0 = vec4(1., 0., 0., 0.);
   // row1 = vec4(0., 1., 0., 0.);
   // row2 = vec4(0., 0., 1., 0.);
   // row3 = vec4(0., 0., 0., 1.);

    return mat4(row0, row1, row2, row3);
}

void main()
{
    uint idx = gl_BaseInstance + gl_InstanceID;
    matertial_id = materialIds[idx];
    ent_id = entityIds[idx];
    mat4 model = modelMatrices[ent_id];
    int currAnimationOffset = animationOffset[ent_id];

    vec4 position = vec4(0.);
    vec4 normal = vec4(0.);
    vec4 tangent = vec4(0.);
    vec4 bitanget = vec4(0.);

    int j = 0;
    if (currAnimationOffset >= 0) {
        for (int i = 0; i < MAX_WEIGHTS; i++) {
            float weight = aBoneWeights[i];
            if (weight > 0.) {
                j += 1;
                int boneId = aBoneIndexes[i];

                mat4 matrixBone = getBoneMatrix(currAnimationOffset, boneId);
                vec4 tempPos = matrixBone * vec4(aPosition, 1.);
                vec4 tempNormal = matrixBone * vec4(aNormal, 0.);
                vec4 tempTangent = matrixBone * vec4(aTangent, 0.);
                vec4 tempBiTangent = matrixBone * vec4(aBitangent, 0.);

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