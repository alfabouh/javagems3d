layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 aTexture;
layout (location=2) in vec3 aNormal;
layout (location=3) in vec3 aTangent;
layout (location=4) in vec3 aBitangent;
layout (location=5) in ivec4 aBoneIndexes;
layout (location=6) in vec4 aBoneWeights;

const int MAX_WEIGHTS = CONST.ANIM_MAX_WEIGHTS;

out vec2 uv_coordinates;
out vec3 model_vertex_normal;
out vec3 modelview_vertex_normal;
out vec3 modelview_vertex_pos;
out vec4 model_vertex_pos;
out mat3 TBN;

out mat4 view;
out mat4 model;

uniform mat4 view_matrix;
uniform mat4 model_matrix;
uniform mat4 projection_matrix;

layout(std430, binding = 0) buffer BoneMatrices {
    mat4 bone_matrices[64];
};

uniform bool hasAnimations;

void main()
{
    vec4 startPos = vec4(0.);
    vec4 startNormal = vec4(0.);
    vec4 startTangent = vec4(0.);
    vec4 startBiTangent = vec4(0.);

    int j = 0;
    if (hasAnimations) {
        for (int i = 0; i < MAX_WEIGHTS; i++) {
            float weight = aBoneWeights[i];
            if (weight > 0.) {
                j += 1;
                int boneId = aBoneIndexes[i];

                vec4 tempPos = bone_matrices[boneId] * vec4(aPosition, 1.);
                vec4 tempNormal = bone_matrices[boneId] * vec4(aNormal, 0.);
                vec4 tempTangent = bone_matrices[boneId] * vec4(aTangent, 0.);
                vec4 tempBiTangent = bone_matrices[boneId] * vec4(aBitangent, 0.);

                startPos += weight * tempPos;
                startNormal += weight * tempNormal;
                startTangent += weight * tempTangent;
                startBiTangent += weight * tempBiTangent;
            }
        }
    }

    if (j == 0) {
        startPos = vec4(aPosition, 1.0);
        startNormal = vec4(aNormal, 0.0);
        startTangent = vec4(aTangent, 0.0);
        startBiTangent = vec4(aBitangent, 0.0);
    }

    view = view_matrix;
    model = model_matrix;

    mat4 model_view_matrix = view_matrix * model_matrix;
    vec4 mv_pos = model_view_matrix * startPos;
    gl_Position = projection_matrix * mv_pos;

    vec3 T = normalize(vec3(model_view_matrix * startTangent));
    vec3 B = normalize(vec3(model_view_matrix * startBiTangent));
    vec3 N = normalize(vec3(model_view_matrix * startNormal));
    TBN = mat3(T, B, N);

    uv_coordinates = aTexture;
    modelview_vertex_normal = normalize(model_view_matrix * startNormal).xyz;
    model_vertex_normal = normalize(model_matrix * startNormal).xyz;
    modelview_vertex_pos = mv_pos.xyz;

    model_vertex_pos = model_matrix * startPos;
}