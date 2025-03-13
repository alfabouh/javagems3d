layout (location = 0) in vec3 aPosition;
layout (location = 1) in vec2 aTexture;
layout (location = 2) in vec3 aNormal;

uniform mat4 model_matrix;
uniform mat4 projection_matrix;

out vec3 pos;
out vec3 normals;
out vec2 uv_texture;

void main()
{
    vec4 mv_pos = model_matrix * vec4(aPosition, 1.0f);
    gl_Position = projection_matrix * mv_pos;
    uv_texture = aTexture;

    normals = normalize(model_matrix * vec4(aNormal, 0.)).xyz;
    pos = mv_pos.xyz;
}