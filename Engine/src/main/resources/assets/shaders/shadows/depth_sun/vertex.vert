#extension GL_ARB_bindless_texture : require
precision highp float;
precision highp int;

layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 texture;
layout (location=5) in ivec4 aBoneIndexes;
layout (location=6) in vec4 aBoneWeights;

out vec2 uv_coordinates;

uniform mat4 projection_view_matrix;
uniform mat4 model_matrix;

struct AnimationData {
    int currAnimationOffset;
    int currAnimationOffsetPrev;
    float deltaFrame;
};
uniform AnimationData animationData;

#include "/assets/shaders/libs/animations"

void main()
{
    vec4 position = vec4(aPosition, 1.0);
    vec4 tempNormal = vec4(0.0);
    vec4 tempTangent = vec4(0.0);
    vec4 tempBiTangent = vec4(0.0);
    perform_animation(position, tempNormal, tempTangent, tempBiTangent, aBoneIndexes, aBoneWeights, animationData.currAnimationOffset, animationData.currAnimationOffsetPrev, animationData.deltaFrame);

    gl_Position = projection_view_matrix * model_matrix * position;
    uv_coordinates = texture;
}