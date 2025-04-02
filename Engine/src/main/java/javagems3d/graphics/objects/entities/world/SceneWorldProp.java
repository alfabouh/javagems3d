package javagems3d.graphics.objects.entities.world;

import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.rendering.constructors.IModelConstructor;
import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.graphics.world.SceneWorld;

import javagems3d.physics.world.IWorld;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import logger.Log;
import org.jetbrains.annotations.NotNull;

public class SceneWorldProp extends SceneProp {
    private final IModelConstructor<Void> propModelConstructor;

    public SceneWorldProp(@NotNull SceneWorld sceneWorld, @NotNull PropRenderData propRenderData) {
        super(sceneWorld, new Model3D(new Pose3D(), propRenderData.getMeshDataGroup()), propRenderData.getObjectRenderAttributes());
        this.propModelConstructor = propRenderData.getPropModelConstructor();
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        Log.get().trace("[ " + this + " ]" + " - PreRender");
        if (this.canBeRendered()) {
            if (!this.hasModel() && this.getPropModelConstructor() != null) {
                this.setModel(new Model3D(new Pose3D(), this.getPropModelConstructor().constructMeshDataGroup(null)));
            }
            this.getRenderFabricsSet().forEach(e -> e.createResources(this));
        }
    }

    public IModelConstructor<Void> getPropModelConstructor() {
        return this.propModelConstructor;
    }
}
