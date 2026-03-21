package api.scripting.coding.env.internal.util.resources.init;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.JSMeshBuffer;
import api.scripting.coding.env.internal.util.resources.instances.shaders.JSShader;
import api.scripting.coding.env.internal.util.resources.instances.sound.JSOggSound;
import api.scripting.coding.env.internal.util.resources.instances.textures.JSTexture2D;
import javagems3d.system.resources.managing.JGemsResourceManager;

@JSCodingClass(binding = "JSDefaultGameResources", description = "Default engine resources")
public final class JSDefaultGameResources {
    public static JSShader gui_image_selectable;
    public static JSShader gui_image;
    public static JSShader gui_button;
    public static JSShader gui_text;
    public static JSShader debug;
    public static JSShader skybox;
    public static JSShader world_gbuffer;
    public static JSShader world_deferred;
    public static JSShader simple;
    public static JSTexture2D crosshair;
    public static JSTexture2D gui1;
    public static JSTexture2D zippo1;
    public static JSTexture2D zippo1_1;
    public static JSTexture2D zippo2;
    public static JSOggSound zippo_o;
    public static JSOggSound zippo_c;
    public static JSOggSound pick;
    public static JSOggSound button;
    public static JSOggSound[] pl_step;
    public static JSMeshBuffer grassCube;

    @JSHideFromDoc
    public static void init() {
        gui_image_selectable = new JSShader(JGemsResourceManager.globalShaderAssets.gui_image_selectable);
        gui_image = new JSShader(JGemsResourceManager.globalShaderAssets.gui_image);
        gui_button = new JSShader(JGemsResourceManager.globalShaderAssets.gui_button);
        gui_text = new JSShader(JGemsResourceManager.globalShaderAssets.gui_text);
        debug = new JSShader(JGemsResourceManager.globalShaderAssets.debug);
        skybox = new JSShader(JGemsResourceManager.globalShaderAssets.skybox);
        world_gbuffer = new JSShader(JGemsResourceManager.globalShaderAssets.world_gbuffer);
        world_deferred = new JSShader(JGemsResourceManager.globalShaderAssets.world_deferred);
        simple = new JSShader(JGemsResourceManager.globalShaderAssets.simple);

        crosshair = new JSTexture2D(JGemsResourceManager.globalTextureAssets.crosshair);
        gui1 = new JSTexture2D(JGemsResourceManager.globalTextureAssets.gui1);
        zippo1 = new JSTexture2D(JGemsResourceManager.globalTextureAssets.zippo1);
        zippo1_1 = new JSTexture2D(JGemsResourceManager.globalTextureAssets.zippo1_1);
        zippo2 = new JSTexture2D(JGemsResourceManager.globalTextureAssets.zippo2);

        zippo_o = new JSOggSound(JGemsResourceManager.globalSoundAssets.zippo_o);
        zippo_c = new JSOggSound(JGemsResourceManager.globalSoundAssets.zippo_c);
        pick = new JSOggSound(JGemsResourceManager.globalSoundAssets.pick);
        button = new JSOggSound(JGemsResourceManager.globalSoundAssets.button);

        pl_step = new JSOggSound[JGemsResourceManager.globalSoundAssets.pl_step.length];
        for (int i = 0; i < pl_step.length; i++) {
            pl_step[i] = new JSOggSound(JGemsResourceManager.globalSoundAssets.pl_step[i]);
        }

        grassCube = new JSMeshBuffer(JGemsResourceManager.globalModelAssets.grassCube);
    }

    private JSDefaultGameResources() {}
}