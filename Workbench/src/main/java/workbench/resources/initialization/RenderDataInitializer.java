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

package workbench.resources.initialization;

import javagems3d.JGems3D;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.LiquidRenderData;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.models.helper.constructor.IEntityModelConstructor;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.resources.managing.resources.GameResources;
import org.joml.Vector3f;
import workbench.resources.WBenchResourceManager;

public class RenderDataInitializer implements IAssetsInitializer {
    public EntityRenderData entityCube;
    public RenderDataInitializer() {
    }

    @Override
    public void load(GameResources gameResources) {
        this.entityCube = new EntityRenderData(EntityRenderData.defaultObjectConstructor(), RenderAttributes.get()).setMeshDataGroup(WBenchResourceManager.globalModelAssets.defaultCube_bff);
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
