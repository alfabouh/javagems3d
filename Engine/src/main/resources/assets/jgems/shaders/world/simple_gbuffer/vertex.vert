layout (location=0) in vec3 aPosition;
layout (location=5) in ivec4 aBoneIndexes;
layout (location=6) in vec4 aBoneWeights;

const int MAX_WEIGHTS = 4;

uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection_matrix;

out vec3 modelview_vertex_pos;

layout(std430, binding = 0) buffer BoneMatrices {
    mat4 bone_matrices[64];
};

void main()
{
    vec4 startPos = vec4(0.);

    int j = 0;
    if (true) {
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

    mat4 model_view = view_matrix * model_matrix;
    vec4 mv_pos = model_view * startPos;
    gl_Position = projection_matrix * mv_pos;

    modelview_vertex_pos = mv_pos.xyz;
}
