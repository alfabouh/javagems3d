layout (location=0) in vec3 position;
layout (location=1) in vec2 texture;

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

layout (std140, binding = 3) uniform Fog {
    float fogDensity;
    float fogColorR;
    float fogColorG;
    float fogColorB;
};

out vec2 out_texture;
out mat4 out_view_matrix;
out mat4 out_inversed_view_matrix;
uniform mat4 projection_model_matrix;
uniform mat4 view_matrix;

void main()
{
    gl_Position = projection_model_matrix * vec4(position, 1.0f);
    out_texture = texture;

    out_inversed_view_matrix = inverse(view_matrix);
    out_view_matrix = view_matrix;
}