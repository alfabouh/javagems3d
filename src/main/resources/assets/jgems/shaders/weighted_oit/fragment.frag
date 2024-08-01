in vec2 out_texture;

layout (location = 0) out vec4 accumulated;
layout (location = 1) out float reveal;

in vec2 texture_coordinates;

uniform vec4 color;

void main()
{
    //vec4 color = texture(diffuse_map, texture_coordinates);
    //if (color.a >= 1.) {
    //    discard;
    //}

    float weight = max(min(1.0, max(max(color.r, color.g), color.b) * color.a), color.a) * clamp(0.03 / (1.0e-5f + pow(gl_FragCoord.z / 200, 4.0)), 1.0e-2f, 3.0e+3f);

    accumulated = vec4(color.rgb * color.a, color.a) * weight;
    reveal = color.a;
}