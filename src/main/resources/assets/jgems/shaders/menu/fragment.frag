in vec2 out_texture;
layout (location = 0) out vec4 frag_color;

uniform bool show_blood;
uniform float w_tick;
uniform vec3 color;

float rand(vec2 co)
{
    return fract(sin(dot(co.xy + tan(w_tick), vec2(12.9898, 78.233))) * 43758.5453);
}

vec4 random_noise(vec4 txtr) {
    float pixelSize = 0.0035;
    vec2 tex = out_texture;
    vec2 pixelCoords = floor(tex / pixelSize) * pixelSize;
    vec4 colors = txtr;
    float grain = clamp(rand(pixelCoords) * (0.05), 0.0, 1.0);
    return txtr + grain;
}

void main()
{
    frag_color = (random_noise(vec4(0.0, 0.0, 0.0, 1.0)) * vec4(color, 1.0));
    frag_color *= vec4(vec3(min(w_tick, 1.0)), 1.0);
}
