layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec2 aTexture;
layout (location = 2) in vec3 aNormal;
layout (location=3) in vec3 aTangent;
layout (location=4) in vec3 aBitangent;
layout (location=5) in ivec4 aBoneIndexes;
layout (location=6) in vec4 aBoneWeights;

uniform mat4 model_matrix;
uniform mat4 projection_matrix;
uniform mat4 view_matrix;

out vec3 pos;
out vec3 normals;
out vec2 uv_texture;

struct AnimationData {
    int currAnimationOffset;
    int currAnimationOffsetPrev;
    float deltaFrame;
};
uniform AnimationData animationData;

#include "assets/jgems/shaders/libs/animations"

void main()
{
    vec4 position = vec4(aPosition, 1.0);
    vec4 normal = vec4(aNormal, 0.0);
    vec4 tangent = vec4(aTangent, 0.0);
    vec4 bitanget = vec4(aBitangent, 0.0);
    perform_animation(position, normal, tangent, bitanget, aBoneIndexes, aBoneWeights, animationData.currAnimationOffset, animationData.currAnimationOffsetPrev, animationData.deltaFrame);

    vec4 mv_pos = model_matrix * view_matrix * position;
    gl_Position = projection_matrix * mv_pos;
    uv_texture = aTexture;

    normals = normalize(model_matrix * vec4(aNormal, 0.)).xyz;
    pos = mv_pos.xyz;
}