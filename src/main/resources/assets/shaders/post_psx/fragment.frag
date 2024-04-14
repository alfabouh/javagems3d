in vec2 out_texture;
layout (location = 0) out vec4 frag_color;

uniform sampler2D texture_sampler;
uniform sampler2D texture_sampler_gui;
uniform sampler2D texture_sampler_inventory;
uniform sampler2D texture_screen;
uniform sampler2D texture_blood;
uniform int e_lsd;
uniform int psx_gui_shake;
uniform int kill;
uniform int victory;
uniform vec2 resolution;
uniform float panic;
uniform float offset;

vec4 random_noise(vec4);
vec4 psx();
vec4 crt(vec4);
vec4 lsd(vec4);
vec4 vinnette(vec4, vec2);

layout (std140, binding = 2) uniform Misc {
    float w_tick;
};

float rand(vec2 co)
{
    return fract(sin(dot(co.xy + tan(w_tick), vec2(12.9898, 78.233))) * 43758.5453);
}

void main()
{
    frag_color = psx() * vec4(vec3(min(w_tick, 1.0)), 1.0);
}

vec4 psx() {
    float pixelSize = e_lsd == 0 ? 0.008 : 0.004;
    float pixelSize2 = 0.002;
    float panic_val = kill == 1 ? 1.5 : panic + 0.1;

    vec2 texCoords = (gl_FragCoord.xy - (offset / 2.0)) / (resolution - offset);

    vec2 pixelCoords = floor(texCoords / pixelSize) * pixelSize;
    vec2 pixelCoords2 = floor(texCoords / pixelSize2) * pixelSize2;

    vec2 distortedCoord = pixelCoords;
    distortedCoord.x += sin(pixelCoords.y * 8.0) * sin(w_tick * panic_val * 50.0) * (panic_val * 0.1);
    distortedCoord.y += sin(pixelCoords.x * 16.0) * cos(w_tick * panic_val * 50.0) * (panic_val * 0.1);

    vec2 distortedCoordGui = texCoords;
    distortedCoordGui.x += (sin(pixelCoords.y * 8.0) * sin(w_tick * panic_val * 50.0) * (panic_val * 0.1));
    distortedCoordGui.y += (sin(pixelCoords.x * 16.0) * cos(w_tick * panic_val * 50.0) * (panic_val * 0.1));

    vec4 screen_over = texture(texture_screen, texCoords * vec2(5.0));
    vec4 blood_over = kill == 1 ? texture(texture_blood, distortedCoord) : vec4(0.0);

    vec4 gui_t1 = texture(texture_sampler_gui, psx_gui_shake == 1 ? distortedCoordGui : gl_FragCoord.xy / resolution);
    vec4 gui_t2 = texture(texture_sampler_gui, distortedCoordGui + vec2(panic_val * 0.01, panic_val * 0.01));
    gui_t2.g *= 0;
    gui_t2.b *= 0;
    vec4 gui_t3 = texture(texture_sampler_gui, distortedCoordGui - vec2(panic_val * 0.01, panic_val * 0.01));
    gui_t3.r *= 0;
    gui_t3.g *= 0;
    vec4 t2 = psx_gui_shake == 1 ? mix(gui_t1, gui_t2 + gui_t3, 1.0 - gui_t1.a) : gui_t1;

    vec2 distortedCoordInventory = pixelCoords2;
    distortedCoordInventory.x += sin(pixelCoords2.y * 8.0) * sin(w_tick * panic_val * 50.0) * (panic_val * 0.1);
    distortedCoordInventory.y += sin(pixelCoords2.x * 16.0) * cos(w_tick * panic_val * 50.0) * (panic_val * 0.1);

    vec4 inv_t = texture(texture_sampler_inventory, distortedCoordInventory);
    inv_t.r = texture(texture_sampler_inventory, distortedCoordInventory + vec2((0.002 + panic * 0.01), (0.002 + panic * 0.01))).r;
    inv_t.b = texture(texture_sampler_inventory, distortedCoordInventory - vec2((0.002 + panic * 0.01), (0.002 + panic * 0.01))).b;
    vec4 t3 = inv_t;

    vec3 color = texture(texture_sampler, distortedCoord).rgb;
    vec4 res = vec4(color * (vec3(screen_over) * 0.5 + 0.5), 1.0);

    vec4 vic = victory == 1 ? vec4(w_tick * 0.5) : vec4(0.);
    return vinnette(random_noise(mix(mix(crt(lsd(res)), t2, t2.a), t3, t3.a)), texCoords) + blood_over + vic;
}

vec4 vinnette(vec4 txt, vec2 textCoords) {
    vec2 center = vec2(0.5, 0.5);
    float dist = length(textCoords - center);
    float factor = smoothstep(1.0, 0.0, dist * (panic * 1.5));
    return txt * (vec4(vec3(factor), 1.0));
}

vec4 lsd(vec4 txt) {
    vec2 texCoords = gl_FragCoord.xy / textureSize(texture_sampler, 0);
    vec3 color = txt.rgb;

    const int pallette = 8;

    vec3 palette[pallette];

    palette[0] = vec3(0.1, 0.1, 0.1);
    palette[4] = vec3(0.3, 0.3, 0.3);
    palette[2] = vec3(0.5, 0.5, 0.5);
    palette[3] = vec3(0.9, 0.9, 0.9);
    palette[1] = vec3(1.0, 0.75, 0.80);
    palette[5] = vec3(0.9, 0.5, 0.6);
    palette[6] = vec3(0.8, 0.3, 0.4);
    palette[7] = vec3(0.65, 0.45, 0.35);

    float grayScale = dot(color, vec3(0.299, 0.587, 0.114));
    int paletteIndex = clamp(int((grayScale * float(pallette)) + 0.5), 0, pallette);
    color = palette[paletteIndex];

    return e_lsd == 0 ? txt : vec4(color, 1.0);
}

vec4 crt(vec4 txt) {
    vec2 texCoord = gl_FragCoord.xy / resolution;
    vec3 color = txt.rgb;
    float sharpness = 0.5;
    color = pow(color, vec3(sharpness));
    return vec4(color * sharpness, 1.0);
}

vec4 random_noise(vec4 txtr) {
    float pixelSize = 0.0035;
    vec2 tex = gl_FragCoord.xy / resolution;
    vec2 pixelCoords = floor(tex / pixelSize) * pixelSize;
    vec4 colors = txtr;
    float grain = clamp(rand(pixelCoords) * (0.035 + panic * 0.1), 0.0, 1.0);
    return txtr + grain;
}