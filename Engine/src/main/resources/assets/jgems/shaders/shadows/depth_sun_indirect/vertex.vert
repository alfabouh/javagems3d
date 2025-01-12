layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 aTexture;
layout (location=2) in vec3 aNormal;

out vec2 uv_coordinates;
out flat uint matertial_id;
out flat uint ent_id;

uniform mat4 projection_view_matrix;

layout(std430, binding = 1) buffer IndirectBufferData {
    mat4 modelMatrices[1024];
    int entityIds[1024];
    int materialIds[1024];
};

void main()
{
    uint idx = gl_BaseInstance + gl_InstanceID;
    matertial_id = materialIds[idx];
    ent_id = entityIds[idx];

    mat4 model_matrix = modelMatrices[ent_id];

    gl_Position = projection_view_matrix * model_matrix * vec4(aPosition, 1.0);
    uv_coordinates = aTexture;
}