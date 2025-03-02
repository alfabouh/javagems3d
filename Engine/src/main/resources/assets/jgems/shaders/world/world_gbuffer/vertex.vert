layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 aTexture;
layout (location=2) in vec3 aNormal;
layout (location=3) in vec3 aTangent;
layout (location=4) in vec3 aBitangent;
layout (location=5) in ivec4 aBoneIndexes;
layout (location=6) in vec4 aBoneWeights;

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

    view = view_matrix;
    model = model_matrix;

    mat4 model_view_matrix = view_matrix * model_matrix;
    vec4 mv_pos = model_view_matrix * position;
    gl_Position = projection_matrix * mv_pos;

    vec3 T = normalize(vec3(model_view_matrix * tangent));
    vec3 B = normalize(vec3(model_view_matrix * bitanget));
    vec3 N = normalize(vec3(model_view_matrix * normal));
    TBN = mat3(T, B, N);

    uv_coordinates = aTexture;
    modelview_vertex_normal = normalize(model_view_matrix * normal).xyz;
    model_vertex_normal = normalize(model_matrix * normal).xyz;
    modelview_vertex_pos = mv_pos.xyz;

    model_vertex_pos = model_matrix * position;
}