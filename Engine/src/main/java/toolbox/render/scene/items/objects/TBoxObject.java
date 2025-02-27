package toolbox.render.scene.items.objects;

import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import org.jetbrains.annotations.NotNull;

import logger.SystemLogging;
import javagems3d.temp.map_sys.save.objects.object_attributes.AttributesContainer;
import toolbox.render.scene.items.objects.base.TBoxAbstractObject;
import toolbox.render.scene.items.renderers.data.TBoxObjectRenderData;

public final class TBoxObject extends TBoxAbstractObject {
    public TBoxObject(@NotNull String name, @NotNull TBoxObjectRenderData renderData, @NotNull Model3D model) {
        super(name, renderData, model);
    }

    @Override
    public TBoxObject copy() {
        SystemLogging.get().getLogManager().trace("Copied " + this);

        TBoxObject tBoxObject = new TBoxObject(this.objectId(), this.getRenderData(), new Model3D(this.getModel()));
        tBoxObject.setAttributeContainer(new AttributesContainer(this.getAttributeContainer()));
        return tBoxObject;
    }
}