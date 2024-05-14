layout (location = 0) out vec4 gPosition;
layout (location = 2) out vec4 gColor;
layout (location = 3) out vec4 gEmission;

in vec4 out_mv_position;

void main()
{
    gPosition = out_mv_position;
    gColor = vec4(1.0, 0.0, 0.0, 1.0);
    gEmission = vec4(1.0);
}