layout (location = 0) out vec4 frag_color;
in vec2 uv_coordinates;

uniform vec4 color;
uniform sampler2D texture_sampler;

layout (std430, binding = 0) buffer Timer {
    float w_tick;
};

float rand(vec2 co)
{
    return fract(sin(dot(co.xy + tan(w_tick), vec2(12.9898, 78.233))) * 43758.5453);
}

void main()
{
    vec2 tex = gl_FragCoord.xy / textureSize(texture_sampler, 0);
    vec4 v4 = color * texture(texture_sampler, uv_coordinates);
    frag_color = vec4(v4.x * (rand(vec2(tex)) * 0.25 + 0.75), v4.y * (rand(vec2(tex)) * 0.25 + 0.75), v4.z * (rand(vec2(tex)) * 0.25 + 0.75), v4.a);
}