layout (location=0) in vec3 position;

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

layout (std140, binding = 3) uniform Fog {
    float fogDensity;
    float fogColorR;
    float fogColorG;
    float fogColorB;
};

out vec3 out_texture;
uniform mat4 projection_matrix;
uniform mat4 model_view_matrix;

void main()
{
    vec4 pos = projection_matrix * model_view_matrix * vec4(position, 1.0f);
    gl_Position = pos.xyww;
    out_texture = position;
}
