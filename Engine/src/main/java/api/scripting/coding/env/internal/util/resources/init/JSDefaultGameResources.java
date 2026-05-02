package api.scripting.coding.env.internal.util.resources.init;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSGlobalVarFactory;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.instances.font.JSFont;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.JSMeshBuffer;
import api.scripting.coding.env.internal.util.resources.instances.shaders.JSShader;
import api.scripting.coding.env.internal.util.resources.instances.sound.JSOggSound;
import api.scripting.coding.env.internal.util.resources.instances.textures.JSTexture2D;
import javagems3d.system.resources.managing.JGemsResourceManager;

@JSCodingClass(binding = "JSDefaultGameResources", description = "DefaultPhysTest engine resources")
public final class JSDefaultGameResources implements JSGlobalVarFactory<JSDefaultGameResources> {
    @JSHideFromDoc public static JSDefaultGameResources jsDefaultGameResources = new JSDefaultGameResources();



    // --- SHADERS ---
    @JSCodingField(description = "Alpha scanning compute shader")
    public JSShader alpha_scanning;

    @JSCodingField(description = "Debug shader")
    public JSShader debug;

    @JSCodingField(description = "GUI text shader")
    public JSShader gui_text;

    @JSCodingField(description = "GUI noised shader")
    public JSShader gui_noised;

    @JSCodingField(description = "GUI button shader")
    public JSShader gui_button;

    @JSCodingField(description = "GUI image shader")
    public JSShader gui_image;

    @JSCodingField(description = "GUI selectable image shader")
    public JSShader gui_image_selectable;

    @JSCodingField(description = "SSAO blur shader")
    public JSShader blur_ssao;

    @JSCodingField(description = "Blur 5 shader")
    public JSShader blur5;

    @JSCodingField(description = "Blur 9 shader")
    public JSShader blur9;

    @JSCodingField(description = "Blur 13 shader")
    public JSShader blur13;

    @JSCodingField(description = "Box blur shader")
    public JSShader blur_box;

    @JSCodingField(description = "ImGui shader")
    public JSShader imgui;

    @JSCodingField(description = "FXAA shader")
    public JSShader fxaa;

    @JSCodingField(description = "HDR shader")
    public JSShader hdr;

    @JSCodingField(description = "Scene gluing shader")
    public JSShader scene_gluing;

    @JSCodingField(description = "Skybox shader")
    public JSShader skybox;

    @JSCodingField(description = "Background shader")
    public JSShader background;

    @JSCodingField(description = "Background indirect shader")
    public JSShader background_indirect;

    @JSCodingField(description = "Screen SSAO compute shader")
    public JSShader world_ssao;

    @JSCodingField(description = "Weighted liquid OIT shader")
    public JSShader weighted_liquid_oit;

    @JSCodingField(description = "Weighted OIT shader")
    public JSShader weighted_oit;

    @JSCodingField(description = "Weighted OIT indirect shader")
    public JSShader weighted_oit_indirect;

    @JSCodingField(description = "World gbuffer shader")
    public JSShader world_gbuffer;

    @JSCodingField(description = "World gbuffer indirect shader")
    public JSShader world_gbuffer_indirect;

    @JSCodingField(description = "World deferred shader")
    public JSShader world_deferred;

    @JSCodingField(description = "Menu shader")
    public JSShader menu;

    @JSCodingField(description = "Simple gbuffer shader")
    public JSShader simple_gbuffer;

    @JSCodingField(description = "Simple shader")
    public JSShader simple;

    @JSCodingField(description = "Sun depth shader")
    public JSShader depth_sun;

    @JSCodingField(description = "Sun depth indirect shader")
    public JSShader depth_sun_indirect;

    @JSCodingField(description = "Point light depth shader")
    public JSShader depth_plight;


    // --- TEXTURES ---
    @JSCodingField(description = "Crosshair texture")
    public JSTexture2D crosshair;

    @JSCodingField(description = "GUI base texture")
    public JSTexture2D gui1;

    @JSCodingField(description = "Zippo texture 1")
    public JSTexture2D zippo1;

    @JSCodingField(description = "Zippo texture 1_1")
    public JSTexture2D zippo1_1;

