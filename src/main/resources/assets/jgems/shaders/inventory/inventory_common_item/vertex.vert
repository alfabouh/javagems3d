layout (location=0) in vec3 aPosition;
layout (location=1) in vec2 aTexture;

layout (std140, binding = 2) uniform Misc {
    float w_tick;
};

out vec2 texture_coordinates;
uniform mat4 model_matrix;
uniform mat4 projection_matrix;

void main()
{
    vec4 m_pos = model_matrix * vec4(aPosition, 1.0f);
    gl_Position = projection_matrix * m_pos;
    texture_coordinates = aTexture;
}
