package javagems3d.graphics.objects.rendering.configuration;

import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class ShadingTable {
    public static JGemsShaderManager DEFAULT_SUN_L_SHADOW_MAP_SHADER = JGemsResourceManager.globalShaderAssets.depth_sun;
    public static JGemsShaderManager DEFAULT_POINT_L_SHADOW_MAP_SHADER = JGemsResourceManager.globalShaderAssets.depth_plight;
    public static JGemsShaderManager DEFAULT_TRANSPARENCY_SHADER = JGemsResourceManager.globalShaderAssets.weighted_oit;

    private final Stage renderingSceneShaderTarget;
    private final Map<Category, JGemsShaderManager> enumMap;

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

    public void setMatch(Category category, @NotNull JGemsShaderManager shaderManager) {
        this.getEnumMap().put(category, shaderManager);
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
