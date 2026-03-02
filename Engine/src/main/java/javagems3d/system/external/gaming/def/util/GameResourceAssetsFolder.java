package javagems3d.system.external.gaming.def.util;

import javagems3d.system.external.gaming.def.IAsset;
import javagems3d.system.service.files.AbstractObjectsFolder;
import org.jetbrains.annotations.NotNull;

public class GameResourceAssetsFolder<T extends IAsset> extends AbstractObjectsFolder<T> {
    public GameResourceAssetsFolder(@NotNull String name) {
        super(name);
    }
}
