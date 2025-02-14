layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 texture;
layout (location=5) in ivec4 aBoneIndexes;
layout (location=6) in vec4 aBoneWeights;

const int MAX_WEIGHTS = CONST.ANIM_MAX_WEIGHTS;

uniform mat4 projection_view_matrix;
uniform mat4 model_matrix;

layout(std430, binding = 0) buffer BoneMatrices {
    mat4 bone_matrices[64];
};
uniform bool hasAnimations;

out vec2 uv_coordinates;

void main()
{
    vec4 startPos = vec4(0.);
    int j = 0;
    if (hasAnimations) {
        for (int i = 0; i < MAX_WEIGHTS; i++) {
            float weight = aBoneWeights[i];
            if (weight > 0.) {
                j += 1;
                int boneId = aBoneIndexes[i];
                vec4 tempPos = bone_matrices[boneId] * vec4(aPosition, 1.);
                startPos += weight * tempPos;
            }
        }
    }

    if (j == 0) {
        startPos = vec4(aPosition, 1.0);
    }

    gl_Position = projection_view_matrix * model_matrix * startPos;
    uv_coordinates = texture;
}