layout (location = 0) out vec4 frag_color;
in vec2 out_texture;

uniform vec4 colour;
uniform sampler2D texture_sampler;

layout (std140, binding = 2) uniform Misc {
    float w_tick;
};

float rand(vec2 co)
{
    return fract(sin(dot(co.xy + w_tick, vec2(12.9898, 78.233) + w_tick)) * 43758.5453 + w_tick);
}

void main()
{
    vec2 tex = gl_FragCoord.xy / textureSize(texture_sampler, 0);
    vec4 v4 = colour * texture(texture_sampler, out_texture);
    frag_color = vec4(v4.x * (rand(vec2(tex)) * 0.25 + 0.75), v4.y * (rand(vec2(tex)) * 0.25 + 0.75), v4.z * (rand(vec2(tex)) * 0.25 + 0.75), v4.a);
}