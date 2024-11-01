package javagems3d.graphics.opengl.rendering.items;

import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.properties.ModelRenderData;

public interface IModeled extends IAnimated {
    Model<Format3D> getModel();
    ModelRenderData getMeshRenderData();
    void updateAnimation();

    default boolean hasModel() {
        return this.getModel() != null && this.getModel().isValid();
    }
    boolean isVisible();
}
