package workbench.project.managing.instances.group;

import javagems3d.system.service.collections.AbstractObjectsFolder;
import org.jetbrains.annotations.NotNull;
import workbench.project.managing.instances.IAsset;

public class GameResourceAssetsFolder<T extends IAsset> extends AbstractObjectsFolder<T> {
    public GameResourceAssetsFolder(@NotNull String name) {
        super(name);
    }
}
