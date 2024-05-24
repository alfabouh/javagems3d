in vec2 out_texture;
layout (location = 0) out vec4 frag_color;

uniform sampler2D texture_sampler;
uniform sampler2D texture_sampler_gui;
uniform sampler2D texture_sampler_inventory;
uniform sampler2D texture_screen;
uniform sampler2D texture_blood;
uniform int e_lsd;
uniform int kill;
uniform int victory;
uniform int glitch_tick;
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

vec2 curveUV(vec2 inVec, float factor) {
    vec2 curveUV = inVec;
    curveUV = curveUV * 2.0 - 1.0;

    float dist = length(curveUV);
    float curve = (1.0 + factor * 1.5) - dist * dist * factor;

    curveUV *= curve;
    curveUV = curveUV * 0.5 + 0.5;

    return curveUV;
}

vec4 psx() {
    vec2 texIn = glitch_tick > 0 ? 1.0 - out_texture : out_texture;

    const float panic_val = (victory == 1 || kill == 1) ? 0.05 : panic + 0.05;

    float pixelSize = e_lsd == 0 ? 0.008 : 0.004;
    float pixelSize2 = 0.004;
    vec2 offres = vec2(offset / textureSize(texture_sampler, 0));
    vec2 texCoords = (texIn / (1.0 - offres)) - offres / 2.0;

    vec2 pixelCoords = floor(texCoords / pixelSize) * pixelSize;
    vec2 pixelCoords2 = floor(texCoords / pixelSize2) * pixelSize2;

    vec2 distortedCoord = curveUV(pixelCoords, panic_val * 0.5);

    const float C1 = w_tick * 10.0 + panic_val;
    const float C2 = panic_val * 0.1;

    distortedCoord.x += sin(pixelCoords.y * 8.0) * sin(C1) * C2;
    distortedCoord.y += sin(pixelCoords.x * 16.0) * cos(C1) * C2;

    vec2 distortedCoordGui = curveUV(texCoords, panic_val * 0.5);
    distortedCoordGui.x += sin(pixelCoords.y * 8.0) * sin(C1) * C2;
    distortedCoordGui.y += sin(pixelCoords.x * 16.0) * cos(C1) * C2;

    vec4 screen_over = texture(texture_screen, texCoords * vec2(5.0));

    vec4 blood_over = kill == 1 ? texture(texture_blood, distortedCoord) : vec4(0.0);

    vec4 gui_t1 = texture(texture_sampler_gui, distortedCoordGui);
    vec4 gui_t2 = texture(texture_sampler_gui, distortedCoordGui + vec2(panic_val * 0.01));
    gui_t2.g *= 0;
    gui_t2.b *= 0;
    vec4 gui_t3 = texture(texture_sampler_gui, distortedCoordGui - vec2(panic_val * 0.01));
    gui_t3.r *= 0;
    gui_t3.g *= 0;
    vec4 t2 = mix(gui_t1, gui_t2 + gui_t3, 1.0 - gui_t1.a);

    vec2 distortedCoordInventory = curveUV(pixelCoords2, panic_val * 0.5);
    distortedCoordInventory.x += sin(pixelCoords2.y * 8.0) * sin(C1) * C2;
    distortedCoordInventory.y += sin(pixelCoords2.x * 16.0) * cos(C1) * C2;

    vec4 inv_t = texture(texture_sampler_inventory, distortedCoordInventory);
    inv_t.r = texture(texture_sampler_inventory, distortedCoordInventory + vec2((0.002 + panic * 0.01))).r;
    inv_t.b = texture(texture_sampler_inventory, distortedCoordInventory - vec2((0.002 + panic * 0.01))).b;
    vec4 t3 = inv_t;

    vec3 color = texture(texture_sampler, distortedCoord).rgb;
    vec4 res = vec4(color * (vec3(screen_over) * 0.5 + 0.5), 1.0);

    vec4 vic = victory == 1 ? vec4(w_tick * 0.5) : vec4(0.);

    vec4 result = mix(mix(crt(lsd(res)), t2, t2.a), t3, t3.a);
    result = glitch_tick > 0 ? 1.0 - result : result;
    return vinnette(random_noise(result), texCoords) + blood_over + vic;
}

vec4 vinnette(vec4 txt, vec2 textCoords) {
    vec2 center = vec2(0.5, 0.5);
    float dist = length(textCoords - center);
    float factor = smoothstep(1.0, 0.0, dist * (panic * 1.5));
    return txt * (vec4(vec3(factor), 1.0));
}

vec4 lsd(vec4 txt) {
    vec2 texCoords = out_texture;
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
    vec3 color = txt.rgb;
    float sharpness = 0.25;
    color = pow(color, vec3(sharpness));
    return vec4(color * 0.5, 1.0);
}

vec4 random_noise(vec4 txtr) {
    float pixelSize = 0.0035;
    vec2 tex = out_texture;
    vec2 pixelCoords = floor(tex / pixelSize) * pixelSize;
    vec4 colors = txtr;
    float grain = clamp(rand(pixelCoords) * (0.04 + panic * 0.125), 0.0, 1.0);
    return txtr + (glitch_tick > 0 ? grain * 2.0f : grain);
}