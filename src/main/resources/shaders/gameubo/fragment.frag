struct PointLight
{
    float plPosX;
    float plPosY;
    float plPosZ;
    float plR;
    float plG;
    float plB;
    float brightness;
    int shadowMapId;
};

layout (std140, binding = 0) uniform SunLight {
    float ambient;
    float sunBright;
    float sunX;
    float sunY;
    float sunZ;
};

layout (std140, binding = 1) uniform PointLights {
    PointLight p_l[128];
};

layout (std140, binding = 2) uniform Misc {
    float w_tick;
};

void main()
{
}