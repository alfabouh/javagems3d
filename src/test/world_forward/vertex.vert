layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 aTexture;
layout (location=2) in vec3 aNormal;
layout (location=3) in vec3 aTangent;
layout (location=4) in vec3 aBitangent;

struct PointLight
{
    float plPosX;
    float plPosY;
    float plPosZ;
    float plR;
    float plG;
    float plB;
    float brightness;
    float shadowMapId;
};

layout (std140, binding = 0) uniform SunLight {
    float ambient;
    float sunBright;
    float sunX;
    float sunY;
    float sunZ;
    float sunColorR;
    float sunColorG;
    float sunColorB;
};

layout (std140, binding = 1) uniform PointLights {
    PointLight p_l[128];
    int total_plights;
};

layout (std140, binding = 2) uniform Misc {
    float w_tick;
};

layout (std140, binding = 3) uniform Fog {
    float fogDensity;
    float fogColorR;
    float fogColorG;
    float fogColorB;
};

out vec2 texture_coordinates;
out vec3 mv_vertex_normal;
out vec3 mv_vertex_pos;
out mat3 TBN;
out mat4 out_view_matrix;

out vec4 out_world_position;

uniform mat4 view_matrix;
uniform mat4 model_matrix;
uniform mat4 projection_matrix;

void main()
{
    mat4 model_view_matrix = view_matrix * model_matrix;
    vec4 mv_pos = model_view_matrix * vec4(aPosition, 1.0f);
    gl_Position = projection_matrix * mv_pos;

    texture_coordinates = aTexture;
    mv_vertex_normal = normalize(model_view_matrix * vec4(aNormal, 0.0f)).xyz;
    mv_vertex_pos = mv_pos.xyz;

    vec3 T = normalize(vec3(model_view_matrix * (vec4(aTangent, 0.0))));
    vec3 B = normalize(vec3(model_view_matrix * (vec4(aBitangent, 0.0))));
    vec3 N = normalize(vec3(model_view_matrix * (vec4(aNormal, 0.0))));
    TBN = mat3(T, B, N);

    out_world_position = model_matrix * vec4(aPosition, 1.0f);
    out_view_matrix = view_matrix;
}
