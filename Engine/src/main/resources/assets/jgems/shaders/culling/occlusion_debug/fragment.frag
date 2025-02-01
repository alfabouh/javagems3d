#extension GL_ARB_bindless_texture : require

layout (early_fragment_tests) in;
layout (location = 0) out vec4 frag_color0;

in flat uint ent_id;

layout(std430, binding = 5) buffer Visibility {
    int visibility[2048];
};

void main()
{
    visibility[ent_id] = 1;
    frag_color0 = vec4(fract(sin(ent_id * 12.9898) * 43758.5453), fract(sin(ent_id * 78.233) * 43758.5453), fract(sin(ent_id * 34.271) * 43758.5453), 1.0);
}