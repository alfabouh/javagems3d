in vec2 out_texture;
in mat4 projection;

layout (location = 0) out vec4 frag_color;

uniform mat4 projection_main_matrix;

uniform sampler2D gPositions;
uniform sampler2D gNormals;
uniform sampler2D ssaoKernel;
uniform sampler2D ssaoNoise;

uniform vec2 noiseScaling;

void main()
{
    const float radius = 1.5;
    const float bias = 0.025;
    vec3 frag_pos = texture(gPositions, out_texture).xyz;
    vec3 normals = texture(gNormals, out_texture).xyz;
    vec3 ran_vec = texture(ssaoNoise, out_texture * noiseScaling).xyz;

    vec3 tangent = normalize(ran_vec - normals * dot(ran_vec, normals));
    vec3 bitangent = cross(normals, tangent);
    mat3 TBN = mat3(tangent, bitangent, normals);

    vec2 kernelSize2 = textureSize(ssaoKernel, 0);
    int kernelSize = int(kernelSize2.x) * int(kernelSize2.y);

    float occlusion = 0.;
    for (int i = 0; i < kernelSize; i++) {
        vec3 sampler = TBN * texelFetch(ssaoKernel, ivec2(i % int(kernelSize2.x), i / int(kernelSize2.y)), 0).rgb;
        sampler = frag_pos + sampler * radius;

        vec4 offset = vec4(sampler, 1.);
        offset = projection_main_matrix * offset;
        offset.xyz /= offset.w;
        offset.xyz = offset.xyz * 0.5 + 0.5;

        float depth = texture(gPositions, offset.xy).z;
        float rangeCheck = smoothstep(0.0, 1.0, radius / abs(frag_pos.z - depth));
        occlusion += (depth >= sampler.z + bias ? 1.0 : 0.0) * rangeCheck;
    }

    occlusion = 1.0 - (occlusion / kernelSize);
    frag_color = vec4(occlusion);
}