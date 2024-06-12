layout (location = 0) out vec4 frag_color;

const float FXAA_REDUCE_MIN = (1.0/128.0);
const float FXAA_REDUCE_MUL = (1.0/8.0);
const float FXAA_THRESHOLD = (1.0/256.0);

uniform sampler2D texture_sampler;
uniform vec2 resolution;
uniform float FXAA_SPAN_MAX;

void main()
{
    vec2 inverse_resolution = vec2(1.0 / resolution.x, 1.0 / resolution.y);
    vec3 luma = vec3(0.2126, 0.7152, 0.0722);

    vec3 rgbNW = texture(texture_sampler, (gl_FragCoord.xy + vec2(-1.0, -1.0)) * inverse_resolution).xyz;
    vec3 rgbNE = texture(texture_sampler, (gl_FragCoord.xy + vec2(1.0, -1.0)) * inverse_resolution).xyz;
    vec3 rgbSW = texture(texture_sampler, (gl_FragCoord.xy + vec2(-1.0, 1.0)) * inverse_resolution).xyz;
    vec3 rgbSE = texture(texture_sampler, (gl_FragCoord.xy + vec2(1.0, 1.0)) * inverse_resolution).xyz;
    vec3 rgbM = texture(texture_sampler, gl_FragCoord.xy * inverse_resolution).xyz;

    float lumaNW = dot(rgbNW, luma);
    float lumaNE = dot(rgbNE, luma);
    float lumaSW = dot(rgbSW, luma);
    float lumaSE = dot(rgbSE, luma);
    float lumaM = dot(rgbM, luma);

    float lumaMin = min(lumaM, min(min(lumaNW, lumaNE), min(lumaSW, lumaSE)));
    float lumaMax = max(lumaM, max(max(lumaNW, lumaNE), max(lumaSW, lumaSE)));

    float lumaRange = lumaMax - lumaMin;
    if (lumaRange <= FXAA_THRESHOLD)
    {
        frag_color = vec4(rgbM, 1.0);
        return;
    }

    vec2 dir;
    dir.x = -((lumaNW + lumaNE) - (lumaSW + lumaSE));
    dir.y = ((lumaNW + lumaSW) - (lumaNE + lumaSE));

    float dirReduce = max((lumaNW + lumaNE + lumaSW + lumaSE) * (0.25 * FXAA_REDUCE_MUL), FXAA_REDUCE_MIN);
    float rcpDirMin = 1.0 / (min(abs(dir.x), abs(dir.y)) + dirReduce);
    dir = min(vec2(FXAA_SPAN_MAX, FXAA_SPAN_MAX), max(vec2(-FXAA_SPAN_MAX, -FXAA_SPAN_MAX), dir * rcpDirMin)) * inverse_resolution;

    vec3 rgbA = 0.5 * (texture(texture_sampler, gl_FragCoord.xy * inverse_resolution + dir * (1.0 / 3.0 - 0.5)).xyz + texture(texture_sampler, gl_FragCoord.xy * inverse_resolution + dir * (2.0 / 3.0 - 0.5)).xyz);
    vec3 rgbB = rgbA * 0.5 + 0.25 * (texture(texture_sampler, gl_FragCoord.xy * inverse_resolution + dir * -0.5).xyz + texture(texture_sampler, gl_FragCoord.xy * inverse_resolution + dir * 0.5).xyz);

    frag_color = vec4(rgbB, 1.0);
}
