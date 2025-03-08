package javagems3d.system.resources.assets.initialization;

import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import org.joml.Vector3f;

import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.LiquidRenderData;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.models.helper.constructor.IEntityModelConstructor;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.resources.managing.JGemsResourceManager;

public class RenderDataInitializer implements IAssetsInitializer {
    public EntityRenderData entityCube;
    public EntityRenderData defaultPlayer;
    public EntityRenderData ground;
    public LiquidRenderData water;

    public RenderDataInitializer() {
    }

    public static void setDefaultRenderTableValues() {
        JGemsShaderManager DEFAULT_SCENE_SHADER = JGemsResourceManager.globalShaderAssets.world_gbuffer;
        JGemsShaderManager DEFAULT_SUN_L_SHADOW_MAP_SHADER = JGemsResourceManager.globalShaderAssets.depth_sun;
        JGemsShaderManager DEFAULT_POINT_L_SHADOW_MAP_SHADER = JGemsResourceManager.globalShaderAssets.depth_plight;
        JGemsShaderManager DEFAULT_TRANSPARENCY_SHADER = JGemsResourceManager.globalShaderAssets.weighted_oit;

        JGemsShaderManager DEFAULT_SCENE_SHADER_IND = JGemsResourceManager.globalShaderAssets.world_gbuffer_indirect;
        JGemsShaderManager DEFAULT_SUN_L_SHADOW_MAP_SHADER_IND = JGemsResourceManager.globalShaderAssets.depth_sun_indirect;
        JGemsShaderManager DEFAULT_POINT_L_SHADOW_MAP_SHADER_IND = JGemsResourceManager.globalShaderAssets.depth_plight;
        JGemsShaderManager DEFAULT_TRANSPARENCY_SHADER_IND = JGemsResourceManager.globalShaderAssets.weighted_oit_indirect;

        RenderTable.SET_DEFAULT_SHADERS_DIRECT(DEFAULT_SCENE_SHADER, DEFAULT_SUN_L_SHADOW_MAP_SHADER, DEFAULT_POINT_L_SHADOW_MAP_SHADER, DEFAULT_TRANSPARENCY_SHADER);
        RenderTable.SET_DEFAULT_SHADERS_INDIRECT(DEFAULT_SCENE_SHADER_IND, DEFAULT_SUN_L_SHADOW_MAP_SHADER_IND, DEFAULT_POINT_L_SHADOW_MAP_SHADER_IND, DEFAULT_TRANSPARENCY_SHADER_IND);
    }

    @Override
    public void load(SystemResources systemResources) {
        IEntityModelConstructor<WorldItem> itemPickUpModelConstructor = e -> {
            MeshGroup meshGroup = new MeshGroup(new MeshNode3D<RenderMesh>(MeshHelper.generateSimplePlane3DMesh(new Vector3f(-0.5f, -0.5f, 0.0f), new Vector3f(0.5f, -0.5f, 0.0f), new Vector3f(-0.5f, 0.5f, 0.0f), new Vector3f(0.5f, 0.5f, 0.0f))));
            return meshGroup;
        };

        //this.zippo_world.getObjectRenderSettings().setOverlappingMaterial(zwMat);

        this.water = new LiquidRenderData(new Material(JGemsResourceManager.globalTextureAssets.waterTexture).setOpacity(0.5f), JGemsResourceManager.globalShaderAssets.weighted_liquid_oit);
        this.entityCube = new EntityRenderData(EntityRenderData.defaultObjectConstructor(), RenderAttributes.get(RenderTable.getDefaultIndirect())).setMeshDataGroup(JGemsResourceManager.globalModelAssets.grassCube); //TODO
        this.defaultPlayer = new EntityRenderData(EntityRenderData.defaultObjectConstructor(), null);
        this.ground = new EntityRenderData(EntityRenderData.defaultObjectConstructor(), RenderAttributes.get(RenderTable.getDefaultIndirect()).setAlphaDiscardValue(0.25f));
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
