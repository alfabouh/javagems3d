package javagems3d.graphics.objects;

import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;

public interface IModeled extends IAnimated {
    Model<Format3D> getModel();
    void updateAnimation();

    default boolean hasModel() {
        return this.getModel() != null && this.getModel().isValid();
    }

    boolean isVisible();
}
