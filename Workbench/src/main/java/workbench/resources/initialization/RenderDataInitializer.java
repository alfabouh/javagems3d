package workbench.resources.initialization;

import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.resources.managing.resources.SystemResources;
import workbench.resources.WBenchResourceManager;

public class RenderDataInitializer implements IAssetsInitializer {
   // public EntityRenderData entityCube;
    public RenderDataInitializer() {
    }

    public static void setDefaultRenderTableValues() {
     JGemsShaderManager DEFAULT_SCENE_SHADER = WBenchResourceManager.localShaderAssets.world_gbuffer;
     JGemsShaderManager DEFAULT_SUN_L_SHADOW_MAP_SHADER = WBenchResourceManager.localShaderAssets.depth_sun;
     JGemsShaderManager DEFAULT_POINT_L_SHADOW_MAP_SHADER = WBenchResourceManager.localShaderAssets.depth_plight;
     JGemsShaderManager DEFAULT_TRANSPARENCY_SHADER = WBenchResourceManager.localShaderAssets.weighted_oit;
     JGemsShaderManager DEFAULT_SCENE_SHADER_IND = WBenchResourceManager.localShaderAssets.world_gbuffer_indirect;
     JGemsShaderManager DEFAULT_SUN_L_SHADOW_MAP_SHADER_IND = WBenchResourceManager.localShaderAssets.depth_sun_indirect;
     JGemsShaderManager DEFAULT_POINT_L_SHADOW_MAP_SHADER_IND = WBenchResourceManager.localShaderAssets.depth_plight;
     JGemsShaderManager DEFAULT_TRANSPARENCY_SHADER_IND = WBenchResourceManager.localShaderAssets.weighted_oit_indirect;

     RenderTable.SET_DEFAULT_SHADERS_DIRECT(DEFAULT_SCENE_SHADER, DEFAULT_SUN_L_SHADOW_MAP_SHADER, DEFAULT_POINT_L_SHADOW_MAP_SHADER, DEFAULT_TRANSPARENCY_SHADER);
     RenderTable.SET_DEFAULT_SHADERS_INDIRECT(DEFAULT_SCENE_SHADER_IND, DEFAULT_SUN_L_SHADOW_MAP_SHADER_IND, DEFAULT_POINT_L_SHADOW_MAP_SHADER_IND, DEFAULT_TRANSPARENCY_SHADER_IND);
    }

    @Override
    public void load(SystemResources systemResources) {
    //    this.entityCube = new EntityRenderData(EntityRenderData.defaultObjectConstructor(), RenderAttributes.get(RenderTable.getDefaultIndirect())).setMeshDataGroup(WBenchResourceManager.globalModelAssets.defaultCube_bff);
    }

    @Override
    public LaunchMode loadMode() {
        return LaunchMode.REGULAR;
    }

    @Override
    public LoadPriority loadPriority() {
        return LoadPriority.LOW;
    }

}
