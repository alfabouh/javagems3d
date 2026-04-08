layout (location=0) in vec3 aPosition;
layout (location=2) in vec3 aNormal;

uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 projection_matrix;

out vec3 normal;

void main()
{
    mat4 modelViewMatrix = view_matrix * model_matrix;

    vec4 mv_pos = modelViewMatrix * vec4(aPosition, 1.0f);
    gl_Position = projection_matrix * mv_pos;

    normal = normalize(model_matrix * vec4(aNormal, 0.0f)).xyz;
}
