layout (location=0) in vec2 position;
layout (location=1) in vec2 texture;
layout (location=2) in vec4 color;

out vec2 out_texture;
out vec4 out_color;
uniform vec2 scale;

void main()
{
    out_texture = texture;
    out_color = color;

    gl_Position = vec4(position * scale + vec2(-1.0, 1.0), 0.0f, 1.0);
}