    @JSCodingField(description = "Zippo texture 2")
    public JSTexture2D zippo2;


    // --- SOUNDS ---
    @JSCodingField(description = "Zippo open sound")
    public JSOggSound zippo_o;

    @JSCodingField(description = "Zippo close sound")
    public JSOggSound zippo_c;

    @JSCodingField(description = "Pick sound")
    public JSOggSound pick;

    @JSCodingField(description = "Button sound")
    public JSOggSound button;

    @JSCodingField(description = "Player step sounds")
    public JSOggSound[] pl_step;


    // --- MODELS ---
    @JSCodingField(description = "Grass cube mesh")
    public JSMeshBuffer grassCube;


    // --- FONTS ---

    @JSCodingField(description = "DefaultPhysTest font")
    public JSFont buttonFont;

    @JSHideFromDoc
    public static void init() {
        JSDefaultGameResources.jsDefaultGameResources.alpha_scanning = new JSShader(JGemsResourceManager.globalShaderAssets.alpha_scanning);
        JSDefaultGameResources.jsDefaultGameResources.debug = new JSShader(JGemsResourceManager.globalShaderAssets.debug);
        JSDefaultGameResources.jsDefaultGameResources.gui_text = new JSShader(JGemsResourceManager.globalShaderAssets.gui_text);
        JSDefaultGameResources.jsDefaultGameResources.gui_noised = new JSShader(JGemsResourceManager.globalShaderAssets.gui_noised);
        JSDefaultGameResources.jsDefaultGameResources.gui_button = new JSShader(JGemsResourceManager.globalShaderAssets.gui_button);
        JSDefaultGameResources.jsDefaultGameResources.gui_image = new JSShader(JGemsResourceManager.globalShaderAssets.gui_image);
        JSDefaultGameResources.jsDefaultGameResources.gui_image_selectable = new JSShader(JGemsResourceManager.globalShaderAssets.gui_image_selectable);
        JSDefaultGameResources.jsDefaultGameResources.blur_ssao = new JSShader(JGemsResourceManager.globalShaderAssets.blur_ssao);
        JSDefaultGameResources.jsDefaultGameResources.blur5 = new JSShader(JGemsResourceManager.globalShaderAssets.blur5);
        JSDefaultGameResources.jsDefaultGameResources.blur9 = new JSShader(JGemsResourceManager.globalShaderAssets.blur9);
        JSDefaultGameResources.jsDefaultGameResources.blur13 = new JSShader(JGemsResourceManager.globalShaderAssets.blur13);
        JSDefaultGameResources.jsDefaultGameResources.blur_box = new JSShader(JGemsResourceManager.globalShaderAssets.blur_box);
        JSDefaultGameResources.jsDefaultGameResources.imgui = new JSShader(JGemsResourceManager.globalShaderAssets.imgui);
        JSDefaultGameResources.jsDefaultGameResources.fxaa = new JSShader(JGemsResourceManager.globalShaderAssets.fxaa);
        JSDefaultGameResources.jsDefaultGameResources.hdr = new JSShader(JGemsResourceManager.globalShaderAssets.hdr);
        JSDefaultGameResources.jsDefaultGameResources.scene_gluing = new JSShader(JGemsResourceManager.globalShaderAssets.scene_gluing);
        JSDefaultGameResources.jsDefaultGameResources.skybox = new JSShader(JGemsResourceManager.globalShaderAssets.skybox);
        JSDefaultGameResources.jsDefaultGameResources.background = new JSShader(JGemsResourceManager.globalShaderAssets.background);
        JSDefaultGameResources.jsDefaultGameResources.background_indirect = new JSShader(JGemsResourceManager.globalShaderAssets.background_indirect);
        JSDefaultGameResources.jsDefaultGameResources.world_ssao = new JSShader(JGemsResourceManager.globalShaderAssets.world_ssao);
        JSDefaultGameResources.jsDefaultGameResources.weighted_liquid_oit = new JSShader(JGemsResourceManager.globalShaderAssets.weighted_liquid_oit);
        JSDefaultGameResources.jsDefaultGameResources.weighted_oit = new JSShader(JGemsResourceManager.globalShaderAssets.weighted_oit);
        JSDefaultGameResources.jsDefaultGameResources.weighted_oit_indirect = new JSShader(JGemsResourceManager.globalShaderAssets.weighted_oit_indirect);
        JSDefaultGameResources.jsDefaultGameResources.world_gbuffer = new JSShader(JGemsResourceManager.globalShaderAssets.world_gbuffer);
        JSDefaultGameResources.jsDefaultGameResources.world_gbuffer_indirect = new JSShader(JGemsResourceManager.globalShaderAssets.world_gbuffer_indirect);
        JSDefaultGameResources.jsDefaultGameResources.world_deferred = new JSShader(JGemsResourceManager.globalShaderAssets.world_deferred);
        JSDefaultGameResources.jsDefaultGameResources.menu = new JSShader(JGemsResourceManager.globalShaderAssets.menu);
        JSDefaultGameResources.jsDefaultGameResources.simple_gbuffer = new JSShader(JGemsResourceManager.globalShaderAssets.simple_gbuffer);
        JSDefaultGameResources.jsDefaultGameResources.simple = new JSShader(JGemsResourceManager.globalShaderAssets.simple);
        JSDefaultGameResources.jsDefaultGameResources.depth_sun = new JSShader(JGemsResourceManager.globalShaderAssets.depth_sun);
        JSDefaultGameResources.jsDefaultGameResources.depth_sun_indirect = new JSShader(JGemsResourceManager.globalShaderAssets.depth_sun_indirect);
        JSDefaultGameResources.jsDefaultGameResources.depth_plight = new JSShader(JGemsResourceManager.globalShaderAssets.depth_plight);

        JSDefaultGameResources.jsDefaultGameResources.crosshair = new JSTexture2D(JGemsResourceManager.globalTextureAssets.crosshair);
        JSDefaultGameResources.jsDefaultGameResources.gui1 = new JSTexture2D(JGemsResourceManager.globalTextureAssets.gui1);
        JSDefaultGameResources.jsDefaultGameResources.zippo1 = new JSTexture2D(JGemsResourceManager.globalTextureAssets.zippo1);
        JSDefaultGameResources.jsDefaultGameResources.zippo1_1 = new JSTexture2D(JGemsResourceManager.globalTextureAssets.zippo1_1);
        JSDefaultGameResources.jsDefaultGameResources.zippo2 = new JSTexture2D(JGemsResourceManager.globalTextureAssets.zippo2);

        JSDefaultGameResources.jsDefaultGameResources.zippo_o = new JSOggSound(JGemsResourceManager.globalSoundAssets.zippo_o);
        JSDefaultGameResources.jsDefaultGameResources.zippo_c = new JSOggSound(JGemsResourceManager.globalSoundAssets.zippo_c);
        JSDefaultGameResources.jsDefaultGameResources.pick = new JSOggSound(JGemsResourceManager.globalSoundAssets.pick);
        JSDefaultGameResources.jsDefaultGameResources.button = new JSOggSound(JGemsResourceManager.globalSoundAssets.button);

        JSDefaultGameResources.jsDefaultGameResources.pl_step = new JSOggSound[JGemsResourceManager.globalSoundAssets.pl_step.length];
        for (int i = 0; i < JSDefaultGameResources.jsDefaultGameResources.pl_step.length; i++) {
            JSDefaultGameResources.jsDefaultGameResources.pl_step[i] = new JSOggSound(JGemsResourceManager.globalSoundAssets.pl_step[i]);
        }

        JSDefaultGameResources.jsDefaultGameResources.grassCube = new JSMeshBuffer(JGemsResourceManager.globalModelAssets.grassCube);

        JSDefaultGameResources.jsDefaultGameResources.buttonFont = new JSFont(JGemsResourceManager.globalTextureAssets.buttonFont);
    }

    @JSHideFromDoc
    public JSDefaultGameResources() {}

    @JSHideFromDoc
    @Override
    public JSDefaultGameResources newGlobalVar() {
        return JSDefaultGameResources.jsDefaultGameResources;
    }

    @JSHideFromDoc
    @Override
    public String getVarName() {
        return "Js_DefaultResources";
    }
}