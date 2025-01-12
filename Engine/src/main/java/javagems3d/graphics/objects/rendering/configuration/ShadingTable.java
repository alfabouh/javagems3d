package javagems3d.graphics.objects.rendering.configuration;

import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsNullException;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class ShadingTable {
    public static JGemsShaderManager DEFAULT_SCENE_SHADER = JGemsResourceManager.globalShaderAssets.world_gbuffer;
    public static JGemsShaderManager DEFAULT_SUN_L_SHADOW_MAP_SHADER = JGemsResourceManager.globalShaderAssets.depth_sun;
    public static JGemsShaderManager DEFAULT_POINT_L_SHADOW_MAP_SHADER = JGemsResourceManager.globalShaderAssets.depth_plight;
    public static JGemsShaderManager DEFAULT_TRANSPARENCY_SHADER = JGemsResourceManager.globalShaderAssets.weighted_oit;

    public static JGemsShaderManager DEFAULT_SCENE_SHADER_IND = JGemsResourceManager.globalShaderAssets.world_gbuffer_indirect;
    public static JGemsShaderManager DEFAULT_SUN_L_SHADOW_MAP_SHADER_IND = JGemsResourceManager.globalShaderAssets.depth_sun_indirect;
    public static JGemsShaderManager DEFAULT_POINT_L_SHADOW_MAP_SHADER_IND = JGemsResourceManager.globalShaderAssets.depth_plight;
    public static JGemsShaderManager DEFAULT_TRANSPARENCY_SHADER_IND = JGemsResourceManager.globalShaderAssets.weighted_oit;

    public static final ShadingTable DEFAULT_SHADING_TABLE = new ShadingTable(Stage.DEFERRED_DIRECT,
            new Pair<>(Category.SCENE, ShadingTable.DEFAULT_SCENE_SHADER),
            new Pair<>(Category.TRANSPARENCY, ShadingTable.DEFAULT_TRANSPARENCY_SHADER),
            new Pair<>(Category.SUN_L_SHADOW_MAP, ShadingTable.DEFAULT_SUN_L_SHADOW_MAP_SHADER),
            new Pair<>(Category.POINT_L_SHADOW_MAP, ShadingTable.DEFAULT_POINT_L_SHADOW_MAP_SHADER));

    public static final ShadingTable DEFAULT_SHADING_TABLE_INDIRECT = new ShadingTable(Stage.DEFERRED_INDIRECT,
            new Pair<>(Category.SCENE, ShadingTable.DEFAULT_SCENE_SHADER_IND),
            new Pair<>(Category.TRANSPARENCY, ShadingTable.DEFAULT_TRANSPARENCY_SHADER_IND),
            new Pair<>(Category.SUN_L_SHADOW_MAP, ShadingTable.DEFAULT_SUN_L_SHADOW_MAP_SHADER_IND),
            new Pair<>(Category.POINT_L_SHADOW_MAP, ShadingTable.DEFAULT_POINT_L_SHADOW_MAP_SHADER_IND));

    private final Stage renderingSceneShaderTarget;
    private final Map<Category, JGemsShaderManager> enumMap;

    @SafeVarargs
    public ShadingTable(@NotNull Stage renderingSceneShaderTarget, Pair<Category, JGemsShaderManager>... pairs) {
        this.renderingSceneShaderTarget = renderingSceneShaderTarget;
        this.enumMap = new EnumMap<>(Category.class);
        for (Pair<Category, JGemsShaderManager> p : pairs) {
            this.setMatch(p.getFirst(), p.getSecond());
        }
        if (this.enumMap.containsValue(null)) {
            throw new JGemsNullException("Shading table contains NULL value!");
        }
    }

    public ShadingTable(@NotNull JGemsShaderManager sceneShader, @NotNull Stage renderingSceneShaderTarget) {
        this.enumMap = new EnumMap<>(Category.class);
        this.renderingSceneShaderTarget = renderingSceneShaderTarget;
        this.setMatch(Category.SCENE, sceneShader);
        this.setDefaults();
    }

    protected void setDefaults() {
        this.setMatch(Category.SUN_L_SHADOW_MAP, ShadingTable.DEFAULT_SUN_L_SHADOW_MAP_SHADER);
        this.setMatch(Category.POINT_L_SHADOW_MAP, ShadingTable.DEFAULT_POINT_L_SHADOW_MAP_SHADER);
        this.setMatch(Category.TRANSPARENCY, ShadingTable.DEFAULT_TRANSPARENCY_SHADER);
    }

    @SuppressWarnings("all")
    public ShadingTable setMatch(Category category, @NotNull JGemsShaderManager shaderManager) {
        this.getEnumMap().put(category, shaderManager);
        return this;
    }

    public @NotNull JGemsShaderManager getShader(Category category) {
        return this.getEnumMap().get(category);
    }

    public Stage getRenderingSceneShaderTarget() {
        return this.renderingSceneShaderTarget;
    }

    public Map<Category, JGemsShaderManager> getEnumMap() {
        return this.enumMap;
    }

    public enum Category  {
        SCENE,
        SUN_L_SHADOW_MAP,
        POINT_L_SHADOW_MAP,
        TRANSPARENCY
    }

    public enum Stage {
        FORWARD,
        DEFERRED_DIRECT,
        DEFERRED_INDIRECT
    }
}
