#extension GL_ARB_bindless_texture : enable

layout (location=0) in vec3 aPosition;
layout (location=5) in ivec4 aBoneIndexes;
layout (location=6) in vec4 aBoneWeights;

out vec3 modelview_vertex_pos;

uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection_matrix;

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

    mat4 model_view = view_matrix * model_matrix;
    vec4 mv_pos = model_view * position;
    gl_Position = projection_matrix * mv_pos;

    modelview_vertex_pos = mv_pos.xyz;
}
