package workbench.resources.initialization;

import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.managing.resources.SystemResources;

public class RenderDataInitializer implements IAssetsInitializer {
   // public EntityRenderData entityCube;
    public RenderDataInitializer() {
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
