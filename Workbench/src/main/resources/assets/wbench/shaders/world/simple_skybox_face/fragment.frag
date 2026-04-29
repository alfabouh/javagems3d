layout (location = 0) out vec4 frag_color;
uniform sampler2D skybox_face2D;
uniform sampler2D skybox_faceHint;
in vec2 uv_coordinates;
uniform bool doHint;

void main()
{
    vec4 diffuse = texture(skybox_face2D, uv_coordinates);
    vec4 hint = doHint ? texture(skybox_faceHint, vec2(1. - uv_coordinates.x, uv_coordinates.y)) : vec4(0.);
    frag_color = hint.a <= 0.1 ? diffuse : hint;
}