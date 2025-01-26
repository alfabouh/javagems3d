/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.system.resources.assets.initialization;

import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.mesh.structures.MeshGroup;
import org.joml.Vector3f;
import javagems3d.JGems3D;

import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.LiquidRenderData;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.models.helper.constructor.IEntityModelConstructor;
import javagems3d.system.resources.managing.resources.GameResources;
import javagems3d.system.resources.managing.JGemsResourceManager;

public class RenderDataInitializer implements IAssetsInitializer {
    public EntityRenderData entityCube;
    public EntityRenderData player;
    public EntityRenderData ground;
    public LiquidRenderData water;

    public RenderDataInitializer() {
    }

    @Override
    public void load(GameResources gameResources) {
        JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Building render data...");
        IEntityModelConstructor<WorldItem> itemPickUpModelConstructor = e -> {
            MeshGroup meshGroup = new MeshGroup(new MeshGroup.MeshGroupNode(MeshHelper.generateSimplePlane3DMesh(new Vector3f(-0.5f, -0.5f, 0.0f), new Vector3f(0.5f, -0.5f, 0.0f), new Vector3f(-0.5f, 0.5f, 0.0f), new Vector3f(0.5f, 0.5f, 0.0f))));
            return meshGroup;
        };

        //this.zippo_world.getObjectRenderSettings().setOverlappingMaterial(zwMat);

        this.water = new LiquidRenderData(new Material(JGemsResourceManager.globalTextureAssets.waterTexture).setFullOpacity(0.5f), JGemsResourceManager.globalShaderAssets.weighted_liquid_oit);
        this.entityCube = new EntityRenderData(EntityRenderData.defaultObjectConstructor(), RenderAttributes.get()).setMeshDataGroup(JGemsResourceManager.globalModelAssets.grassCube); //TODO
        this.player = new EntityRenderData(EntityRenderData.defaultObjectConstructor(), null);
        this.ground = new EntityRenderData(EntityRenderData.defaultObjectConstructor(), RenderAttributes.get().setAlphaDiscardValue(0.25f));
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
